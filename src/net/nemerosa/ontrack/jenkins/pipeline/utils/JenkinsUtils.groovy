package net.nemerosa.ontrack.jenkins.pipeline.utils

import com.cloudbees.workflow.flownode.FlowNodeUtil
import com.cloudbees.workflow.rest.external.StatusExt
import hudson.model.Cause
import hudson.model.Result
import hudson.model.Run
import hudson.triggers.SCMTrigger
import org.apache.commons.lang.StringUtils
import org.jenkinsci.plugins.workflow.actions.BodyInvocationAction
import org.jenkinsci.plugins.workflow.actions.TimingAction
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.graph.FlowNode
import org.jenkinsci.plugins.workflow.graph.StepNode
import org.jenkinsci.plugins.workflow.steps.StepDescriptor

class JenkinsUtils {

    static String getValidationRunStatusFromStage(def dsl) {
        // Gets the current node
        FlowNode flowNode = dsl.getContext FlowNode
        if (flowNode != null) {
            // Gets the current stage
            FlowNode stage = getStage(flowNode)
            // If there is a stage, gets its status and converts it
            if (stage != null) {
                Result result = getStageStatusAsResult(stage)
                return toValidationRunStatus(result)
            }
        }
        // No node not stage, takes the current build status as a fallback
        return getValidationRunStatusFromRun(dsl)
    }

    static String getValidationRunStatusFromRun(def dsl) throws IOException, InterruptedException {
        Run run = dsl.getContext Run
        if (run != null) {
            Result result = run.getResult()
            return toValidationRunStatus(result)
        } else {
            throw new IllegalStateException("Cannot get any status when not running in a build.")
        }
    }

    static RunInfo getRunInfo(def dsl) throws IOException, InterruptedException {
        // Gets the associated run
        Run run = dsl.getContext Run
        if (run == null) {
            // No run? Getting away from there...
            return null
        }

        // Base run info
        RunInfo runInfo = new RunInfo()
        runInfo.sourceType = 'jenkins'
        runInfo.sourceUri = dsl.env.JOB_URL as String

        // Trigger info
        List<Cause> causes = run.getCauses()
        if (!causes.isEmpty()) {
            Cause cause = causes.get(0)
            if (cause instanceof SCMTrigger.SCMTriggerCause) {
                runInfo.triggerType = "scm"
                String git_commit = dsl.env.GIT_COMMIT
                if (StringUtils.isNotBlank(git_commit)) {
                    runInfo.triggerData = git_commit
                } else {
                    String svn_revision = dsl.env.SVN_REVISION
                    if (StringUtils.isNotBlank(svn_revision)) {
                        runInfo.triggerData = svn_revision
                    } else {
                        runInfo.triggerData = ""
                    }
                }
            } else if (cause instanceof Cause.UserIdCause) {
                runInfo.triggerType = "user"
                runInfo.triggerData = ((Cause.UserIdCause) cause).getUserId()
            }
        }

        // Gets the duration of this build
        long durationMs = run.getDuration()
        if (durationMs > 0) {
            runInfo.runTime = durationMs / 1000L;
        } else {
            runInfo.runTime = (System.currentTimeMillis() - run.getStartTimeInMillis()) / 1000L;
        }

        // Adaptation
        adaptRunInfo(dsl, runInfo)
        // Run info if not empty
        if (runInfo.isEmpty()) {
            return null
        } else {
            return runInfo
        }
    }

    private static void adaptRunInfo(def dsl, RunInfo runInfo) {
        // Gets the (current) duration of the stage
        FlowNode flowNode = dsl.getContext FlowNode
        if (flowNode != null) {
            Long durationMilliSeconds = getTiming(flowNode)
            if (durationMilliSeconds != null) {
                runInfo.runTime = durationMilliSeconds / 1000
            }
        }
    }

    private static Long getTiming(FlowNode node) {
        Long runTime = getExecutionTimeMs(node)
        if (node instanceof StepNode) {
            StepNode stepNode = (StepNode) node
            StepDescriptor stepDescriptor = stepNode.getDescriptor()
            if (stepDescriptor != null) {
                String stepDescriptorId = stepDescriptor.getId()
                if ("org.jenkinsci.plugins.workflow.support.steps.StageStep" == stepDescriptorId) {
                    if (runTime != null) return runTime
                } else if ("org.jenkinsci.plugins.workflow.support.steps.ExecutorStep" == stepDescriptorId) {
                    BodyInvocationAction bodyInvocationAction = node.getAction(BodyInvocationAction.class)
                    if (bodyInvocationAction != null && runTime != null) {
                        return runTime
                    }
                }
            }
        }
        return getTiming(node.getParents())
    }

    private static Long getExecutionTimeMs(FlowNode node) {
        TimingAction timingAction = node.getAction(TimingAction.class)
        if (timingAction != null) {
            long startTime = timingAction.getStartTime()
            return (System.currentTimeMillis() - startTime)
        } else {
            return null
        }
    }

    private static Long getTiming(List<FlowNode> nodes) {
        for (FlowNode node : nodes) {
            Long durationMilliSeconds = getTiming(node)
            if (durationMilliSeconds != null) {
                return durationMilliSeconds
            }
        }
        return null
    }

    private static String toValidationRunStatus(Result result) {
        if (result == null || result == Result.SUCCESS) {
            return "PASSED"
        } else if (result == Result.UNSTABLE) {
            return "WARNING"
        } else if (result == Result.FAILURE) {
            return "FAILED"
        } else if (result == Result.ABORTED) {
            return "INTERRUPTED"
        } else {
            return null
        }
    }

    private static Result getStageStatusAsResult(FlowNode node) {
        Result current = toResult(FlowNodeUtil.getStatus(node))
        List<FlowNode> otherNodes = FlowNodeUtil.getStageNodes(node)
        for (FlowNode otherNode : otherNodes) {
            Result other = toResult(FlowNodeUtil.getStatus(otherNode))
            if (current == null || (other != null && other.isWorseThan(current))) {
                current = other
            }
        }
        return current
    }

    private static Result toResult(StatusExt statusExt) {
        if (statusExt == null) {
            return null
        } else {
            switch (statusExt) {
                case StatusExt.NOT_EXECUTED:
                    return null
                case StatusExt.ABORTED:
                    return Result.ABORTED
                case StatusExt.SUCCESS:
                    return Result.SUCCESS
                case StatusExt.IN_PROGRESS:
                    // Still running - means still OK
                    return Result.SUCCESS
                case StatusExt.PAUSED_PENDING_INPUT:
                    return null
                case StatusExt.FAILED:
                    return Result.FAILURE
                case StatusExt.UNSTABLE:
                    return Result.UNSTABLE
                default:
                    return null
            }
        }
    }

    private static FlowNode getStage(FlowNode node) {
        if (node instanceof StepStartNode) {
            StepStartNode stepNode = (StepStartNode) node
            StepDescriptor stepDescriptor = stepNode.getDescriptor()
            if (stepDescriptor != null) {
                String stepDescriptorId = stepDescriptor.getId()
                if ("org.jenkinsci.plugins.workflow.support.steps.StageStep" == stepDescriptorId) {
                    return stepNode
                }
            }
        }
        return getStage(node.getParents())
    }

    private static FlowNode getStage(List<FlowNode> nodes) {
        for (FlowNode node : nodes) {
            FlowNode stage = getStage(node)
            if (stage != null) {
                return stage
            }
        }
        return null
    }

}

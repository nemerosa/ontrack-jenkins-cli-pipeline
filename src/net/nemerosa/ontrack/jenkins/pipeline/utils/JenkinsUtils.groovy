package net.nemerosa.ontrack.jenkins.pipeline.utils

import com.cloudbees.workflow.flownode.FlowNodeUtil
import com.cloudbees.workflow.rest.external.StatusExt
import hudson.model.Result
import hudson.model.Run
import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.graph.FlowNode
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

    public static String getValidationRunStatusFromRun(def dsl) throws IOException, InterruptedException {
        Run run = dsl.getContext Run
        if (run != null) {
            Result result = run.getResult();
            return toValidationRunStatus(result);
        } else {
            throw new IllegalStateException("Cannot get any status when not running in a build.");
        }
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

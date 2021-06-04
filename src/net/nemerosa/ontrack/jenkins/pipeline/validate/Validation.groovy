package net.nemerosa.ontrack.jenkins.pipeline.validate

import net.nemerosa.ontrack.jenkins.pipeline.utils.JenkinsUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.RunInfo

class Validation {

    private final String name

    Validation(String name) {
        this.name = name
    }

    Map<String,?> variables(def dsl, Map<String, ?> params, boolean computeStatusWhenMissing) {
        String project = ParamUtils.getParam(params, "project", dsl.env.ONTRACK_PROJECT_NAME as String)
        String branch = ParamUtils.getParam(params, "branch", dsl.env.ONTRACK_BRANCH_NAME as String)
        String build = ParamUtils.getParam(params, "build", dsl.env.ONTRACK_BUILD_NAME as String)
        String stamp = ParamUtils.getParam(params, "stamp")
        String description = ParamUtils.getConditionalParam(params, "description", false, '')
        String status = ParamUtils.getConditionalParam(params, "status", false, null)
        boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
        boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)

        Closure logger = {}
        if (logging) {
            logger = { msg ->
                println("[$name] $msg")
            }
        }

        Closure tracer = {}
        if (logging && tracing) {
            tracer = logger
        }

        // Base variables
        Map<String,?> variables = [
                project: project,
                branch: branch,
                build: build,
                validation: stamp,
                description: description,
        ]

        // Computing the status if needed
        String actualStatus = status
        if (!actualStatus) {
            if (computeStatusWhenMissing) {
                logger("No status is provided, computing status...")
                actualStatus = JenkinsUtils.getValidationRunStatusFromStage(dsl)
                logger("Computed status: $actualStatus")
            }
        }

        // Setting up the status
        if (actualStatus) {
            variables.status = actualStatus
        }

        // Run info
        RunInfo runInfo = JenkinsUtils.getRunInfo(dsl, tracer)
        tracer("Run info = ${runInfo.toString()}")
        if (runInfo != null && !runInfo.isEmpty()) {
            Map<String,?> runInfoVariables = [:]
            variables.runInfo = runInfoVariables
            if (runInfo.runTime != null) {
                runInfoVariables.runTime = runInfo.runTime
            }
            if (runInfo.sourceType != null) {
                runInfoVariables.sourceType = runInfo.sourceType
            }
            if (runInfo.sourceUri != null) {
                runInfoVariables.sourceUri = runInfo.sourceUri
            }
            if (runInfo.triggerType != null) {
                runInfoVariables.triggerType = runInfo.triggerType
            }
            if (runInfo.triggerData != null) {
                runInfoVariables.triggerData = runInfo.triggerData
            }
        }

        // OK
        return variables
    }

    @Deprecated
    List<String> cli(def dsl, Map<String, ?> params, boolean computeStatusWhenMissing) {
        String project = ParamUtils.getParam(params, "project", dsl.env.ONTRACK_PROJECT_NAME as String)
        String branch = ParamUtils.getParam(params, "branch", dsl.env.ONTRACK_BRANCH_NAME as String)
        String build = ParamUtils.getParam(params, "build", dsl.env.ONTRACK_BUILD_NAME as String)
        String stamp = ParamUtils.getParam(params, "stamp")
        String status = ParamUtils.getConditionalParam(params, "status", false, null)
        boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
        boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)

        Closure logger = {}
        if (logging) {
            logger = { msg ->
                println("[$name] $msg")
            }
        }

        Closure tracer = {}
        if (logging && tracing) {
            tracer = logger
        }

        // Base arguments
        List<String> args = ['validate', '--project', project, '--branch', branch, '--build', build, '--validation', stamp]

        // Computing the status if needed
        String actualStatus = status
        if (!actualStatus) {
            if (computeStatusWhenMissing) {
                logger("No status is provided, computing status...")
                actualStatus = JenkinsUtils.getValidationRunStatusFromStage(dsl)
                logger("Computed status: $actualStatus")
            }
        }

        // Setting up the status
        if (actualStatus) {
            args += '--status'
            args += actualStatus
        }

        // Run info
        RunInfo runInfo = JenkinsUtils.getRunInfo(dsl, tracer)
        tracer("Run info = ${runInfo.toString()}")
        if (runInfo != null && !runInfo.isEmpty()) {
            if (runInfo.runTime != null) {
                args += '--run-time'
                args += runInfo.runTime
            }
            if (runInfo.sourceType != null) {
                args += '--source-type'
                args += runInfo.sourceType
            }
            if (runInfo.sourceUri != null) {
                args += '--source-uri'
                args += runInfo.sourceUri
            }
            if (runInfo.triggerType != null) {
                args += '--trigger-type'
                args += runInfo.triggerType
            }
            if (runInfo.triggerData != null) {
                args += '--trigger-data'
                args += runInfo.triggerData
            }
        }

        // OK
        return args
    }
}

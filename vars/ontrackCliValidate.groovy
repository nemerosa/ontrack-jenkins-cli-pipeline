import net.nemerosa.ontrack.jenkins.pipeline.utils.JenkinsUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.RunInfo
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

import net.sf.json.JSONSerializer

def call(Map<String, ?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    String stamp = ParamUtils.getParam(params, "stamp")
    String status = ParamUtils.getConditionalParam(params, "status", false, null)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)

    String dataType = ParamUtils.getConditionalParam(params, "dataType", false, null)
    boolean dataValidation = ParamUtils.getBooleanParam(params, "dataValidation", true)
    Object data = params.data

    Closure logger = {}
    if (logging) {
        logger = { msg ->
            println("[ontrack-cli-validate] $msg")
        }
    }

    Closure tracer = {}
    if (logging && tracing) {
        tracer = logger
    }

    List<String> args = ['validate', '--project', project, '--branch', branch, '--build', build, '--validation', stamp]

    // Computing the status if needed
    String actualStatus = status
    if (!actualStatus) {
        if (!dataType || !dataValidation) {
            logger("No status is provided, computing status...")
            actualStatus = JenkinsUtils.getValidationRunStatusFromStage(this)
            logger("Computed status: $actualStatus")
        }
    }

    // Setting up the status
    if (actualStatus) {
        args += '--status'
        args += actualStatus
    }

    // Run info
    RunInfo runInfo = JenkinsUtils.getRunInfo(this, tracer)
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

    // Data

    if (dataType) {
        if (!data) throw new RuntimeException("dataType is provided but data is missing.")
        args += '--data-type'
        args += dataType
        String dataJson = JSONSerializer.toJSON(data)
        args += '--data'
        args += "'$dataJson'".toString()
    }

    // Actual CLI call with all arguments

    Cli.call(this, logger, args)

}

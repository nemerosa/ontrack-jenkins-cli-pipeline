import net.nemerosa.ontrack.jenkins.pipeline.utils.JsonUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    String dataType = ParamUtils.getConditionalParam(params, "dataType", false, null)
    boolean dataValidation = ParamUtils.getBooleanParam(params, "dataValidation", true)
    Object data = params.data

    boolean computeStatusWhenMissing = !dataType || !dataValidation

    Validation validation = new Validation("ontrack-cli-validate")
    List<String> args = validation.cli(this, params, computeStatusWhenMissing)

    // Data
    if (dataType) {
        if (!data) throw new RuntimeException("dataType is provided but data is missing.")
        args += '--data-type'
        args += dataType
        String dataJson = JsonUtils.toJSON(data)
        args += '--data'
        args += "'$dataJson'".toString()
    }

    // Actual CLI call with all arguments

    Cli.call(this, logging, args)

}

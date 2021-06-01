import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    int value = ParamUtils.getIntParam(params, "value", 0)

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate-percentage")
    List<String> args = validation.cli(this, params, false)

    // CHML values
    args += ['percentage', '--value', value]

    // Calling the CLI
    Cli.call(this, logging, args)
}

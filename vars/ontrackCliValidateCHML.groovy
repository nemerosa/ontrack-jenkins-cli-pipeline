import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    int critical = ParamUtils.getIntParam(params, "critical", 0)
    int high = ParamUtils.getIntParam(params, "high", 0)
    int medium = ParamUtils.getIntParam(params, "medium", 0)
    int low = ParamUtils.getIntParam(params, "low", 0)

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate-chml")
    List<String> args = validation.cli(this, params, false)

    // CHML values
    args += ['chml', '--critical', critical, '--high', high, '--medium', medium, '--low', low]

    // Calling the CLI
    Cli.call(this, logging, args)
}

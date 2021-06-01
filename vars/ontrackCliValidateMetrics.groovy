import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    Map<String, Double> metrics = params.metrics as Map<String, Double>
    if (!metrics) throw new RuntimeException("Missing metrics")

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate-metrics")
    List<String> args = validation.cli(this, params, false)

    // Metrics
    args += 'metrics'
    metrics.each { name, value ->
        args += '--metric'
        args += "$name=$value"
    }

    // Calling the CLI
    Cli.call(this, logging, args)
}

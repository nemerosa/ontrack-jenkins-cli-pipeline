import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {

    String url = ParamUtils.getParam(params, 'url', env.ONTRACK_URL as String)
    String credentialsId = ParamUtils.getParam(params, "credentialsId", 'ONTRACK_TOKEN')
    String name = ParamUtils.getParam(params, "name", "prod")

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-connect] $it")
        }
    }

    withCredentials([string(credentialsId: credentialsId, variable: 'ONTRACK_TOKEN')]) {
        Cli.call(this, logger, "config", "create", name, url, "--token", env.ONTRACK_TOKEN, "--override")
    }
}
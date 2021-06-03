import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {

    String url = ParamUtils.getParam(params, 'url', env.ONTRACK_URL as String)
    String credentialsId = ParamUtils.getParam(params, "credentialsId", 'ONTRACK_TOKEN')
    String name = ParamUtils.getParam(params, "name", "prod")

    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)

    withCredentials([string(credentialsId: credentialsId, variable: 'ONTRACK_TOKEN')]) {
        if (logging && tracing) {
            sh 'ls -l $ONTRACK_CLI_DIR'
        }
        env.ONTRACK_CLI_URL = url
        env.ONTRACK_CLI_TOKEN = env.ONTRACK_TOKEN
        Cli.call(this, logging, "config", "create", name, url, "--token", env.ONTRACK_TOKEN, "--override")
    }
}
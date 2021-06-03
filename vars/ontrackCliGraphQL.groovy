import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String,?> params = [:]) {
    String query = ParamUtils.getParam(params, "query")
    def variables = params.variables
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-graphql] $it")
        }
    }

    String url = env.ONTRACK_CLI_URL as String
    String token = env.ONTRACK_CLI_TOKEN as String

    return new GraphQL(url, token, logger).call(query, variables)

}
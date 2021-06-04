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

    // Ontrack URL

    String url = params.url
    if (!url) {
        url = env.ONTRACK_URL as String
        if (!url) {
            throw new RuntimeException("Missing ONTRACK_URL environment variable")
        }
    }

    // Explicit Ontrack token

    String token = params.token
    if (!token) {
        token = env.ONTRACK_TOKEN as String
    }

    // Call with explicit token
    if (token) {
        return new GraphQL(url, token, logger).call(query, variables)
    }
    // Call with token in credentials
    else {
        String tokenId = env.ONTRACK_TOKEN_ID as String ?: 'ONTRACK_TOKEN'
        withCredentials([string(credentialsId: tokenId, variable: 'ONTRACK_TOKEN')]) {
            return new GraphQL(url, env.ONTRACK_TOKEN, logger).call(query, variables)
        }
    }

}
package net.nemerosa.ontrack.jenkins.pipeline.graphql

import net.nemerosa.ontrack.jenkins.pipeline.utils.JsonUtils
import net.sf.json.JSON
import net.sf.json.JSONSerializer

class GraphQL {

    private final String url
    private final String token
    private final Closure logger

    GraphQL(String url, String token, Closure logger) {
        this.url = url
        this.token = token
        this.logger = logger
    }

    JSON call(String query, Map<String,?> variables) {
        logger("URL = $url")
        logger("Query = $query")
        logger("Variables = $variables")
        def graphQLURL = new URL("$url/graphql")
        def connection = graphQLURL.openConnection() as HttpURLConnection
        return connection.with {
            doInput = true
            doOutput = true
            requestMethod = 'POST'
            setRequestProperty('X-Ontrack-Token', token)
            setRequestProperty('Content-Type', 'application/json')
            // Payload
            def payload = [
                    query: query,
            ]
            if (variables) {
                payload.variables = variables
            }
            // JSON representation
            def jsonPayload = JsonUtils.toJSON(payload)
            // Logging
            logger("Payload = $jsonPayload")
            // Body
            outputStream.withWriter { writer ->
                writer << jsonPayload
            }
            // Gets the response as text
            def jsonResponse = content.text
            // Parsing
            JSONSerializer.toJSON(jsonPayload)
        }
    }
}

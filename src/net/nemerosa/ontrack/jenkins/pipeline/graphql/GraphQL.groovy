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
        return connection.with { con ->
            con.doInput = true
            con.doOutput = true
            con.requestMethod = 'POST'
            con.setRequestProperty('X-Ontrack-Token', token)
            con.setRequestProperty('Content-Type', 'application/json')
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
            con.outputStream.withWriter { writer ->
                writer << jsonPayload
            }
            // Gets the response as text
            def jsonResponse = con.content.text
            // Logging
            logger("Raw response = $jsonResponse)")
            // Parsing
            def response = JSONSerializer.toJSON(jsonResponse)
            logger("Response = $response)")
            // TODO Management of errors
            // OK
            response
        }
    }
}

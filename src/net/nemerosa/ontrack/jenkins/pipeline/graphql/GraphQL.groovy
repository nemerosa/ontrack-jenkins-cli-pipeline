package net.nemerosa.ontrack.jenkins.pipeline.graphql

import net.nemerosa.ontrack.jenkins.pipeline.utils.JsonUtils
import net.sf.json.JSON
import net.sf.json.JSONSerializer

class GraphQL {

    private final String url
    private final String token
    private final Closure logger
    private final boolean ignoreErrors

    GraphQL(String url, String token, Closure logger, boolean ignoreErrors) {
        this.url = url
        this.token = token
        this.logger = logger
        this.ignoreErrors = ignoreErrors
    }

    JSON call(String query, Map<String, ?> variables) {
        logger("URL = $url")
        logger("Query = $query")
        logger("Variables = $variables")
        logger("Ignoring errors = $ignoreErrors")
        def graphQLURL = new URL("$url/graphql")
        try {
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
                con.outputStream.write(jsonPayload.bytes)
                // Gets the response code
                def code = con.responseCode
                if (code != 200) {
                    throw new RuntimeException("GraphQL HTTP $code error: ${con.responseMessage}")
                }
                // Gets the response as text
                def jsonResponse = con.inputStream.text
                // Logging
                logger("Response = $jsonResponse)")
                // Parsing
                def response = JSONSerializer.toJSON(jsonResponse)
                // Management of errors
                def errors = response.errors
                if (errors) {
                    String message = errors.collect { it.message }.join('\n')
                    throw new RuntimeException("GraphQL errors:\n$message")
                }
                // OK
                response
            }
        } catch (Exception ex) {
            if (ignoreErrors) {
                println("Error while connecting to Ontrack: ${ex.message}. This error has been ignored and an empty response is returned.")
                return [:] // Empty object
            } else {
                throw ex
            }
        }
    }

    /**
     * Checks for errors in the GraphQL payload.
     * @param response GraphQL payload, containing the `data` node
     * @param nodeName Name of the "business" node which contains the actual payload (and may contain an `errors` collection)
     * @param ignoreErrors True if no exception must be thrown
     * @return True if NO error is detected, False if at least one error is present (False is returned only when ignoreErrors is true)
     */
    static boolean checkForMutationErrors(def response, String nodeName, boolean ignoreErrors = false) {
        if (response && response.data) {
            def node = response.data[nodeName]
            if (node != null && node.errors && node.errors.isArray()) {
                String message = node.errors.collect { it.message }.join('\n')
                if (ignoreErrors) {
                    return false
                } else {
                    throw new RuntimeException("$nodeName mutation returns some errors:\n$message")
                }
            } else {
                return true
            }
        } else {
            return true
        }
    }
}

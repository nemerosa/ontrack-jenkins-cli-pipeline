package net.nemerosa.ontrack.jenkins.pipeline.graphql

import net.nemerosa.ontrack.jenkins.pipeline.utils.JsonUtils
import groovy.json.JsonSlurper

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

    Object call(String query, Map<String, ?> variables) {
        logger("URL = $url")
        logger("Query = $query")
        logger("Variables = $variables")
        logger("Ignoring errors = $ignoreErrors")
        // Payload (built once, reused across redirects)
        def payload = [query: query]
        if (variables) {
            payload.variables = variables
        }
        def jsonPayload = JsonUtils.toJSON(payload)
        logger("Payload = $jsonPayload")
        def targetURL = new URL("$url/graphql")
        int redirectCount = 0
        try {
            while (true) {
                def connection = targetURL.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.doInput = true
                connection.doOutput = true
                connection.requestMethod = 'POST'
                connection.setRequestProperty('X-Ontrack-Token', token)
                connection.setRequestProperty('Content-Type', 'application/json')
                connection.outputStream.write(jsonPayload.bytes)
                // Gets the response code
                def code = connection.responseCode
                logger("HTTP Code = $code)")
                // Follow redirects manually (HttpURLConnection does not follow them for POST)
                if (code in [301, 302, 307, 308] && redirectCount < 5) {
                    def location = connection.getHeaderField('Location')
                    logger("Redirect ($code) to $location")
                    targetURL = new URL(location)
                    redirectCount++
                    continue
                }
                // Gets the response as text
                def jsonResponse
                if (code >= 400) {
                    // For error responses, read from errorStream
                    jsonResponse = connection.errorStream?.text ?: "No error response body"
                } else {
                    jsonResponse = connection.inputStream.text
                }
                // Logging
                logger("Response = $jsonResponse)")
                // Error mgt
                if (code != 200) {
                    throw new RuntimeException("GraphQL HTTP $code error: ${connection.responseMessage}")
                }
                // Parsing
                def response = new JsonSlurper().parseText(jsonResponse)
                // Management of errors
                def errors = response.errors
                if (errors) {
                    String message = errors.collect { it.message }.join('\n')
                    throw new RuntimeException("GraphQL errors:\n$message")
                }
                // OK
                return response
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
            if (node != null && node.errors && node.errors instanceof List) {
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

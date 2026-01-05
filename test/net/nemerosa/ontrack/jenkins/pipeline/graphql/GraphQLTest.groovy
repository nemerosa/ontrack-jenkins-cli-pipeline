package net.nemerosa.ontrack.jenkins.pipeline.graphql

import groovy.json.JsonSlurper
import org.junit.Test
import static org.junit.Assert.*

class GraphQLTest {

    @Test
    void "JsonSlurper returns Groovy null for JSON null"() {
        def response = new JsonSlurper().parseText('{"data": {"node": null}}')
        assertNull(response.data.node)
        // Safe navigation works
        assertNull(response.data.node?.name)
    }

    @Test
    void "checkForMutationErrors with no errors"() {
        def response = [
            data: [
                myNode: [
                    payload: "some data"
                ]
            ]
        ]
        assertTrue(GraphQL.checkForMutationErrors(response, "myNode"))
    }

    @Test
    void "checkForMutationErrors with errors"() {
        def response = [
            data: [
                myNode: [
                    errors: [
                        [message: "Error 1"],
                        [message: "Error 2"]
                    ]
                ]
            ]
        ]
        try {
            GraphQL.checkForMutationErrors(response, "myNode")
            fail("Should have thrown an exception")
        } catch (RuntimeException ex) {
            assertEquals("myNode mutation returns some errors:\nError 1\nError 2", ex.message)
        }
    }

    @Test
    void "checkForMutationErrors with errors and ignoreErrors"() {
        def response = [
            data: [
                myNode: [
                    errors: [
                        [message: "Error 1"]
                    ]
                ]
            ]
        ]
        assertFalse(GraphQL.checkForMutationErrors(response, "myNode", true))
    }

    @Test
    void "checkForMutationErrors with missing node"() {
        def response = [
            data: [:]
        ]
        assertTrue(GraphQL.checkForMutationErrors(response, "myNode"))
    }
}

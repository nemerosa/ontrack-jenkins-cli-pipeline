package net.nemerosa.ontrack.jenkins.pipeline.graphql

import org.junit.Ignore
import org.junit.Test

class GraphQLTest {

    @Test
    @Ignore
    void 'Getting a project by name'() {
        String query = '''
            query ProjectByName($name: String!) {
                projects(name: $name) {
                    name
                }
            }
        '''
        def variables = [
            name: 'ontrack',
        ]

        String url = System.getenv('ONTRACK_URL')
        assert url: "ONTRACK_URL is required"

        String token = System.getenv('ONTRACK_TOKEN')
        assert token: "ONTRACK_TOKEN is required"

        Closure logger = {
            println("[graphql] $it")
        }

        GraphQL graphQL = new GraphQL(url, token, logger)

        def response = graphQL.call(query, variables)

        assert response.data.projects[0].name == 'ontrack'
    }

}

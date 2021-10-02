package net.nemerosa.ontrack.jenkins.pipeline.build

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

class BuildUtils {

    static void buildLink(def dsl, Map<String,?> params, List<Map<String,String>> links) {
        boolean logging = ParamUtils.getLogging(params, dsl.env.ONTRACK_LOGGING)

        String project = ParamUtils.getParam(params, "project", dsl.env.ONTRACK_PROJECT_NAME as String)
        String build = ParamUtils.getParam(params, "build", dsl.env.ONTRACK_BUILD_NAME as String)

        String query = '''
            mutation LinksBuild(
                $fromProject: String!,
                $fromBuild: String!,
                $links: [LinksBuildInputItem!]!,
            ) {
                linksBuild(input: {
                    fromProject: $fromProject,
                    fromBuild: $fromBuild,
                    links: $links,
                }) {
                    errors {
                        message
                    }
                }
            }
        '''

        Map<String,?> variables = [
                fromProject: project,
                fromBuild: build,
                links: links,
        ]

        // GraphQL call

        def response = dsl.ontrackCliGraphQL(
                logging: logging,
                query: query,
                variables: variables,
        )

        // Checks for errors

        GraphQL.checkForMutationErrors(response, 'linksBuild')

    }

}

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String channel = ParamUtils.getParam(params, "channel")
    def channelConfig = params.channelConfig
    if (!channelConfig) {
        throw new IllegalArgumentException("Missing parameter: channelConfig")
    }
    List<String> events = params.events as List<String> ?: []
    String keywords = params.keywords
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Call
    def response = ontrackCliGraphQL(
            logging: logging,
            variables: [
                    project      : project,
                    branch       : branch,
                    channel      : channel,
                    channelConfig: channelConfig,
                    events       : events,
                    keywords     : keywords,
            ],
            query: '''
                mutation SetBranchSubscriptions(
                    $project: String!,
                    $branch: String!,
                    $channel: String!,
                    $channelConfig: JSON!,
                    $events: [String!]!,
                    $keywords: String,
                ) {
                    subscribeBranchToEvents(input: {
                        project: $project,
                        branch: $branch,
                        channel: $channel,
                        channelConfig: $channelConfig,
                        events: $events,
                        keywords: $keywords,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            '''
    )

    GraphQL.checkForMutationErrors(response, 'subscribeBranchToEvents')
}
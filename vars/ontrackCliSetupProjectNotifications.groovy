import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String name = params.name ?: ''
    String channel = ParamUtils.getParam(params, "channel")
    def channelConfig = params.channelConfig
    if (!channelConfig) {
        throw new IllegalArgumentException("Missing parameter: channelConfig")
    }
    List<String> events = params.events as List<String> ?: []
    String keywords = params.keywords
    String contentTemplate = params.contentTemplate
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Call
    def response = ontrackCliGraphQL(
            logging: logging,
            variables: [
                    name           : name,
                    project        : project,
                    channel        : channel,
                    channelConfig  : channelConfig,
                    events         : events,
                    keywords       : keywords,
                    contentTemplate: contentTemplate,
            ],
            query: '''
                mutation SetProjectSubscriptions(
                    $name: String,
                    $project: String!,
                    $channel: String!,
                    $channelConfig: JSON!,
                    $events: [String!]!,
                    $keywords: String,
                    $contentTemplate: String,
                ) {
                    subscribeProjectToEvents(input: {
                        name: $name,
                        project: $project,
                        channel: $channel,
                        channelConfig: $channelConfig,
                        events: $events,
                        keywords: $keywords,
                        contentTemplate: $contentTemplate,
                    }) {
                        errors {
                            message
                        }
                    }
                }
            '''
    )

    GraphQL.checkForMutationErrors(response, 'subscribeProjectToEvents')
}
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation

def call(Map<String,?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String configuration = ParamUtils.getParam(params, "configuration", 'SonarQube')
    String key = ParamUtils.getParam(params, "key")
    String validationStamp = ParamUtils.getParam(params, "validationStamp", 'sonarqube')
    List<String> measures = params.measures as List<String> ?: []
    boolean override = ParamUtils.getBooleanParam(params, "override", false)
    boolean branchModel = ParamUtils.getBooleanParam(params, "branchModel", false)
    String branchPattern = params.branchPattern as String
    boolean validationMetrics = ParamUtils.getBooleanParam(params, "validationMetrics", true)



    // GraphQL query
    String query = '''
        mutation SetupProjectSonarQubeProperties(
            $project: String!,
            $configuration: String!,
            $key: String!,
            $validationStamp: String!,
            $measures: [String!]!,
            $override: Boolean!,
            $branchModel: Boolean!,
            $branchPattern: String,
            $validationMetrics: Boolean!,
        ) {
            setProjectSonarQubeProperty(input: {
                project: $project,
                configuration: $configuration,
                key: $key,
                validationStamp: $validationStamp,
                measures: $measures,
                override: $override,
                branchModel: $branchModel,
                branchPattern: $branchPattern,
                validationMetrics: $validationMetrics,
            }) {
                errors {
                    message
                }
            }
        }
    '''

    // Parameters
    def variables = [
            project: project,
            configuration: configuration,
            key: key,
            validationStamp: validationStamp,
            measures: measures,
            override: override,
            branchModel: branchModel,
            branchPattern: branchPattern,
            validationMetrics: validationMetrics,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'validateBuildWithTests', ontrackCliIgnoreErrors())
 }
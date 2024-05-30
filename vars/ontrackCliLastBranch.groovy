import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return null

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return ""
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME)
    String pattern = ParamUtils.getParam(params, "pattern", '.*')
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    def response = ontrackCliGraphQL(
            query: '''
                query GetLastBranch($project: String!, $pattern: String!) {
                    branches(project: $project, name: $pattern) {
                        name
                    }
                }
            ''',
            variables: [
                    project: project,
                    pattern: pattern,
            ],
            logging: logging,
    )

    def branches = response.data.branches
    if (branches && branches.size() > 0) {
        return branches[0].name as String
    } else {
        return null
    }
}
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return null

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return [:]
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME)
    String promotion = ParamUtils.getParam(params, "promotion")
    boolean injectEnv = ParamUtils.getBooleanParam(params, 'injectEnv', false)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    def response = ontrackCliGraphQL(
            query: '''
                query GetLastPromotion($project: String!, $branch: String!, $promotion: String!) {
                  builds(project: $project, branch: $branch, buildBranchFilter: {count: 1, withPromotionLevel: $promotion}) {
                    id
                    name
                    branch {
                        name
                    }
                    releaseProperty { value }
                    gitCommitProperty { value }
                  }
                }
            ''',
            variables: [
                    project  : project,
                    branch   : branch,
                    promotion: promotion,
            ],
            logging: logging,
    )

    def builds = response.data.builds
    if (builds && builds.size() > 0) {
        def build = builds[0]
        def release = build.releaseProperty?.value?.name
        def commit = build.gitCommitProperty?.value?.commit

        if (logging) {
            println("[ontrack-cli-last-promotion] Build found for promotion ${promotion}")
            println("[ontrack-cli-last-promotion] Build ID = ${build.id}")
            println("[ontrack-cli-last-promotion] Build branch = ${build.branch.name}")
            println("[ontrack-cli-last-promotion] Build name = ${build.name}")
            println("[ontrack-cli-last-promotion] Build release = ${release}")
            println("[ontrack-cli-last-promotion] Build commit = ${commit}")
        }

        if (injectEnv) {
            env.ONTRACK_BUILD_LAST_PROMOTION = 'true'
            env.ONTRACK_BUILD_ID = build.id as String
            env.ONTRACK_BUILD_BRANCH_NAME = build.branch.name as String
            env.ONTRACK_BUILD_NAME = build.name
            if (release) {
                env.ONTRACK_BUILD_RELEASE = release
            }
            if (commit) {
                env.ONTRACK_BUILD_COMMIT = commit
            }
        }
        return [
                id     : build.id,
                name   : build.name,
                release: release,
                commit : commit,
        ]
    } else {
        if (logging) {
            println("[ontrack-cli-last-promotion] No build found for promotion ${promotion}")
        }
        if (injectEnv) {
            env.ONTRACK_BUILD_LAST_PROMOTION = 'false'
        }
        return null
    }
}
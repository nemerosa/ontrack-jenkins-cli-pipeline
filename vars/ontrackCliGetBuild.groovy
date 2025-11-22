import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return null

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Yontrack for pull requests."
        return [:]
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String buildName = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING as String)

    def response = ontrackCliGraphQL(
            query: '''
                query GetBuild($project: String!, $branch: String!, $build: String!) {
                  builds(project: $project, branch: $branch, build: $build) {
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
                    project: project,
                    branch : branch,
                    build  : buildName,
            ],
            logging: logging,
    )

    def builds = response.data.builds
    if (builds && builds.size() > 0) {
        def build = builds[0]
        def release = build.releaseProperty?.value?.name
        def commit = build.gitCommitProperty?.value?.commit

        if (logging) {
            println("[ontrackCliGetBuild] Build ID = ${build.id}")
            println("[ontrackCliGetBuild] Build branch = ${build.branch.name}")
            println("[ontrackCliGetBuild] Build name = ${build.name}")
            println("[ontrackCliGetBuild] Build release = ${release}")
            println("[ontrackCliGetBuild] Build commit = ${commit}")
        }

        return [
                id     : build.id,
                name   : build.name,
                branch : build.branch.name,
                release: release,
                commit : commit,
        ]
    } else {
        if (logging) {
            println("[ontrackCliGetBuild] No build found")
        }
        return null
    }
}
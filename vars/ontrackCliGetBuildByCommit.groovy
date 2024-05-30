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
    String commit = ParamUtils.getParam(params, "commit")
    boolean injectEnv = ParamUtils.getBooleanParam(params, 'injectEnv', false)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    def response = ontrackCliGraphQL(
            query: '''
                query GetBuildByCommit($project: String!, $branch: String!, $commit: String!) {
                  builds(
                    project: $project
                    branch: $branch
                    buildBranchFilter: {
                        count: 1,
                        withProperty: "net.nemerosa.ontrack.extension.git.property.GitCommitPropertyType",
                        withPropertyValue: $commit
                    }
                  ) {
                    id
                    name
                    releaseProperty {
                      value
                    }
                    gitCommitProperty {
                      value
                    }
                  }
                }
            ''',
            variables: [
                    project: project,
                    branch : branch,
                    commit : commit,
            ],
            logging: logging,
    )

    def builds = response.data.builds
    if (builds && builds.size() > 0) {
        def build = builds[0]
        def release = build.releaseProperty?.value?.name

        if (logging) {
            println("[ontrack-cli-get-build-by-commit] Build found for version ${version}")
            println("[ontrack-cli-get-build-by-commit] Build id = ${build.id}")
            println("[ontrack-cli-get-build-by-commit] Build name = ${build.name}")
            println("[ontrack-cli-get-build-by-commit] Build release = ${release}")
            println("[ontrack-cli-get-build-by-commit] Build commit = ${commit}")
        }

        if (injectEnv) {
            env.ONTRACK_BUILD_ID = build.id
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
            println("[ontrack-cli-get-build-by-commit] No build found for version ${version}")
        }
        return null
    }
}
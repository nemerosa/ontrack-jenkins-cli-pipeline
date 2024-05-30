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
    String version = ParamUtils.getParam(params, "version")
    boolean injectEnv = ParamUtils.getBooleanParam(params, 'injectEnv', false)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    def response = ontrackCliGraphQL(
            query: '''
                query GetBuildByMetaVersion($project: String!, $branch: String!, $version: String!) {
                  builds(project: $project, branch: $branch, buildBranchFilter: {
                    count: 1,
                    withProperty: "net.nemerosa.ontrack.extension.general.MetaInfoPropertyType",
                    withPropertyValue: $version,
                  }) {
                    id
                    name
                    releaseProperty { value }
                    gitCommitProperty { value }
                  }
                }
            ''',
            variables: [
                    project: project,
                    branch : branch,
                    version: version,
            ],
            logging: logging,
    )

    def builds = response.data.builds
    if (builds && builds.size() > 0) {
        def build = builds[0]
        def release = build.releaseProperty?.value?.name
        def commit = build.gitCommitProperty?.value?.commit

        if (logging) {
            println("[ontrack-cli-get-build-by-meta-version] Build found for version ${version}")
            println("[ontrack-cli-get-build-by-meta-version] Build id = ${build.id}")
            println("[ontrack-cli-get-build-by-meta-version] Build name = ${build.name}")
            println("[ontrack-cli-get-build-by-meta-version] Build release = ${release}")
            println("[ontrack-cli-get-build-by-meta-version] Build commit = ${commit}")
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
            println("[ontrack-cli-get-build-by-meta-version] No build found for version ${version}")
        }
        return null
    }
}
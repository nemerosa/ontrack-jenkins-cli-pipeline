import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return null

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return [:]
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME)
    String version = ParamUtils.getParam(params, "version")
    boolean injectEnv = ParamUtils.getBooleanParam(params, 'injectEnv', false)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    def response = ontrackCliGraphQL(
            query: '''
                query GetBuildByProjectAndVersion(
                    $project: String!,
                    $version: String!,
                ) {
                    builds(
                        project: $project,
                        buildProjectFilter: {
                            maximumCount: 1,
                            property: "net.nemerosa.ontrack.extension.general.ReleasePropertyType",
                            propertyValue: $version
                        }
                    ) {
                        id
                        name
                        releaseProperty { value }
                        gitCommitProperty { value }
                        branch {
                            name
                        }
                      }
                }
            ''',
            variables: [
                    project: project,
                    version: version,
            ],
            logging: logging,
    )

    def builds = response.data.builds
    if (builds && builds.size() > 0) {
        def build = builds[0]
        def branch = build.branch.name
        def release = build.releaseProperty?.value?.name
        def commit = build.gitCommitProperty?.value?.commit

        if (logging) {
            println("[ontrack-cli-get-build-by-version] Build found for version ${version}")
            println("[ontrack-cli-get-build-by-version] Build branch = ${branch}")
            println("[ontrack-cli-get-build-by-version] Build id = ${build.id}")
            println("[ontrack-cli-get-build-by-version] Build name = ${build.name}")
            println("[ontrack-cli-get-build-by-version] Build release = ${release}")
            println("[ontrack-cli-get-build-by-version] Build commit = ${commit}")
        }

        if (injectEnv) {
            env.ONTRACK_BRANCH_NAME = branch
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
                branch : branch,
        ]
    } else {
        if (logging) {
            println("[ontrack-cli-get-build-by-version] No build found for version ${version}")
        }
        return null
    }
}
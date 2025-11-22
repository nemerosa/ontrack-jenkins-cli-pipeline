import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return ""

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Yontrack changelog for pull requests."
        return ""
    }

    // Parameters
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)

    String promotion = ParamUtils.getParam(params, "promotion")
    String renderer = ParamUtils.getParam(params, "promotion", "plain")
    def config = params.config

    // Getting the current build ID
    def currentBuild = ontrackCliGetBuild(
            project: project,
            branch: branch,
            build: build,
    )

    // Getting the last promoted build on this branch
    def fromBuild = ontrackCliLastPromotion(
            project: project,
            branch: branch,
            promotion: promotion,
    )

    // Getting and rendering the changelog between the two builds
    def response = ontrackCliGraphQL(
            query: '''
                query ChangeLog(
                    $fromBuildId: Int!,
                    $toBuildId: Int!,
                    $renderer: String!
                    $config: ChangeLogTemplatingServiceConfig,
                ) {
                    scmChangeLog(
                        from: $fromBuildId,
                        to: $toBuildId,
                    ) {
                        render(
                            renderer: $renderer,
                            config: $config,
                        )
                    }
                }
            ''',
            variables: [
                    fromBuildId: fromBuild.id,
                    toBuildId  : currentBuild.id,
                    renderer   : renderer,
                    config     : config,
            ],
            logging: logging,
    )

    return response.data.scmChangeLog.render
}
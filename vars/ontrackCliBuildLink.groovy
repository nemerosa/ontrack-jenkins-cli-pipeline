import net.nemerosa.ontrack.jenkins.pipeline.build.BuildUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack build link for pull requests."
        return
    }

    String toProject = ParamUtils.getParam(params, "toProject")
    String toBuild = ParamUtils.getParam(params, "toBuild")

    BuildUtils.buildLink(this, params, [
            [
                    project: toProject,
                    build  : toBuild,
            ]
    ])

}
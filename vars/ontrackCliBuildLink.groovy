import net.nemerosa.ontrack.jenkins.pipeline.build.BuildUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params) {
    if (ontrackCliFailsafe()) return

    String toProject = ParamUtils.getParam(params, "toProject")
    String toBuild = ParamUtils.getParam(params, "toBuild")

    BuildUtils.buildLink(this, params, [
            [
                    project: toProject,
                    build  : toBuild,
            ]
    ])

}
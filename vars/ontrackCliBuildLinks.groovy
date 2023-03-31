import net.nemerosa.ontrack.jenkins.pipeline.build.BuildUtils

def call(Map<String, ?> params) {
    if (ontrackCliFailsafe()) return

    List<Map<String, String>> targets = params.to ?: []

    BuildUtils.buildLink(this, params, targets)

}
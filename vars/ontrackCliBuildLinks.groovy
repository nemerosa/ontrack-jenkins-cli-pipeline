import net.nemerosa.ontrack.jenkins.pipeline.build.BuildUtils

def call(Map<String, ?> params) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack build links for pull requests."
        return
    }

    List<Map<String, String>> targets = params.to ?: []

    BuildUtils.buildLink(this, params, targets)

}
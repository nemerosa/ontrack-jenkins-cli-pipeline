import net.nemerosa.ontrack.jenkins.pipeline.build.BuildUtils

def call(Map<String, ?> params) {

    List<Map<String, String>> targets = params.to ?: []

    BuildUtils.buildLink(this, params, targets)

}
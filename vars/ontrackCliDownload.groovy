import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String,?> params = [:]) {
    // Loqging
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    // Version to download
    String version = ParamUtils.getConditionalParam(params, "version", false, null)
    if (logging) {
        if (version) {
            println("[cli-download] Version = $version")
        } else {
            println("[cli-download] Version = latest")
        }
    }
}

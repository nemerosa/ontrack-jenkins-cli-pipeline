import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.cli.OS
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String,?> params = [:]) {
    // Loqging
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)
    // Version to download
    String version = ParamUtils.getConditionalParam(params, "version", false, null)
    if (logging) {
        if (version) {
            println("[cli-download] Version = $version")
        } else {
            println("[cli-download] Version = latest")
        }
    }
    // Logger
    Closure logger = {}
    if (logging && tracing) {
        logger = { println("[cli-download] $it") }
    }
    // Gets the OS & Arch for the CLI
    OS os = Cli.getOS(logger)
    if (logging) {
        println("[cli-download] OS name = ${os.name}")
        println("[cli-download] OS arch = ${os.arch}")
    }
    // Gets the download path to the Cli
    String url = Cli.getDownloadUrl(os, version)
    if (logging) {
        println("[cli-download] CLI URL = $url")
    }
}

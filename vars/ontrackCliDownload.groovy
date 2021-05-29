import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.cli.OS
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.Tools

def call(Map<String,?> params = [:]) {
    // Loqging
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)
    // Name of the executable
    String executable = ParamUtils.getParam(params, "executable", "ontrack-cli")
    // Version to download
    String version = ParamUtils.getConditionalParam(params, "version", false, null)
    if (logging) {
        if (version) {
            println("[ontrack-cli-download] Version = $version")
        } else {
            println("[ontrack-cli-download] Version = latest")
        }
    }
    // Logger
    Closure logger = {}
    if (logging && tracing) {
        logger = { println("[ontrack-cli-download] $it") }
    }
    // Gets the OS & Arch for the CLI
    OS os = Cli.getOS(logger)
    if (logging && tracing) {
        println("[ontrack-cli-download] OS name = ${os.name}")
        println("[ontrack-cli-download] OS arch = ${os.arch}")
    }
    // Windows executable name
    executable += os.extension
    // Gets the download path to the Cli
    String url = Cli.getDownloadUrl(os, version)
    if (logging) {
        println("[ontrack-cli-download] CLI URL = $url")
    }
    // Downloading into a temporary directory and setting the path
    String directory = Tools.download(url, executable, logger)
    if (logging && tracing) {
        println("[ontrack-cli-download] CLI downloaded at $directory/$executable")
    }
    // Exporting the different environment variables
    env.ONTRACK_CLI_DIR = directory
    env.ONTRACK_CLI_NAME = executable
    env.ONTRACK_CLI = "$directory/$executable" as String
    // Path completion
    env.PATH = env.PATH + System.getProperty('path.separator') + directory
    if (logging && tracing) {
        println("[ontrack-cli-download] New PATH ${env.PATH}")
    }
}

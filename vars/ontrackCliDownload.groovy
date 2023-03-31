import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli
import net.nemerosa.ontrack.jenkins.pipeline.cli.OS
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

import hudson.FilePath

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return
    // Loqging
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)
    // Name of the executable
    String executable = ParamUtils.getParam(params, "executable", "ontrack-cli")
    // Download URL
    String downloadHostUrl = ParamUtils.getParam(params, "downloadHostUrl", "https://github.com")
    if (logging) {
        println("[ontrack-cli-download] Download Host URL = $downloadHostUrl")
    }
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
    String url = Cli.getDownloadUrl(os, version, downloadHostUrl)
    if (logging) {
        println("[ontrack-cli-download] CLI URL = $url")
    }

    // Gets the current workspace
    FilePath workspace = getContext hudson.FilePath
    logger("Workspace $workspace")
    // FilePath interface must be used for download
    FilePath directory = workspace.createTempDir('.ontrack-cli', null)
    // Target file
    FilePath target = directory.child(executable)
    // Downloading
    logger("Downloading $url into $target")
    target.copyFrom(new URL(url))
    logger("Target at ${target} is ${target.length()} bytes")
    // Makes the file executable
    target.chmod(0777)

    // Logging
    if (logging && tracing) {
        println("[ontrack-cli-download] CLI downloaded at $target")
    }
    // Exporting the different environment variables
    env.ONTRACK_CLI_DIR = directory.remote
    env.ONTRACK_CLI_NAME = executable
    env.ONTRACK_CLI = "${directory.remote}/$executable" as String
    // Path completion
    env.PATH = env.PATH + System.getProperty('path.separator') + directory.remote
    if (logging && tracing) {
        println("[ontrack-cli-download] New PATH ${env.PATH}")
    }
}

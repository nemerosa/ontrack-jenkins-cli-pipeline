package net.nemerosa.ontrack.jenkins.pipeline.cli

/**
 * Manipulation of the CLI.
 */
class Cli {

    /**
     * Gets information about the OS.
     *
     * @return OS information
     */
    static OS getOS(Closure logger = {}) {
        String jvmName = System.getProperty("os.name")
        String jvmArch = System.getProperty("os.arch")

        if (logger) {
            logger("JVM OS Name = $jvmName")
            logger("JVM OS Arch = $jvmArch")
        }

        String name = ''
        String arch = jvmArch

        // OS name conversion
        if (jvmName.toLowerCase().indexOf('mac') >= 0 || jvmName.toLowerCase().indexOf('darwin') >= 0) {
            logger("Detected Darwin")
            name = 'darwin'
        } else if (jvmName.toLowerCase().indexOf('win') >= 0) {
            logger("Detected Windows")
            name = 'windows'
        } else if (jvmName.toLowerCase().indexOf('linux') >= 0) {
            logger("Detected Linux")
            name = 'linux'
        } else {
            throw new RuntimeException("Unsupported OS name: $jvmName")
        }

        // OK
        return new OS(name, arch)
    }

    /**
     * Gets the URL to download the CLI from.
     * @param os OS information
     * @param version Version or null for the latest
     * @return URL to the CLI
     */
    static String getDownloadUrl(OS os, String version) {
        if (version) {
            return "https://github.com/nemerosa/ontrack-cli/releases/download/$version/ontrack-cli-${os.name}-${os.arch}${os.extension}"
        } else {
            return "https://github.com/nemerosa/ontrack-cli/releases/latest/download/ontrack-cli-${os.name}-${os.arch}${os.extension}"
        }
    }
}
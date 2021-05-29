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
            logger("[cli] JVM OS Name = $jvmName")
            logger("[cli] JVM OS Arch = $jvmArch")
        }

        String name
        String arch = jvmArch

        // OS name conversion
        if (jvmName.toLowerCase().indexOf('mac') || jvmName.toLowerCase().indexOf('darwin') >= 0) {
            if (logger) {
                logger("Detected Darwin")
            }
            name = 'darwin'
        } else if (jvmName.toLowerCase().indexOf('win') >= 0) {
            if (logger) {
                logger("Detected Windows")
            }
            name = 'windows'
        } else if (jvmName.toLowerCase().indexOf('linux') >= 0) {
            if (logger) {
                logger("Detected Linux")
            }
            name = 'linux'
        } else {
            throw new RuntimeException("Unsupported OS name: $jvmName")
        }

        // OK
        return new OS(name, arch)
    }

}
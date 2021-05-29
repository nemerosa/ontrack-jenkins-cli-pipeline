package net.nemerosa.ontrack.jenkins.pipeline.cli

/**
 * Manipulation of the CLI.
 */
class Cli {

    /**
     * Gets information about the OS.
     *
     * @param logger Logger to use (null if no logging is needed)
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
            name = 'darwin'
        } else if (jvmName.toLowerCase().indexOf('win') >= 0) {
            name = 'windows'
        } else if (jvmName.toLowerCase().indexOf('linux') >= 0) {
            name = 'linux'
        } else {
            throw new RuntimeException("Unsupported OS name: $jvmName")
        }

        // OS arch conversion

        // OK
        return new OS(name, arch)
    }

}
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

    /**
     * Calls the CLI and returns the output
     *
     * @param dsl Pipeline DSL
     * @param logging Logging the shell script (set +x)
     * @param params List of parameters
     * @return Standard output of the command
     */
    static String call(def dsl, boolean logging, String... params) {
        return call(dsl, logging, params.toList())
    }

    /**
     * Calls the CLI and returns the output
     *
     * @param dsl Pipeline DSL
     * @param logging Logging the shell script (set +x)
     * @param params List of parameters
     * @return Standard output of the command
     */
    static String call(def dsl, boolean logging, List<String> params) {
        String cli = dsl.env.ONTRACK_CLI_NAME as String
        String cliPath = dsl.env.ONTRACK_CLI_DIR as String
        String script = cli + ' ' + params.toList().join(' ')

        String header = '#!/bin/bash -e'
        if (logging) {
            header += ' -x'
        } else {
            header += ' +x'
        }

        String bash = """\
            $header

            export PATH=\$PATH:$cliPath
            
            $script
        """

        return dsl.sh(script: bash, returnStdout: true).trim()
    }
}
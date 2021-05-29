package net.nemerosa.ontrack.jenkins.pipeline.utils

/**
 * Interaction with the environment
 */
class Tools {
    /**
     * Downloads a file into a temporary directory
     * @param path URL to the executable to download
     * @param executable Name of the file to create locally
     * @param logger Logger
     * @return Path to the directory
     */
    static String download(String path, String executable, Closure logger = {}) {
        // Creating a temporary directory
        File tmp = File.createTempDir()
        // Target file
        File target = new File(tmp, executable)
        // Downloading
        logger("Downloading $path into ${target.absolutePath}")
        URL url = new URL(path)
        target.bytes = url.bytes
        logger("Target at ${target.absolutePath} is ${target.size()} bytes")
        // OK
        return tmp.absolutePath
    }
}

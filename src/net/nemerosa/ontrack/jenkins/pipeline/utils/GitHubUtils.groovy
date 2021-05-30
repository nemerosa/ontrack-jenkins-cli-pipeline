package net.nemerosa.ontrack.jenkins.pipeline.utils

class GitHubUtils {

    static String getOwner(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            return m.group(1)
        } else {
            throw new RuntimeException("Could not identify any GitHub repository owner in Git URL $gitUrl")
        }
    }

    static String getRepository(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            return m.group(2)
        } else {
            throw new RuntimeException("Could not identify any GitHub repository name in Git URL $gitUrl")
        }
    }
}

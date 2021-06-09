package net.nemerosa.ontrack.jenkins.pipeline.utils

class GitLabUtils {

    static String getRepository(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            String project = m.group(1)
            String repository = m.group(2)
            return "$project/$repository" as String
        } else {
            throw new RuntimeException("Could not identify any GitHub repository owner in Git URL $gitUrl")
        }
    }
}

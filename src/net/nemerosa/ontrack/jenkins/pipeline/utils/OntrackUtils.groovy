package net.nemerosa.ontrack.jenkins.pipeline.utils

class OntrackUtils {

    static String getProjectName(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /\\/([^\\/]*)\.git/
        if (m.matches()) {
            return m.group(1)
        } else {
            throw new RuntimeException("Could not identify any project name in Git URL $gitUrl")
        }
    }

}

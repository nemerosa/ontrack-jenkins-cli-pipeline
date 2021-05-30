package net.nemerosa.ontrack.jenkins.pipeline.utils

class OntrackUtils {

    static String getProjectName(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /\\/([^\\/]*)\.git$/
        if (m.find()) {
            return m.group(1)
        } else {
            throw new RuntimeException("Could not identify any project name in Git URL $gitUrl")
        }
    }

    static String getBranchName(String branchName) {
        if (!branchName) {
            throw new RuntimeException("BRANCH_NAME environment variable is required.")
        } else {
            return branchName.replaceAll(/[^A-Za-z0-9_\\.-]/, '-')
        }
    }

}

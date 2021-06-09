package net.nemerosa.ontrack.jenkins.pipeline.utils

class BitbucketUtils {

    static BitbucketRepository getBitbucketRepository(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            String project = m.group(1)
            String repository = m.group(2)
            return new BitbucketRepository(project, repository)
        } else {
            throw new RuntimeException("Could not identify any Bitbucket project/repository in Git URL $gitUrl")
        }
    }

}

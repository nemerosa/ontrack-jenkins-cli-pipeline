package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.server

class BitbucketServerUtils {

    static BitbucketServerRepository getBitbucketRepository(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            String project = m.group(1)
            String repository = m.group(2)
            return new BitbucketServerRepository(project, repository)
        } else {
            throw new RuntimeException("Could not identify any Bitbucket Server project/repository in Git URL $gitUrl")
        }
    }

}

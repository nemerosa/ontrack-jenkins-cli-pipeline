package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.cloud

class BitbucketCloudUtils {

    static BitbucketCloudRepository getBitbucketRepository(String gitUrl) {
        if (!gitUrl) {
            throw new RuntimeException("GIT_URL environment variable is required.")
        }
        def m = gitUrl =~ /([^\/:]*)\/([^\/]*)\.git$/
        if (m.find()) {
            String workspace = m.group(1)
            String repository = m.group(2)
            return new BitbucketCloudRepository(workspace, repository)
        } else {
            throw new RuntimeException("Could not identify any Bitbucket Cloud workspace/repository in Git URL $gitUrl")
        }
    }

}

package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.cloud

/**
 * Defines a repository in Bitbucket Cloud using its workspace and name.
 */
class BitbucketCloudRepository {

    private final String workspace
    private final String repository

    BitbucketCloudRepository(String workspace, String repository) {
        this.workspace = workspace
        this.repository = repository
    }

    String getWorkspace() {
        return workspace
    }

    String getRepository() {
        return repository
    }

}

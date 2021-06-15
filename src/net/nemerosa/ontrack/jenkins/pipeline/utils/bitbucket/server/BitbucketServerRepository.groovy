package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.server

class BitbucketServerRepository {

    private final String project
    private final String repository

    BitbucketServerRepository(String project, String repository) {
        this.project = project
        this.repository = repository
    }

    String getProject() {
        return project
    }

    String getRepository() {
        return repository
    }
}

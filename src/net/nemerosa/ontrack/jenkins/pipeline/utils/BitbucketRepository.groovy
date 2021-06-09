package net.nemerosa.ontrack.jenkins.pipeline.utils

class BitbucketRepository {

    private final String project
    private final String repository

    BitbucketRepository(String project, String repository) {
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

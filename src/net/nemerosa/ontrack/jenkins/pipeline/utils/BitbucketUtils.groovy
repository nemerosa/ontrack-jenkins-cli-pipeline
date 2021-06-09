package net.nemerosa.ontrack.jenkins.pipeline.utils

class BitbucketUtils {

    static class BitbucketRepository {
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

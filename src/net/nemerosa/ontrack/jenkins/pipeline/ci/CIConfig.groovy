package net.nemerosa.ontrack.jenkins.pipeline.ci

class CIConfig {
    final String expandedConfigText
    final List<CIConfigEnv> environment

    CIConfig(String expandedConfigText, List<CIConfigEnv> environment) {
        this.expandedConfigText = expandedConfigText
        this.environment = environment
    }
}

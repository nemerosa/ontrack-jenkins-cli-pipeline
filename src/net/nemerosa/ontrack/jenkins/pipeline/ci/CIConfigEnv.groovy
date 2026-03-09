package net.nemerosa.ontrack.jenkins.pipeline.ci

class CIConfigEnv {
    final String name
    final String value

    CIConfigEnv(String name, String value) {
        this.name = name
        this.value = value
    }
}

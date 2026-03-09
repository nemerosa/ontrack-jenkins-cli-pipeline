package net.nemerosa.ontrack.jenkins.pipeline.ci

class CIConfigEnv {
    final String name
    final String value

    CIConfigEnv(String name, String value) {
        this.name = name
        this.value = value
    }

    static CIConfigEnv of(String name, String value) {
        return new CIConfigEnv(name, value)
    }
}

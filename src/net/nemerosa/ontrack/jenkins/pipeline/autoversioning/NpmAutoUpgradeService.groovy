package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

class NpmAutoUpgradeService extends AbstractAutoUpgradeService {

    @Override
    String getDefaultVersionFilePath() {
        return "package.json"
    }

    @Override
    String readVersion(Object script, String filePath, String versionProperty) {
        def json = script.readJSON(file: filePath) as Map<String, ?>
        def dependencies = json.dependencies as Map<String, String>
        def entry = dependencies.find { it.key == versionProperty }
        if (entry) {
            String text = entry.value
            if (text.startsWith("^")) {
                return text - "^"
            } else {
                throw new IllegalArgumentException("Version defined by $versionProperty in $filePath must start with ^: $text")
            }
        } else {
            throw new IllegalArgumentException("Cannot find dependency $versionProperty in $filePath")
        }
    }

    @Override
    void replaceVersion(Object script, String versionFilePath, String versionProperty, String version) {
        def json = script.readJSON(file: versionFilePath) as Map<String, ?>
        def dependencies = json.dependencies as Map<String, String>
        dependencies.put(versionProperty, "^$version")
        script.writeObjectAsJSON(versionFilePath, json)
    }

    @Override
    void resolveLocks(Object script, String dir, String resolveLocksCommand) {
        String command = resolveLocksCommand ?: 'npm i'
        script.withCredentials([script.usernamePassword(credentialsId: 'nexus-gradle', passwordVariable: 'NEXUS_PSW', usernameVariable: 'NEXUS_USR')]) {
            script.sh script: """
                cd $dir
                # We don't fail the pipeline if resolving fails, as we want the PR to always be created
                set +e
                $command
            """, returnStatus: true
        }
        script.sh """
            cd $dir
            git add package-lock.json
        """
    }
}

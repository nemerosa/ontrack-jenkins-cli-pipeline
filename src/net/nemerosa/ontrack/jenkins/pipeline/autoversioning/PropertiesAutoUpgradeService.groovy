package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

class PropertiesAutoUpgradeService extends AbstractAutoUpgradeService {

    @Override
    String getDefaultVersionFilePath() {
        return "gradle.properties"
    }

    @Override
    String readVersion(def script, String filePath, String versionProperty) {
        def oldProps = script.readProperties(file: filePath)
        return oldProps.get(versionProperty) as String
    }

    @Override
    void replaceVersion(def script, String versionFilePath, String versionProperty, String version) {
        String versionRegex = "${versionProperty}[\\s]*=[\\s]*(.*)" as String
        script.replaceInFileUsingRegex(
                versionFilePath,
                versionRegex,
                "${versionProperty} = ${version}"
        )
    }

    @Override
    void resolveLocks(def script, String dir, String resolveLocksCommand) {
        String command = resolveLocksCommand ?: '''
            ./gradlew \\
                resolveAndLockAll \\
                --write-locks \\
                -PnexusUserName=\${NEXUS_USR} \\
                -PnexusPassword=\${NEXUS_PSW} \\
                --console plain \\
                --stacktrace
        '''
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
            git add *.lockfile
        """
    }
}

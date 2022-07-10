package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

/**
 * Defines the way an auto upgrade of version must be performed.
 */
interface AutoUpgradeService {

    /**
     * Reads the version from a file
     *
     * @param script The DSL
     * @param filePath Path to the file
     * @param versionProperty Version property
     * @return Version
     */
    String readVersion(def script, String filePath, String versionProperty)

    /**
     * Replaces the version in a file
     *
     * @param script The DSL
     * @param versionFilePath Path to the file
     * @param versionProperty Version property
     * @param version New version to set
     */
    void replaceVersion(def script, String versionFilePath, String versionProperty, String version)

    /**
     * Lock file resolution after version has been upgraded
     *
     * @param script The DSL
     * @param dir Path to the directory containing the files
     * @param resolveLocksCommand Optional (can be <code>null</code>) snippet used to override the lock resolution
     */
    void resolveLocks(def script, String dir, String resolveLocksCommand)

    /**
     * Gets the default path to the file containing the version to update
     *
     * @return Path relative to the root of the repository
     */
    String getDefaultVersionFilePath()
}
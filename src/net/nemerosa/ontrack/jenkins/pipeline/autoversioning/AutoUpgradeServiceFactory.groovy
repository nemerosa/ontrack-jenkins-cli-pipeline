package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

/**
 * Returns an {@link AutoUpgradeService} according to an ID.
 */
class AutoUpgradeServiceFactory {

    static AutoUpgradeService getAutoUpgradeService(String id) {
        switch (id) {
            case 'properties':
                return new PropertiesAutoUpgradeService()
            case 'npm':
                return new NpmAutoUpgradeService()
            default:
                throw new IllegalArgumentException("Auto upgrade service with ID = $id is not defined")
        }
    }

}

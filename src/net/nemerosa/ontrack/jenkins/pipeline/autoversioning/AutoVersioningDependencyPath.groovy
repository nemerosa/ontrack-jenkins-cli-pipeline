package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

class AutoVersioningDependencyPath {

    private final String path
    private final String regex
    private final String property
    private final String propertyRegex
    private final String propertyType
    private final String versionSource

    AutoVersioningDependencyPath(String path, String regex, String property, String propertyRegex, String propertyType, String versionSource) {
        this.path = path
        this.regex = regex
        this.property = property
        this.propertyRegex = propertyRegex
        this.propertyType = propertyType
        this.versionSource = versionSource
    }

    String getPath() {
        return path
    }

    String getRegex() {
        return regex
    }

    String getProperty() {
        return property
    }

    String getPropertyRegex() {
        return propertyRegex
    }

    String getPropertyType() {
        return propertyType
    }

    String getVersionSource() {
        return versionSource
    }

    Map<String, ?> toMap() {
        return [
                path         : path,
                regex        : regex,
                property     : property,
                propertyRegex: propertyRegex,
                propertyType : propertyType,
                versionSource: versionSource,
        ]
    }
}

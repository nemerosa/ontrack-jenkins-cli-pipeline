package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

/**
 * Collection of dependencies for auto version on promotion
 */
class AutoVersioningContext {

    /**
     * DSL context
     */
    private final def dsl

    /**
     * Logger
     */
    private final Closure logger

    /**
     * Constructor
     * @param dsl DSL context
     */
    AutoVersioningContext(dsl, Closure logger) {
        this.dsl = dsl
        this.logger = logger
    }

    /**
     * List of regular expressions defining the Git branches where to apply this auto versioning
     */
    Set<String> branches = [] as Set

    /**
     * List of dependency configurations
     */
    List dependencies = []

    /**
     * Adds a Git branch
     */
    void branch(String value) {
        branches.add(value)
    }

    /**
     * Putting the dependencies as a YAML definition file
     */
    void yaml(String path) {
        // Reads the path as YAML
        def yaml = dsl.readYaml(file: path)
        // Logging
        logger("yaml = $yaml")
        // Configuration list
        def configurations = yaml.configurations
        if (!yaml.configurations) {
            configurations = yaml.dependencies
            if (!configurations) {
                configurations = []
            }
        }
        // Dependencies as a collection
        dependencies += configurations
    }

    /**
     * Defines a dependency
     */
    void dependency(Map<String, ?> params) {
        def path = params.path
        if (path) {
            if (path instanceof Collection) {
                params.targetPath = path.join(",")
            } else {
                params.targetPath = path as String
            }
        }
        // Adding this configuration to the list
        dependencies.add(params)
    }

}

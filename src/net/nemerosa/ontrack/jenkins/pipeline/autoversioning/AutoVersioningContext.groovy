package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

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
    List<AutoVersioningDependency> dependencies = []

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
        // Dependencies as a collection
        yaml.dependencies.each {
            def map = it as Map
            logger("config map = $map")
            dependency(map)
        }
    }

    /**
     * Defines a dependency
     */
    void dependency(Map<String, ?> params) {

        String targetPath
        def path = params.path
        if (!path) {
            throw new IllegalArgumentException("Missing parameter: path")
        } else if (path instanceof Collection) {
            targetPath = path.join(",")
        } else {
            targetPath = path as String
        }

        String sourceProject = ParamUtils.getParam(params, "project")
        String sourceBranch = ParamUtils.getParam(params, "branch")
        String sourcePromotion = ParamUtils.getParam(params, "promotion")
        String targetRegex = params.regex
        String targetProperty = params.property
        String targetPropertyRegex = params.propertyRegex
        String targetPropertyType = params.propertyType
        Boolean autoApproval = params.autoApproval as Boolean
        String validationStamp = params.validationStamp
        String upgradeBranchPattern = params.upgradeBranchPattern
        String autoApprovalMode = params.autoApprovalMode
        String qualifier = params.qualifier
        String versionSource = params.versionSource
        List<String> reviewers = params.reviewers as List<String> ?: []

        List<AutoVersioningNotification> notifications = null
        def notificationsArray = params.notifications
        if (notificationsArray) {
            notifications = notificationsArray.collect {
                new AutoVersioningNotification(
                        it.channel as String,
                        it.config,
                        it.scope as List<String>,
                        it.notificationTemplate as String,
                )
            }
        }

        String postProcessing = params.postProcessing
        def postProcessingConfig = params.postProcessingConfig as Map<String, ?> ?: [:]

        // Config object
        def config = new AutoVersioningDependency(
                sourceProject,
                sourceBranch,
                sourcePromotion,
                targetPath,
                targetRegex,
                targetProperty,
                targetPropertyRegex,
                targetPropertyType,
                autoApproval,
                upgradeBranchPattern,
                validationStamp,
                postProcessing,
                postProcessingConfig as Map<String, ?>,
                autoApprovalMode,
                qualifier,
                versionSource,
                reviewers,
                notifications,
        )

        // Adding this configuration to the list
        dependencies.add(config)
    }

}

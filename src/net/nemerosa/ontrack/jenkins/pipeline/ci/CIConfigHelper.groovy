package net.nemerosa.ontrack.jenkins.pipeline.ci

import org.yaml.snakeyaml.Yaml
import com.cloudbees.groovy.cps.NonCPS

class CIConfigHelper {

    @NonCPS
    static String expandConfig(def dsl, String configText, Closure logger) {
        def yaml = new Yaml()
        def configData = yaml.load(configText)
        configData = resolveYamlReferences(dsl, configData, logger)
        return yaml.dump(configData)
    }

    /**
     * Recursively resolves YAML file references (values starting with '@')
     * @param dsl The Jenkins Pipeline DSL
     * @param obj The object to process (can be Map, List, or primitive)
     * @param logger Logger closure for debug output
     * @return The processed object with file references resolved
     */
    @NonCPS
    static private def resolveYamlReferences(def dsl, def obj, Closure logger) {
        if (obj instanceof Map) {
            def result = [:]
            obj.each { key, value ->
                result[key] = resolveYamlReferences(dsl, value, logger)
            }
            return result
        } else if (obj instanceof List) {
            return obj.collect { item ->
                resolveYamlReferences(dsl, item, logger)
            }
        } else if (obj instanceof String && obj.startsWith('@')) {
            // This is a file reference - resolve it
            def referencedFile = obj.substring(1) // Remove the '@' prefix
            logger("Resolving file reference: ${referencedFile}")

            // Read the referenced file
            String referencedContent = dsl.readFile(file: referencedFile)

            // Parse the referenced YAML
            def yaml = new Yaml()
            def referencedData = yaml.load(referencedContent)

            // OK
            return referencedData
        } else {
            // Return as-is for primitives and other types
            return obj
        }
    }

}

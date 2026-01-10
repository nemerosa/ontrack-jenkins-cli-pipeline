package net.nemerosa.ontrack.jenkins.pipeline.ci

import groovy.text.SimpleTemplateEngine
import org.yaml.snakeyaml.Yaml

class CIConfigHelper {

    static String expandConfig(def dsl, String configText, Closure logger, Map<String, ?> vars = [:], List<Map<String, String>> environment = []) {
        def binding = vars + environment.collectEntries { [it.name, it.value] }
        binding.environment = environment
        binding.variables = vars.collect { k, v -> [name: k, value: v] }

        // 1. Resolve YAML references (without rendering)
        def yaml = new Yaml()
        def configData = yaml.load(configText)
        configData = resolveYamlReferences(dsl, configData, logger)

        // 2. Render the whole expanded config as a template
        def expandedConfigText = yaml.dump(configData)
        // Escape backslashes for the template engine
        expandedConfigText = expandedConfigText.replace('\\', '\\\\')
        // Unquote template expressions so they can be rendered as literals (important for type preservation)
        expandedConfigText = expandedConfigText.replaceAll(/'(\$\{[^']+\})'/, '$1')
        expandedConfigText = expandedConfigText.replaceAll(/'(\$[^']+)'/, '$1')
        // Restore backslashes inside template expressions
        expandedConfigText = expandedConfigText.replaceAll(/\$\{[^}]+\}/) { it.replace('\\\\', '\\') }
        expandedConfigText = expandedConfigText.replaceAll(/<%[^%>]+%>/) { it.replace('\\\\', '\\').replace("''", "'") }

        def engine = new SimpleTemplateEngine()
        def renderedConfigText = engine.createTemplate(expandedConfigText).make(binding).toString()

        // 3. Final YAML validation and formatting
        return yaml.dump(yaml.load(renderedConfigText))
    }

    /**
     * Recursively resolves YAML file references (values starting with '@')
     * @param dsl The Jenkins Pipeline DSL
     * @param obj The object to process (can be Map, List, or primitive)
     * @param logger Logger closure for debug output
     * @return The processed object with file references resolved
     */
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

            // Recursively resolve references in the loaded data
            return resolveYamlReferences(dsl, referencedData, logger)
        } else {
            // Return as-is for primitives and other types
            return obj
        }
    }

}

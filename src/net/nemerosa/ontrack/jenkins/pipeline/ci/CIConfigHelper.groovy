package net.nemerosa.ontrack.jenkins.pipeline.ci

import groovy.text.SimpleTemplateEngine
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

class CIConfigHelper {

    static private def engine = new SimpleTemplateEngine()

    static CIConfig buildCIConfig(def dsl, Closure logger, String configPath, Map<String, ?> vars, boolean skipGitCommit = false) {
        logger("Reading CI config at $configPath")
        def configText = dsl.readFile(file: configPath)
        logger("CI config: $configText")

        // Collecting the environment
        List<CIConfigEnv> environment = dsl.env.getEnvironment().findAll { k, _ ->
            k.startsWith('GIT_') ||
                    k.startsWith('JOB_') ||
                    k.startsWith('NODE_') ||
                    k.startsWith('BUILD_') ||
                    k == "JENKINS_URL" ||
                    k == "BRANCH_NAME" ||
                    k == "VERSION"
        }.collect { k, v ->
            CIConfigEnv(k, v)
        } + [
                CIConfigEnv('GIT_URL', dsl.env.GIT_URL) // Not part of the env.getEnvironment()
        ]

        // GIT_COMMIT maybe not be injected correctly
        def existingGitCommit = environment.find { it.name == "GIT_COMMIT" }?.value
        if (!existingGitCommit && !skipGitCommit) {
            def gitCommit = dsl.sh(
                    returnStdout: true,
                    script: 'git rev-parse HEAD'
            ).trim()
            if (gitCommit) {
                environment += CIConfigEnv('GIT_COMMIT', gitCommit)
            }
        }

        def expandedConfigText = expandConfig(this, configText, logger, vars, environment)
        logger("CI expanded config: $expandedConfigText")

        return new CIConfig(expandedConfigText, environment)
    }

    static String expandConfig(def dsl, String configText, Closure logger, Map<String, ?> vars = [:], List<Map<String, String>> environment = []) {
        def binding = vars + environment.collectEntries { [it.name, it.value] }
        binding.environment = environment
        binding.variables = vars.collect { k, v -> [name: k, value: v] }

        def configYaml = renderTemplate(dsl, configText, logger, binding)
        
        def options = new DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.indicatorIndent = 2
        options.indent = 4
        def yaml = new Yaml(options)
        return yaml.dump(configYaml)
    }

    private static def renderTemplate(def dsl, String configText, Closure logger, Map<String, ?> binding) {

        // 1. Rendering as a Groovy template first

        def renderedConfigText = renderText(configText, binding)

        // 2. Parsing as YAML

        def yaml = new Yaml()
        def configYaml = yaml.load(renderedConfigText)

        // 3. Resolving references
        return resolveYamlReferences(dsl, configYaml, logger, binding)
    }

    private static String renderText(String configText, Map<String, ?> binding) {
        // Escape backslashes for the template engine
        String text = configText.replace('\\', '\\\\')
        // Escape all ${...} to preserve them for other systems (like Ontrack)
        text = text.replace('$', '\\$')
        // Unquote template expressions so they can be rendered as literals (important for type preservation)
        text = text.replaceAll(/'(<%=[^']+%>)'/, '$1')
        // Restore backslashes inside template expressions
        text = text.replaceAll(/<%[^%>]+%>/) { it.replace('\\\\', '\\').replace("''", "'") }
        // Actual processing
        engine.createTemplate(text).make(binding).toString()
    }

    /**
     * Recursively resolves YAML file references (values starting with '@')
     * @param dsl The Jenkins Pipeline DSL
     * @param obj The object to process (can be Map, List, or primitive)
     * @param logger Logger closure for debug output
     * @param binding For the rendering of the included fragments
     * @return The processed object with file references resolved
     */
    static private def resolveYamlReferences(def dsl, def obj, Closure logger, Map<String, ?> binding) {
        if (obj instanceof Map) {
            def result = [:]
            obj.each { key, value ->
                result[key] = resolveYamlReferences(dsl, value, logger, binding)
            }
            return result
        } else if (obj instanceof List) {
            return obj.collect { item ->
                resolveYamlReferences(dsl, item, logger, binding)
            }
        } else if (obj instanceof String && obj.startsWith('@')) {
            // This is a file reference - resolve it
            def referencedFile = obj.substring(1) // Remove the '@' prefix
            logger("Resolving file reference: ${referencedFile}")

            // Read the referenced file
            String referencedContent = dsl.readFile(file: referencedFile)

            // Recursive rendering
            return renderTemplate(dsl, referencedContent, logger, binding)
        } else {
            // Return as-is for primitives and other types
            return obj
        }
    }

}

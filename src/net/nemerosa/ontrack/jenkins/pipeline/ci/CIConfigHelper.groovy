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
                    k.startsWith('YONTRACK_CI_') ||
                    k == "JENKINS_URL" ||
                    k == "BRANCH_NAME" ||
                    k == "VERSION" ||
                    // Legacy (Y)Ontrack environment variables
                    k == 'ONTRACK_SCM_ISSUES'
        }.collect { k, v ->
            CIConfigEnv.of(k as String, v as String)
        } + [
                CIConfigEnv.of('GIT_URL', dsl.env.GIT_URL as String) // Not part of the env.getEnvironment()
        ]

        // GIT_COMMIT maybe not be injected correctly
        def existingGitCommit = environment.find { it.name == "GIT_COMMIT" }?.value
        if (!existingGitCommit && !skipGitCommit) {
            def gitCommit = dsl.sh(
                    returnStdout: true,
                    script: 'git rev-parse HEAD'
            ).trim()
            if (gitCommit) {
                environment += CIConfigEnv.of('GIT_COMMIT', gitCommit as String)
            }
        }

        def expandedConfigText = expandConfig(dsl, configText, logger, vars, environment)
        logger("CI expanded config: $expandedConfigText")

        return new CIConfig(expandedConfigText, environment)
    }

    static String expandConfig(def dsl, String configText, Closure logger, Map<String, ?> vars = [:], List<CIConfigEnv> environment = []) {
        def binding = vars + environment.collectEntries { [it.name, it.value] }
        binding.environment = environment
        binding.variables = vars.collect { k, v -> [name: k, value: v] }

        // Add include function to binding
        binding.include = { String name, int indent = 0, Map<String, ?> extraVars = [:] ->
            includeTemplate(dsl, name, indent, false, logger, binding, extraVars)
        }
        binding.ninclude = { String name, int indent = 0, Map<String, ?> extraVars = [:] ->
            includeTemplate(dsl, name, indent, true, logger, binding, extraVars)
        }

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
     * Includes a template file, renders it with the current context plus extra variables,
     * and indents each line of the result
     * @param dsl The Jenkins Pipeline DSL
     * @param name The name of the file to include (without .yaml extension)
     * @param indent Number of spaces to indent each line
     * @param prependNewline If true, prepend a newline before the content (ninclude behavior)
     * @param logger Logger closure for debug output
     * @param binding Current template binding/context
     * @param extraVars Additional variables to add to the context
     * @return The rendered and indented content
     */
    private static String includeTemplate(def dsl, String name, int indent, boolean prependNewline, Closure logger, Map<String, ?> binding, Map<String, ?> extraVars) {
        // Construct the filename
        String filename = "${name}.yaml"
        logger("Including template file: ${filename}")

        // Read the file
        String fileContent = dsl.readFile(file: filename)

        // Merge binding with extra variables
        def mergedBinding = binding + extraVars

        // Render the template
        String rendered = renderText(fileContent, mergedBinding)

        // Apply indentation to each line
        if (indent > 0) {
            String indentation = ' ' * indent
            rendered = rendered.split('\n').collect { line ->
                line.isEmpty() ? line : indentation + line
            }.join('\n')
        }

        // Prepend newline if requested (ninclude behavior)
        if (prependNewline) {
            rendered = '\n' + rendered
        }

        return rendered
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

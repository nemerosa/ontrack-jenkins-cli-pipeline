package net.nemerosa.ontrack.jenkins.pipeline.ci


import org.junit.Before
import org.junit.Test
import org.yaml.snakeyaml.Yaml

import static org.junit.Assert.*

class CIConfigHelperTest {

    def dslMock
    def loggerMock
    def loggerCalls
    Yaml yaml

    @Before
    void setup() {
        // Simple map-based mock with closure for readFile
        dslMock = [
                env     : [
                        getEnvironment: { [:] }
                ],
                readFile: { Map args ->
                    // This will be overridden in individual tests
                    throw new RuntimeException("readFile not configured for: ${args.file}")
                }
        ]

        // Track logger calls
        loggerCalls = []
        loggerMock = { String message ->
            loggerCalls << message
        }

        yaml = new Yaml()
    }

    @Test
    void expandConfig_shouldReturnConfigTextUnchangedWhenNoReferences() {
        // Given: a simple YAML config without file references
        String configText = """
project: test-project
branch: main
build: "1.0.0"
"""

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: the result should be valid YAML with the same content
        def resultData = yaml.load(result)
        assertEquals("test-project", resultData.project)
        assertEquals("main", resultData.branch)
        assertEquals("1.0.0", resultData.build)
    }

    @Test
    void expandConfig_shouldResolveSingleFileReference() {
        // Given: a config with a file reference
        String configText = """
project: test-project
settings: '@settings.yaml'
"""
        // And: the referenced file content
        String settingsContent = """
timeout: 300
retry: 3
"""

        // And: configure the mock to return the file content
        dslMock.readFile = { Map args ->
            if (args.file == 'settings.yaml') {
                return settingsContent
            }
            throw new RuntimeException("Unexpected file: ${args.file}")
        }

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: the logger is called for the file reference
        assertEquals(1, loggerCalls.size())
        assertEquals("Resolving file reference: settings.yaml", loggerCalls[0])

        // And: the result contains the resolved content
        def resultData = yaml.load(result)
        assertEquals("test-project", resultData.project)
        assertEquals(300, resultData.settings.timeout)
        assertEquals(3, resultData.settings.retry)
    }

    @Test
    void expandConfig_shouldResolveNestedFileReferencesInMaps() {
        // Given: a config with nested file references
        String configText = """
project: test-project
pipeline:
  stages: '@stages.yaml'
  options:
    timeout: 600
"""
        // And: the referenced file content
        String stagesContent = """
- build
- test
- deploy
"""

        // And: configure the mock
        dslMock.readFile = { Map args ->
            if (args.file == 'stages.yaml') {
                return stagesContent
            }
            throw new RuntimeException("Unexpected file: ${args.file}")
        }

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: the logger is called
        assertEquals(1, loggerCalls.size())
        assertEquals("Resolving file reference: stages.yaml", loggerCalls[0])

        // And: the result contains the resolved content
        def resultData = yaml.load(result)
        assertEquals("test-project", resultData.project)
        assertEquals(['build', 'test', 'deploy'], resultData.pipeline.stages)
        assertEquals(600, resultData.pipeline.options.timeout)
    }

    @Test
    void expandConfig_shouldResolveFileReferencesInLists() {
        // Given: a config with file references in a list
        String configText = """
project: test-project
includes:
  - '@config1.yaml'
  - '@config2.yaml'
  - inline-value
"""
        // And: the referenced files content
        String config1Content = """
name: config1
enabled: true
"""
        String config2Content = """
name: config2
enabled: false
"""

        // And: configure the mock
        dslMock.readFile = { Map args ->
            switch (args.file) {
                case 'config1.yaml':
                    return config1Content
                case 'config2.yaml':
                    return config2Content
                default:
                    throw new RuntimeException("Unexpected file: ${args.file}")
            }
        }

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: the logger is called for both files
        assertEquals(2, loggerCalls.size())
        assertEquals("Resolving file reference: config1.yaml", loggerCalls[0])
        assertEquals("Resolving file reference: config2.yaml", loggerCalls[1])

        // And: the result contains all resolved content
        def resultData = yaml.load(result)
        assertEquals("test-project", resultData.project)
        assertEquals(3, resultData.includes.size())
        assertEquals("config1", resultData.includes[0].name)
        assertEquals(true, resultData.includes[0].enabled)
        assertEquals("config2", resultData.includes[1].name)
        assertEquals(false, resultData.includes[1].enabled)
        assertEquals("inline-value", resultData.includes[2])
    }

    @Test
    void expandConfig_shouldHandleDeeplyNestedStructures() {
        // Given: a config with deeply nested file references
        String configText = """
project: test-project
configuration:
  level1:
    level2:
      level3: '@deep.yaml'
"""
        // And: the referenced file content
        String deepContent = """
value: deep-value
count: 42
"""

        // And: configure the mock
        dslMock.readFile = { Map args ->
            if (args.file == 'deep.yaml') {
                return deepContent
            }
            throw new RuntimeException("Unexpected file: ${args.file}")
        }

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: the logger is called
        assertEquals(1, loggerCalls.size())
        assertEquals("Resolving file reference: deep.yaml", loggerCalls[0])

        // And: the result contains the resolved content at the correct nesting level
        def resultData = yaml.load(result)
        assertEquals("deep-value", resultData.configuration.level1.level2.level3.value)
        assertEquals(42, resultData.configuration.level1.level2.level3.count)
    }

    @Test
    void expandConfig_shouldNotResolveStringsThatContainAtButDontStartWithIt() {
        // Given: a config with @ in the middle of strings
        String configText = """
project: test-project
email: user@example.com
description: "This is @ sign in text"
"""

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: no logger calls should occur
        assertEquals(0, loggerCalls.size())

        // And: the strings remain unchanged
        def resultData = yaml.load(result)
        assertEquals("user@example.com", resultData.email)
        assertEquals("This is @ sign in text", resultData.description)
    }

    @Test
    void expandConfig_shouldHandleMixedPrimitivesCorrectly() {
        // Given: a config with various primitive types
        String configText = """
project: test-project
number: 123
decimal: 45.67
enabled: true
disabled: false
nullValue: null
"""

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: no logger calls should occur
        assertEquals(0, loggerCalls.size())

        // And: all primitives are preserved correctly
        def resultData = yaml.load(result)
        assertEquals(123, resultData.number)
        assertEquals(45.67, resultData.decimal, 0.001)
        assertEquals(true, resultData.enabled)
        assertEquals(false, resultData.disabled)
        assertNull(resultData.nullValue)
    }

    @Test
    void expandConfig_shouldHandleEmptyCollections() {
        // Given: a config with empty collections
        String configText = """
project: test-project
emptyList: []
emptyMap: {}
"""

        // When: expanding the config
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock)

        // Then: no logger calls should occur
        assertEquals(0, loggerCalls.size())

        // And: empty collections are preserved
        def resultData = yaml.load(result)
        assertEquals([], resultData.emptyList)
        assertEquals([:], resultData.emptyMap)
    }

    @Test
    void expandConfig_template_with_vars() {
        // Given
        String configText = 'project: $project'
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [project: 'test'])
        // Then
        def resultData = yaml.load(result)
        assertEquals('test', resultData.project)
    }

    @Test
    void expandConfig_template_with_env() {
        // Given
        def environment = [[name: 'BRANCH_NAME', value: 'main']]
        String configText = 'branch: $BRANCH_NAME'
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [:], environment)
        // Then
        def resultData = yaml.load(result)
        assertEquals('main', resultData.branch)
    }

    @Test
    void expandConfig_template_with_env_list() {
        // Given
        def environment = [[name: 'GIT_COMMIT', value: '123456']]
        String configText = 'commit: ${environment.find { it.name == "GIT_COMMIT" }.value}'
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [:], environment)
        // Then
        def resultData = yaml.load(result)
        assertEquals(123456, resultData.commit)
    }

    @Test
    void expandConfig_template_with_variables_list() {
        // Given
        def vars = [MY_VAR: 'my-value']
        String configText = 'var: ${variables.find { it.name == "MY_VAR" }.value}'
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, vars)
        // Then
        def resultData = yaml.load(result)
        assertEquals('my-value', resultData.var)
    }

    @Test
    void expandConfig_should_resolve_references_before_template_rendering() {
        // Given: a config with a file reference that contains a template variable
        String configText = """
project: test-project
settings: '@settings.yaml'
"""
        // And: the referenced file content containing a template variable
        String settingsContent = """
timeout: \${TIMEOUT}
"""

        // And: configure the mock
        dslMock.readFile = { Map args ->
            if (args.file == 'settings.yaml') {
                return settingsContent
            }
            throw new RuntimeException("Unexpected file: \${args.file}")
        }

        // When: expanding the config with variables
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [TIMEOUT: 500])

        // Then: the result contains the resolved and rendered content
        def resultData = yaml.load(result)
        assertEquals(500, resultData.settings.timeout)
    }

    @Test
    void expandConfig_regex_preservation() {
        // Given
        String configText = """
custom:
  configs:
    - conditions:
        - name: environment-regex
          config:
            name: VERSION
            regex: '\\d+\\.\\d+\\.\\d+'
"""
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [:])
        // Then
        def resultData = yaml.load(result)
        assertEquals('\\d+\\.\\d+\\.\\d+', resultData.custom.configs[0].conditions[0].config.regex)
    }

    @Test
    void expandConfig_template_with_groovy_expression_blocks() {
        // Given
        def environment = [[name: 'ENV', value: 'prod']]
        String configText = """
project: <% if (ENV == 'prod') { print 'production' } else { print 'test' } %>
"""
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [:], environment)
        // Then
        def resultData = yaml.load(result)
        assertEquals('production', resultData.project)
    }

    @Test
    void expandConfig_template_with_backslashes_in_groovy_expression_blocks() {
        // Given
        String configText = """
regex: <% print '\\\\d+' %>
literal: <% print '\\\\\\\\path\\\\\\\\to\\\\\\\\file' %>
"""
        // When
        String result = CIConfigHelper.expandConfig(dslMock, configText, loggerMock, [:])
        // Then
        def resultData = yaml.load(result)
        assertEquals('\\d+', resultData.regex)
        assertEquals('\\\\path\\\\to\\\\file', resultData.literal)
    }
}
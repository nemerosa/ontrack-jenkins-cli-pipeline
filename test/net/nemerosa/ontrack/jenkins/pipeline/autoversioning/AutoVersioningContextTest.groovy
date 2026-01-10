package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*

class AutoVersioningContextTest {

    private def dsl
    private List<String> logs
    private Closure logger
    private AutoVersioningContext context

    @Before
    void setup() {
        logs = []
        logger = { String msg -> logs << msg }
        dsl = [:]
        context = new AutoVersioningContext(dsl, logger)
    }

    @Test
    void "Initial state"() {
        assertTrue(context.branches.isEmpty())
        assertTrue(context.dependencies.isEmpty())
    }

    @Test
    void "Adding a branch"() {
        context.branch "main"
        context.branch "release/.*"
        assertEquals(["main", "release/.*"] as Set, context.branches)
    }

    @Test
    void "Adding a dependency"() {
        context.dependency(
                project: "project",
                branch: "master",
                artifact: "artifact"
        )
        assertEquals(1, context.dependencies.size())
        assertEquals("project", context.dependencies[0].project)
        assertEquals("master", context.dependencies[0].branch)
        assertEquals("artifact", context.dependencies[0].artifact)
    }

    @Test
    void "Adding a dependency with a path"() {
        context.dependency(
                project: "project",
                path: "path/to/file"
        )
        assertEquals(1, context.dependencies.size())
        assertEquals("project", context.dependencies[0].project)
        assertEquals("path/to/file", context.dependencies[0].targetPath)
    }

    @Test
    void "Adding a dependency with multiple paths"() {
        context.dependency(
                project: "project",
                path: ["path1", "path2"]
        )
        assertEquals(1, context.dependencies.size())
        assertEquals("project", context.dependencies[0].project)
        assertEquals("path1,path2", context.dependencies[0].targetPath)
    }

    @Test
    void "Loading from YAML using configurations"() {
        dsl.readYaml = { Map params ->
            assertEquals("path/to/yaml", params.file)
            return [
                    configurations: [
                            [project: "p1"],
                            [project: "p2"]
                    ]
            ]
        }
        context.yaml("path/to/yaml")
        assertEquals(2, context.dependencies.size())
        assertEquals("p1", context.dependencies[0].project)
        assertEquals("p2", context.dependencies[1].project)
        assertEquals(1, logs.size())
        assertTrue(logs[0].contains("yaml = "))
    }

    @Test
    void "Loading from YAML using dependencies"() {
        dsl.readYaml = { Map params ->
            assertEquals("path/to/yaml", params.file)
            return [
                    dependencies: [
                            [project: "p1"]
                    ]
            ]
        }
        context.yaml("path/to/yaml")
        assertEquals(1, context.dependencies.size())
        assertEquals("p1", context.dependencies[0].project)
    }

    @Test
    void "Loading from YAML with no configurations nor dependencies"() {
        dsl.readYaml = { Map params ->
            return [:]
        }
        context.yaml("path/to/yaml")
        assertEquals(0, context.dependencies.size())
    }

}

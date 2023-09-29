package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

import org.junit.Test

class AutoVersioningDependencyTest {

    @Test
    void to_string() {
        def dep = new AutoVersioningDependency(
                "source",
                "branch",
                "promotion",
                "path",
                "version = \"(.*)\"",
                null,
                null,
                null,
                false,
                null,
                null,
                null,
                [:],
                null,
                "dep1",
        )
        def s = dep.toString()
        println(s)
    }

    @Test
    void parsing() {
        def yaml =
    }

}
plugins {
    groovy
}

sourceSets {
    main {
        withConvention(GroovySourceSet::class) {
            groovy {
                setSrcDirs(listOf(
                    "src",
                    "vars"
                ))
            }
        }
    }
    test {
        withConvention(GroovySourceSet::class) {
            groovy {
                setSrcDirs(listOf(
                    "test"
                ))
            }
        }
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.jenkins-ci.org/public/")
    }
}

dependencies {
    implementation("org.codehaus.groovy:groovy-all:2.4.15")
    implementation("org.jenkins-ci.main:jenkins-core:2.107.3")
    implementation("org.jenkins-ci.plugins.workflow:workflow-api:2.22@jar")
    implementation("org.jenkins-ci.plugins.workflow:workflow-cps:2.19@jar")
    implementation("org.jenkins-ci.plugins.workflow:workflow-step-api:2.12@jar")
    implementation("org.jenkins-ci.plugins.pipeline-stage-view:pipeline-rest-api:2.10@jar")
}

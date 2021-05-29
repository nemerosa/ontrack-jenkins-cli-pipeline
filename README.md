# Ontrack CLI Jenkins Pipeline Library

Jenkins pipeline library using the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli).

## Installation

### JCasC

JCasC (Jenkins Configuration as Code) is the recommended approach:

```yaml
globalLibraries:
    libraries:
    - name: "ontrack-jenkins-cli-pipeline"
      retriever:
        modernSCM:
          scm:
            github:
              configuredByUrl: true
              credentialsId: "<GitHub credentials>"
              repositoryUrl: "https://github.com/nemerosa/ontrack-jenkins-cli-pipeline"
```

### UI

You can also use the Jenkins management UI to register this library:

![Library UI](docs/ontrack-jenkins-cli-pipeline-install-ui.png)

## Usage

In your `Jenkinsfile`, declare the version of the pipeline library you want to use and start using the [steps](#steps) provided by the library. For example:

```groovy
@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {
    agent any
    stages {
        stage("Setup") {
            steps {
                ontrackSetup()
            }
        }
    }
}
```

### Versioning

In this example, we use the `main` branch of the pipeline library but it's better to stick to:

* versioned branches, like `v1`
* or explicit tags in order to ensure build reproducibility. See the list of available tags in [GitHub](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline/tags).

## Steps

* [`ontrackSetup`](vars/ontrackSetup.md) - setting up Ontrack in your pipeline

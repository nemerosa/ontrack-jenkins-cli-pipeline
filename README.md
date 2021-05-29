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

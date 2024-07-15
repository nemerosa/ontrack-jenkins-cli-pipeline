# Ontrack CLI Jenkins Pipeline Library

Jenkins pipeline library using the [Ontrack](https://github.com/nemerosa/ontrack) GraphQL API.

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

## Setup

The [steps](#steps) provided by this library rely on environment variables and credentials.

Set the `ONTRACK_URL` Jenkins environment variable to the URL to Ontrack.

Either:

* create an `ONTRACK_TOKEN` secret text credentials entry containing the authentication token used to connect to Ontrack
* or reuse an existing credentials entry by referring to its ID in an `ONTRACK_TOKEN_ID` environment variable

By default, projects and branches managed by this pipeline steps will be linked to a GitHub configuration named `github.com`,
using GitHub as an issue tracker.

For other settings, use the following environment variables:

* `ONTRACK_SCM` - defaults to `github` - type of SCM, like `github` or `bitbucket-server`.
* `ONTRACK_SCM_CONFIG` - defaults to `github.com` - name of the SCM configuration in Ontrack.
* `ONTRACK_SCM_JIRA` - using the defaults if not configured - identifier of the issue service configuration. For example, for
  Jira, it looks like `jira//<name of the config>`. So if your Jira configuration in Ontrack is named "MyJira", the
  identifier will be `jira//MyConfig`.

Note that all these settings can be overridden at pipeline level. See [`ontrackCliSetup`](vars/ontrackCliSetup.md) for more information.

## Usage

In your `Jenkinsfile`, declare the version of the pipeline library you want to use and start using the [steps](#steps)
provided by the library. For example:

```groovy
@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {
    agent any
    stages {
        stage("Setup") {
            steps {
                // Ontrack connection & branch setup
                ontrackCliSetup()
            }
        }
        stage("Preparation") {
            steps {
                // Computing a version in VERSION
                // Ontrack build entry creation
                ontrackCliBuild(release: env.VERSION)
            }
            post {
                always {
                    // Validation run, including detection of the 
                    // status and run information
                    ontrackCliValidate(stamp: 'STAMP')
                }
            }
        }
    }
}
```

### Logging

Most of the [steps](#steps) accept a `logging` parameter but logging can be configured globally using
the `ONTRACK_LOGGING` environment variable, set to `false` or `true` (`false` is the default).

> Note that any `logging` parameter on the step will override the global settings.

Typically, the `ONTRACK_LOGGING` environment variable would be set at pipeline level:

```groovy
pipeline {
    environment {
        ONTRACK_LOGGING = true
    }
}
```

### Versioning

In this example, we use the `main` branch of the pipeline library but it's better to stick to:

* versioned branches, like `v1`
* or explicit tags in order to ensure build reproducibility. See the list of available tags
  in [GitHub](https://github.com/nemerosa/ontrack-jenkins-cli-pipeline/tags).

## Steps

### General setup

* [`ontrackCliSetup`](vars/ontrackCliSetup.md) - general purpose setup task to set up Ontrack in your pipeline, from
  creating common environment variables to initializing project and branch in Ontrack for your pipeline.
* [`ontrackCliSetupSonarQube`](vars/ontrackCliSetupSonarQube.md) - setup of SonarQube properties at project level

### Creating Ontrack items

* [`ontrackCliBuild`](vars/ontrackCliBuild.md) - creates an Ontrack build entry based on current information
* [`ontrackCliPromote`](vars/ontrackCliPromote.md) - creates an Ontrack promotion run for the current build, based on current information or provided information
* [`ontrackCliValidate`](vars/ontrackCliValidate.md) - creates an Ontrack validation run for the current build, based on current information or provided information
* [`ontrackCliValidateTests`](vars/ontrackCliValidateTests.md) - creates an Ontrack validation run based on JUnit test
  results
* [`ontrackCliValidateWithTestResults`](vars/ontrackCliValidateWithTestResults.md) - same as above but reuses existing
  JUnit tst results
* [`ontrackCliValidateCHML`](vars/ontrackCliValidateCHML.md) - creates an Ontrack validation run based on
  critical/high/medium/low stats
* [`ontrackCliValidatePercentage`](vars/ontrackCliValidatePercentage.md) - creates an Ontrack validation run based on a
  percentage
* [`ontrackCliValidateMetrics`](vars/ontrackCliValidateMetrics.md) - creates an Ontrack validation run based on a map of
  metrics

### Setting properties on builds

* [`ontrackCliBuildMessage`](vars/ontrackCliBuildMessage.md) - setting a message property on an existing build
* [`ontrackCliBuildMetaInfo`](vars/ontrackCliBuildMetaInfo.md) - setting or updating a meta-info property on an existing
  build
* [`ontrackCliBuildGitCommit](vars/ontrackCliBuildGitCommit.md) - setting or updating the "Git commit" property on an
  existing build

### Build links

* [`ontrackCliBuildLink`](vars/ontrackCliBuildLink.md) - creates a link to another build
* [`ontrackCliBuildLinks`](vars/ontrackCliBuildLinks.md) - creates several link other builds

### Accessing Ontrack information

* [`ontrackCliLastBranch`](vars/ontrackCliLastBranch.md) - getting the last branch for given pattern
* [`ontrackCliLastPromotion`](vars/ontrackCliLastPromotion.md) - getting the last build for a given promotion
* [`ontrackCliLastPromotionByProject`](vars/ontrackCliLastPromotionByProject.md) - getting the last build for a given
  promotion over all branches
* [`ontrackCliGetBuildByVersion`](vars/ontrackCliGetBuildByVersion.md) - getting a build using its release/label/version
  property
* [`ontrackCliGetBuildByMetaVersion`](vars/ontrackCliGetBuildByMetaVersion.md) - getting a build using a version stored
  in its meta information
* [`ontrackCliGetBuildByProjectAndVersion`](vars/ontrackCliGetBuildByProjectAndVersion.md) - getting a build using its
  release/label/version property inside a project
* [`ontrackCliGetBuildByCommit`](vars/ontrackCliGetBuildByCommit.md) - getting a build in the current branch using its
  commit

### Auto versioning setup

* [`ontrackCliAutoVersioning`](vars/ontrackCliAutoVersioning.md) - setting up the auto versioning for the branch
* [`ontrackCliAutoVersioningProject`](vars/ontrackCliAutoVersioningProject.md) - setting up the auto versioning rules at
  project level
* [`ontrackCliAutoVersioningCheck`](vars/ontrackCliAutoVersioningCheck.md) - checking the auto versioning alignment and
  creating a corresponding validation

### Setup of notifications

* [`ontrackCliSetupProjectNotifications`](vars/ontrackCliSetupProjectNotifications.md) - setup of notifications at
  project level
* [`ontrackCliSetupBranchNotifications`](vars/ontrackCliSetupBranchNotifications.md) - setup of notifications at branch
  level
* [`ontrackCliSetupPromotionLevelNotifications`](vars/ontrackCliSetupPromotionLevelNotifications.md) - setup of
  notifications at promotion level
* [`ontrackCliSetupValidationStampNotifications`](vars/ontrackCliSetupValidationStampNotifications.md) - setup of
  notifications at validation stamp level

### Generic steps

* [`ontrackCliGraphQL`](vars/ontrackCliGraphQL.md) - performs a GraphQL call to Ontrack and returns the JSON response

### Orchestration steps

> These steps are very specific in the kind of scenario they are running and interact with more than Ontrack.

* [`ontrackCliCheckoutConditionalOnPromotionAndTrigger`](vars/ontrackCliCheckoutConditionalOnPromotionAndTrigger.md) -
  performs a SCM checkout based on the nature of the trigger

### CLI steps

Those steps allow to download and the setup the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli).

* [`ontrackCliDownload`](vars/ontrackCliDownload.md) - downloads
  the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli) and sets it into the path
* [`ontrackCliConnect`](vars/ontrackCliConnect.md) - creates a connection configuration for the Ontrack CLI, based on
  provided information or the environment

## Ignoring errors

You can run the Ontrack steps in a mode where any error is ignored.

> Note that steps _returning_ values will then return a null or empty value. This
> may cause some issues in some pipelines relying on these values, so it's not a perfect solution.

Set the `ONTRACK_IGNORE_ERRORS` global environment variable to `true`.

## Failsafe

In case you want to utterly disable all the Ontrack steps, in all pipelines, to do something and in particular,
you want to remove any connection to the Ontrack server, you can set the `ONTRACK_STOP` global environment
variable to `true`.

> Like when [ignoring errors](#ignoring-errors), the steps _returning_ values will return a null or empty value. This
> may cause some issues in some pipelines relying on these values, so it's not a perfect solution.

## Environment variables

| Variable              | Default value                    | Description                                                                    |
|-----------------------|----------------------------------|--------------------------------------------------------------------------------|
| ONTRACK_LOGGING       | `false`                          | Enabling by default logging for all Ontrack operations                         |
| ONTRACK_URL           | _Required_                       | URL to the Ontrack server                                                      |
| ONTRACK_TOKEN         | _Required_ or `ONTRACK_TOKEN_ID` | (credentials entry) API token used to connect to Ontrack                       |
| ONTRACK_TOKEN_ID      | _Required_ or `ONTRACK_TOKEN`    | ID of a text credentials entry containing API token used to connect to Ontrack |
| ONTRACK_STOP          | `false`                          | See [Failsafe](#failsafe)                                                      |
| ONTRACK_IGNORE_ERRORS | `false`                          | See [Ignoring errors](#ignoring-errors)                                        |
| ONTRACK_USE_LABEL     | `false`                          | See [`ontrackCliSetup`](vars/ontrackCliSetup.md)                               |
| ONTRACK_SCM           | `github`                         | See [`ontrackCliSetup`](vars/ontrackCliSetup.md)                               |
| ONTRACK_SCM_CONFIG    | `github.com`                     | See [`ontrackCliSetup`](vars/ontrackCliSetup.md)                               |
| ONTRACK_SCM_ISSUES    | ``                               | See [`ontrackCliSetup`](vars/ontrackCliSetup.md)                               |

## Change log

### 4.9

> Requires Ontrack >= 4.9

#### 4.9.0

* support for Ontrack subscriptions names

### 2.x

* old versions not linked to a given version of Ontrack
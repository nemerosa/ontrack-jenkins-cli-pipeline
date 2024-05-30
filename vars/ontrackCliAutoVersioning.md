## [`ontrackCliAutoVersioning`](ontrackCliAutoVersioning.groovy)

This step allows to configure the current branch for auto versioning.

> This step relies strongly on [`ontrackCliSetup`](ontrackCliSetup.md) having been called but this is not strictly required and all parameters can be provided explicitly.

### Usage

This step accepts a Groovy `Closure` for its configuration, either programmatically or from a YAML file.

Methods for this closure are:

* `branch(value)` - defines a regular expression which restricts the setup to the branches matching this regular expression. Several calls to this method are possible (to define several regular expressions)

* `dependency(map)` - defines a dependency as a `Map` of parameter values

* `yaml(path)` - uses a YAML file at `path` to define dependencies. The YAML file looks like:

```yaml
dependencies
  - project: ...
    branch: ...
  - project: ...
```

> Refer to the [official documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html) for more information about the parameters.

### Examples

```groovy
ontrackCliAutoVersioning {
    branch "master"
    dependency(
            project: "my-pipeline",
            branch: "main",
            promotion: "GOLD",
            path: "Jenkinsfile",
            regex: "@Library\\(\"my-pipeline@(.*)\"\\) _",
    )
}
```

Using this setup, this branch will be updated every time a new `GOLD` promotion is available on the `main` branch of the `my-pipeline` project. Its version will be set in the `Jenkinsfile` at the place designated by the first capturing group in the indicated regular expression.

The example belows adds some post-processing based on Jenkins:

```groovy
ontrackCliAutoVersioning {
    branch "main"
    dependency(
            project: "my-library",
            branch: "release-1.3",
            promotion: "IRON",
            path: "gradle.properties",
            property: "my-version",
            postProcessing: "jenkins",
            postProcessingConfig: [
                    dockerImage  : "openjdk:8",
                    dockerCommand: "./gradlew clean",
            ]
    )
}
```

The same configuration is are given as YAML below:

```groovy
ontrackCliAutoVersioning {
    branch "main"
    yaml "auto-versioning.yml"
}
```

with `auto-versioning.yml` containing:

```yaml
dependencies:
  - project: my-library
    branch: release-1.3"
    promotion: IRON
    path: gradle.properties
    property: my-version
    postProcessing: jenkins
    postProcessingConfig:
        dockerImage  : openjdk:8
        dockerCommand: ./gradlew clean
```

### Multiple target files

The `path` parameter, both in Groovy and YAML based configurations, can either be one unique path to update or a list of paths to update.

If a list of paths to update is provided, one distinct dependency per path will be created in Ontrack. So the following two declarations are equivalent:

```yaml
dependencies:
  - project: my-library
    branch: release-1.3
    promotion: IRON
    path:
      - gradle.properties
      - dep.properties
    property: my-version
```

is equivalent to:

```yaml
dependencies:
  - project: my-library
    branch: release-1.3
    promotion: IRON
    path: gradle.properties
    property: my-version
  - project: my-library
    branch: release-1.3
    promotion: IRON
    path: dep.properties
    property: my-version
```

### Parameters

* `project` - Ontrack project to watch
* `branch` - name of the branch to take into account for the dependency. Several branches can be selected using a regular expression. If several branches are eligible, only the latest version can be used, based on inverted order of semantic versioning. Branches which do not comply with semantic versioning are discarded.
* `promotion` - promotion to watch
* `path` - comma-separated list of file to update with the new version
* `regex` - regex to use in the target file to identify the line to replace with the new version. It must have a capturing group in position 1, which will be replaced by the actual version. For example: `coreVersion = (.*)`
* `property` - can be used instead of the `regex` when we consider property files. In the sample above, the target property can be set to `coreVersion`
* `propertyRegex` - when `property` is used, `propertyRegex` can define a regular expression to extract / update the actual version from/into the property value. The regular expression must contain at least one capturing group, holding the actual version value. This `propertyRegex` is useful for cases when the version is part of a bigger string, for example, for a Docker image qualified name. Example: when `property = "repository/image:tag"`, to target the tag, you can use `propertyRegex: "repository\/image\:(.*)"`
* `propertyType` - when `property` is set, defines how the target file must be handled. For example, it could be a dependency notation in a NPM `package.json` file, or a property entry in Java properties file for Gradle. For NPM, use `npm`. For Java properties, use `properties`. When not specified, it defaults to `properties`. Other types are available (see the [Ontrack documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#auto-versioning-config-type))
* `autoApproval` - check if the PR must be approved automatically or not (`true` by default)
* `upgradeBranchPattern` - prefix to use for the upgrade branch in Git, defaults to `feature/auto-upgrade-<project>-<version>`. If set manually, the `<project>` and `<version>` tokens can be used to be replaced respectively# by the dependency project (the `project` above) and the actual version. Only the `<version>` token is required.
* `postProcessing` - type of post-processing to launch after the version has been updated - see the [Ontrack documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#auto-versioning-post-processing)
* `postProcessingConfig` - configuration of the post-processing - see the [Ontrack documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#auto-versioning-post-processing)
* `validationStamp` - ff defined, will create a validation stamp in the target branch being upgraded. If this parameter is set to `auto`, the validation stamp name will be created automatically from the source project, with `auto-versioning-` as a prefix.
* `autoApprovalMode` - either `CLIENT` or `SCM` - see the [Ontrack documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#auto-versioning-pr)
* `qualifier` - optional qualifier to use when creating a build link for this configuration
* `versionSource` - how must the source build version be computed if not using the default mechanism (label if source project configured or build name). Possible options are:
    * `name` - using the build name
    * `labelOnly` - using the build label only (required)
    * `metaInfo/<category>/<name>` or `metaInfo/<name>` - the version is required to be stored in the meta information of the source build, identified its name and optional category
* `prTitleTemplate` - (optional) template for the title of the auto-versioning PR
* `prBodyTemplate` - (optional) template for the body of the auto-versioning PR
* `prBodyTemplateFormat` - (optional) template format for the body of the auto-versioning PR. By default using text (`plain`) but `html` and `markdown` are also supported.
* `reviewers` - list of user names to add as reviewers to the auto versioning pull requests
* `notifications` - optional list of [notifications](#notifications) to send upon an auto-versioning event

#### Notifications

Each notification object has the following parameters:

* `channel` - id of the channel to use
* `config` - configuration of the channel (as a map)
* `scope` - list of scopes to listen to
* `notificationTemplate` - optional template for the notification's text

### See also

* [Ontrack documentation](https://static.nemerosa.net/ontrack/release/latest/docs/doc/index.html#auto-versioning)

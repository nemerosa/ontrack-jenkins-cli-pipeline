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

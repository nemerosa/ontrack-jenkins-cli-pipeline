## [`ontrackCliAutoVersioning`](ontrackCliAutoVersioning.groovy)

This step allows to configure the current branch for auto versioning.

> This step relies strongly on [`ontrackCliSetup`](ontrackCliSetup.md) having been called but this is not strictly required and all parameters can be provided explicitly.

> This step is redundant when using the new [`ontrackCliCiConfig`](ontrackCliCIConfig.md) step.

### Usage

This step accepts a Groovy `Closure` for its configuration, either programmatically or from a YAML file.

Methods for this closure are:

* `branch(value)` - defines a regular expression which restricts the setup to the branches matching this regular expression. Several calls to this method are possible (to define several regular expressions). Only one of these regular expressions must match the current branch for the auto-versioning to be applied.

* `dependency(map)` - defines a dependency as a `Map` of parameter values

* `yaml(path)` - uses a YAML file at `path` to define dependencies. The YAML file looks like:

```yaml
configurations
  - sourceProject: ...
    sourceBranch: ...
  - sourceProject:: ...
```

> Refer to the [official documentation](https://docs.yontrack.com/yontrack/ref/latest/content/integrations/auto-versioning/auto-versioning.html) for more information about the parameters.

> In version 5.x of this pipeline library, old names for parameters have been deprecated and the ones listed in the documentation are the ones to use.
> They have been in place since version V4 of Yontrack, but the Jenkins library was very lenient in accepting old names and translating them to the new ones.
> 
> Starting with version 5.x of the library, the new names are preferred, but the old ones are still accepted for backward compatibility.
> 
> Starting version 6.x of the library and V6 of Yontrack, the ones will not be accepted anymore.
> 
> List of old names and new names:
> 
> * `project` -> `sourceProject`
> * `branch` -> `sourceBranch`
> * `promotion` -> `sourcePromotion`
> * `path` -> `targetPath`
> * `property` -> `targetProperty`
> * `regex` -> `targetRegex`
> * `propertyType` -> `targetPropertyType`
>
> For the same reasons, in YAML files, `configurations` is now preferred over `dependencies`.

### Examples

```groovy
ontrackCliAutoVersioning {
    branch "master"
    dependency(
            sourceProject: "my-pipeline",
            sourceBranch: "main",
            sourcePromotion: "GOLD",
            targetPath: "Jenkinsfile",
            targetRegex: "@Library\\(\"my-pipeline@(.*)\"\\) _",
    )
}
```

Using this setup, this branch will be updated every time a new `GOLD` promotion is available on the `main` branch of the `my-pipeline` project. Its version will be set in the `Jenkinsfile` at the place designated by the first capturing group in the indicated regular expression.

The example belows adds some post-processing based on Jenkins:

```groovy
ontrackCliAutoVersioning {
    branch "main"
    dependency(
            sourceProject: "my-library",
            sourceBranch: "release-1.3",
            sourcePromotion: "IRON",
            targetPath: "gradle.properties",
            targetProperty: "my-version",
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
configurations:
  - sourceProject: my-library
    sourceBranch: release-1.3"
    sourcePromotion: IRON
    targetPath: gradle.properties
    targetProperty: my-version
    postProcessing: jenkins
    postProcessingConfig:
        dockerImage  : openjdk:8
        dockerCommand: ./gradlew clean
```

### Multiple target files

The `targetPath` parameter, both in Groovy and YAML based configurations, can either be one unique path to update or a list of paths to update, separated by commas.

```yaml
configurations:
  - sourceProject: my-library
    sourceBranch: release-1.3
    sourcePromotion: IRON
    targetPath: gradle.properties,dep.properties
    targetProperty: my-version
```


### Parameters

See the [Yontrack documentation](https://docs.yontrack.com/yontrack/ref/latest/content/integrations/auto-versioning/auto-versioning.html) for the full list of parameters.

### See also

* [Ontrack documentation](https://docs.yontrack.com/yontrack/ref/latest/content/integrations/auto-versioning/auto-versioning.html)
* [`ontrackCliCIConfig`](ontrackCliCIConfig.md) for an easier setup

## [`ontrackCliCIConfig`](ontrackCliCIConfig.groovy)

This step uses the CI context to setup the project, the branch and the build in Yontrack.

> This step is a complete replacement for the [`ontrackCliSetup`](ontrackCliSetup.md) step.

### Parameters

| Parameter       | Type      | Default             | Description                                          |
|-----------------|-----------|---------------------|------------------------------------------------------|
| `config`        | YAML file | `.yontrack/ci.yaml` | Path to the CI configuration (see below)             |
| `scm`           | String    | _None_              | If left empty, the SCM is detected automatically (1) |
| `skipGitCommit` | boolean   | `false`             | See (2)                                              |
| `vars`          | Map       | `[:]`               | Additional variables for the template (see below)    |
| `logging`       | boolean   | `false`             | Set to `true` to display debug / logging information |

(1) If the SCM is not detected automatically, you can specify it with the `scm` parameter using values like `github`,
`bitbucket-server`, etc.

(2) Sometimes, the Git commit is not available in the environment variables seen by Jenkins. By default, the step will
try to get it by running `git rev-parse HEAD`. Use `skipGitCommit: false` to disable this behaviour.

### Templating

The YAML configuration file is treated as a template and rendered using the Groovy `SimpleTemplateEngine`.
This also applies to any file referenced using the `@` syntax in the YAML configuration.

For variables that Jenkins should expand, the recommended syntax is `<%= variable %>` (for simple values)
or `<% print variable %>` (for more complex logic).

The `${...}` syntax is **reserved** for templates that are intended for Yontrack (like notification templates)
and will be preserved as-is in the final output.

The following variables are available in the template:

* any environment variable like `GIT_COMMIT`, `BRANCH_NAME`, etc.
* the `environment` collection, which is a list of maps with `name` and `value` properties.
* any variable provided in the `vars` parameter of the step.
* the `variables` collection, which is a list of maps with `name` and `value` properties, containing the variables provided in the `vars` parameter.

Example of usage in the YAML configuration:

```yaml
version: v1
configuration:
  project:
    properties:
      - type: net.nemerosa.ontrack.extension.general.Property
        data:
          description: "Built from <%= BRANCH_NAME %>"
```

Using custom variables:

```yaml
version: v1
configuration:
  project:
    properties:
      - type: net.nemerosa.ontrack.extension.general.Property
        data:
          description: "Project <%= PROJECT_LABEL %>"
```

Using the `variables` collection:

```yaml
version: v1
configuration:
  project:
    properties:
      - type: net.nemerosa.ontrack.extension.general.Property
        data:
          description: "Release <%= variables.find { it.name == 'RELEASE_VERSION' }.value %>"
```

Preserving Yontrack expressions:

```yaml
version: v1
configuration:
  branch:
    notifications:
      - channel: slack
        channelConfig:
          channel: "#alerts"
        events:
          - promotion_run
        contentTemplate: |
          Yontrack <%= build %> has been released.

          ${promotionRun.changelog?title=true&commitsOption=OPTIONAL}
```

In this example:
* `<%= build %>` is expanded by Jenkins.
* `${promotionRun.changelog...}` is preserved and will be used by Yontrack.

### Outputs

All the following environment variables are set:

* `ONTRACK_PROJECT_ID`
* `ONTRACK_PROJECT_NAME`
* `ONTRACK_BRANCH_ID`
* `ONTRACK_BRANCH_NAME`
* `ONTRACK_BRANCH_DISPLAY_NAME` - typically the SCM branch name
* `ONTRACK_BUILD_ID`
* `ONTRACK_BUILD_NAME` - technical build name
* `ONTRACK_BUILD_DISPLAY_NAME` - typically the value of the `VERSION` environment variable or the technical build name

### Example

Raw usage, with basic and default setup:

```groovy
ontrackCliCIConfig()
```

### Configuration

The configuration is done in a YAML file. The default location is `.yontrack/ci.yaml`.

The very minimal configuration is:

```yaml
version: v1
configuration: { }
```

When using this configuration, Yontrack will create the project, the branch and the build, based on the information
found in the CI context (mostly, the environment variables provided by Jenkins).

You can of course configure way more, like the promotions and validation stamps at the branch level,
with some additional configuration for the release branches:

```yaml
version: v1
configuration:
  branch:
    validations:
      unit-tests:
        tests: { }
    promotions:
      BRONZE:
        validations:
          - unit-tests
```

> For more information about the configuration, see the Yontrack documentation to see how to configure:
> properties, promotions, validation stamps, notifications, workflows, auto-versioning, custom setup, etc.

### Documentation

See the [Yontrack documentation](https://docs.yontrack.com/yontrack/ref/latest/content/configuration/ci-config.html) for
complete information.

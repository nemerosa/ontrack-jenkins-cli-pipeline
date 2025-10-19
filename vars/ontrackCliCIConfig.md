## [`ontrackCliCIConfig`](ontrackCliCIConfig.groovy)

This step uses the CI context to setup the project, the branch and the build in Yontrack.

> This step is a complete replacement for the [`ontrackCliSetup`](ontrackCliSetup.md) step.

### Parameters

| Parameter | Type      | Default             | Description                                          |
|-----------|-----------|---------------------|------------------------------------------------------|
| `config`  | YAML file | `.yontrack/ci.yaml` | Path to the CI configuration (see below)             |
| `scm`     | String    | _None_              | If left empty, the SCM is detected automatically (1) |
| `logging` | boolean   | `false`             | Set to `true` to display debug / logging information |

(1) If the SCM is not detected automatically, you can specify it with the `scm` parameter using values like `github`,
`bitbucket-server`, etc.

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

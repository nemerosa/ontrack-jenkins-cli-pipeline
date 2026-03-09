## [`ontrackCliCIConfig`](ontrackCliCIConfig.groovy)

This step does the same thing as the [`ontrackCliCIConfig`](ontrackCliCIConfig.groovy) step, but for the branch only.

### Parameters

| Parameter       | Type      | Default             | Description                                          |
|-----------------|-----------|---------------------|------------------------------------------------------|
| `config`        | YAML file | `.yontrack/ci.yaml` | Path to the CI configuration (see below)             |
| `scm`           | String    | _None_              | If left empty, the SCM is detected automatically (1) |
| `vars`          | Map       | `[:]`               | Additional variables for the template (see below)    |
| `logging`       | boolean   | `false`             | Set to `true` to display debug / logging information |

(1) If the SCM is not detected automatically, you can specify it with the `scm` parameter using values like `github`,
`bitbucket-server`, etc.

> See [`ontrackCliCIConfig`](ontrackCliCIConfig.groovy) for more information.

### Outputs

All the following environment variables are set:

* `ONTRACK_PROJECT_ID`
* `ONTRACK_PROJECT_NAME`
* `ONTRACK_BRANCH_ID`
* `ONTRACK_BRANCH_NAME`
* `ONTRACK_BRANCH_DISPLAY_NAME` - typically the SCM branch name

### Example

Raw usage, with basic and default setup:

```groovy
ontrackCliCIConfigBranch()
```

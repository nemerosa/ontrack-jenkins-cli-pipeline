## [`ontrackCliBuildRelease`](ontrackCliBuildRelease.groovy)

This step sets the release property on an existing build.

> When using the [`ontrackCliCIConfig`](ontrackCliCIConfig.md) step, the release property is automatically set whenever
> the `VERSION` environment variable is set.
> This step is therefore only useful when the `VERSION` environment variable is set later.

### Parameters

| Parameter | Type    | Default                                     | Description                                                                          |
|-----------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project` | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Yontrack to target                                            |
| `branch`  | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Yontrack to target                                             |
| `build`   | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Yontrack                                            |
| `logging` | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |
| `version` | String  | `VERSION` environment variable              | Value of the release label                                                           |

### Examples

```groovy
ontrackCliSCIConfig()
env.VERSION = "1.0.0"
ontrackCliBuildRelease()
```

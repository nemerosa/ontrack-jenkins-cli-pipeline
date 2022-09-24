## [`ontrackCliAutoVersioningCheck`](ontrackCliAutoVersioningCheck.groovy)

This step checks the auto versioning alignment and creates a corresponding validation for the current build.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter | Type    | Default                                     | Description                                                                          |
|-----------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project` | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`  | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`   | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `logging` | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |

### Examples

```groovy
ontrackCliAutoVersioningCheck()
```

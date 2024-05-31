## [`ontrackCliPromote`](ontrackCliPromote.groovy)

This step creates a promotion run for the current Ontrack build, using the current state of the pipeline.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md)
> and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter     | Type    | Default                                     | Description                                                                          |
|---------------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project`     | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`      | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`       | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `promotion`   | String  | _Required_                                  | Name of the promotion level to set                                                   |
| `description` | String  | ''                                          | Description to set in the promotion run                                              |
| `logging`     | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |

### Example

```groovy
ontrackCliPromote(promotion: 'GOLD')
```

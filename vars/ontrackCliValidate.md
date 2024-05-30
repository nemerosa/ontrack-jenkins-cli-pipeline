## [`ontrackCliValidate`](ontrackCliValidate.groovy)

This step creates a validation run for the current Ontrack build, using the current state of the pipeline.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter          | Type    | Default                                     | Description                                                                          |
|--------------------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project`          | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`           | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`            | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `stamp`            | String  | _Required_                                  | Name of the validation stamp to set                                                  |
| `status`           | String  | _None_                                      | Status of the validation. See the [validation](#validation) section below.           |
| `dataType`         | String  | _None_                                      | FQCN of the validation data type                                                     |
| `data`             | Object  | _None_                                      | Validation data. It'll be serialized as JSON.                                        |
| `dataValidation`   | boolean | `true`                                      | If some validation data is provided, uses the data for the status computation.       | 
| `logging`          | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |
| `tracing`          | boolean | `false`                                     | Set to `true` to display low level information while performing the operation.       |
| `useBuildDuration` | boolean | `false`                                     | Overrides the stage duration computation to use the overall build duration           |

### Validation

By default, the status of the validation is computed from the result of the current Jenkins stage.

If can also be provided explicitly using the `status` parameter or through data validation.

### Example

```groovy
ontrackCliValidate(stamp: 'BUILD')
```

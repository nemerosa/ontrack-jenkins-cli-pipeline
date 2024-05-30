## [`ontrackCliValidateMetrics`](ontrackCliValidateMetrics.groovy)

This step creates a validation run with validation data based on a map of metrics.

### Parameters

| Parameter          | Type               | Default                                     | Description                                                                          |
|--------------------|--------------------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project`          | String             | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`           | String             | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`            | String             | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `stamp`            | String             | _Required_                                  | Name of the validation stamp to set                                                  |
| `metrics`          | Map<String,Double> | _Required_                                  | List of metrics                                                                      |
| `status`           | String             | _None_                                      | Status of the validation. Optional, it will be computed based on the provided data.  |
| `logging`          | boolean            | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |
| `tracing`          | boolean            | `false`                                     | Set to `true` to display low level information while performing the operation.       |
| `useBuildDuration` | boolean            | `false`                                     | Overrides the stage duration computation to use the overall build duration           |

### Outputs

None.

### Example

```groovy
ontrackCliValidateMetrics(
        stamp: 'COVERAGE',
        metrics: [
                ui: 85,
                backend: 56.8,
        ]
)
```

## [`ontrackCliValidateWithTestResults`](ontrackCliValidateWithTestResults.groovy)

This step creates a validation run with validation data based on JUnit test results which have already
been parsed and made available.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter               | Type    | Default                                     | Description                                                                          |
|-------------------------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project`               | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`                | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`                 | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `stamp`                 | String  | _Required_                                  | Name of the validation stamp to set                                                  |
| `results`               | Object  | _Optional_                                  | Object returned by the `junit` step or `null` if not available                       |
| `ignoreStatusIfResults` | boolean | `true`                                      | If the `results` are defined, uses them to compute the status                        |
| `useBuildDuration`      | boolean | `false`                                     | Overrides the stage duration computation to use the overall build duration           |
| `status`                | String  | _None_                                      | Status of the validation. Optional, it will be computed based on the test results.   |
| `logging`               | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |

### Outputs

None.

### Example

```groovy
def testSummary = junit(/* ... */)
ontrackCliValidateWithTestResults(
    stamp: 'tests',
    status: 'FAILED',
    results: testSummary,
    ignoreStatusIfResults: true,
    useBuildDuration: true,
)
```

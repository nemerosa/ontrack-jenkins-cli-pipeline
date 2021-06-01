## [`ontrackCliValidateTests`](ontrackCliValidateTests.groovy)

This step creates a validation run with validation data based on JUnit test results.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `branch` | String | `ONTRACK_BRANCH_NAME` environment variable | Name of the branch in Ontrack to target |
| `build` | String | `ONTRACK_BUILD_NAME` environment variable | Name of the build to validate in Ontrack |
| `stamp` | String | _Required_ | Name of the validation stamp to set |
| `pattern` | String | `**/build/test-results/**/*.xml` | ANT pattern to get the test results |
| `allowEmptyResults` | boolean | `true` | If empty test results are allowed. |
| `status` | String | _None_ | Status of the validation. Optional, it will be computed based on the test results. |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |
| `tracing` | boolean | `false` | Set to `true` to display low level information while performing the operation. |

### Outputs

The JUnit results.

### Example

With a custom path:

```groovy
ontrackCliValidateTests(stamp: 'BUILD', pattern: 'build/test-results/**/*.xml')
```

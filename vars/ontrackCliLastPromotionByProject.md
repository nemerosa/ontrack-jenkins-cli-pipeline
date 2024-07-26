## [`ontrackCliLastPromotionByProject`](ontrackCliLastPromotionByProject.groovy)

This step returns the last build having a given promotion on a given project
(regardless of the branch).

### Parameters

| Parameter   | Type    | Default                                               | Description                                                                             |
|-------------|---------|-------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `project`   | String  | Value of the `ONTRACK_PROJECT_NAME` environment value | Name of the Ontrack project (see [`ontrackCliSetup`](ontrackCliSetup.md))               |
| `promotion` | String  | _Required_                                            | Name of the promotion                                                                   |
| `injectEnv` | Boolean | `false`                                               | If `true`, results are injected as environment variables (see [output below](#outputs)) |
| `logging`   | boolean | `false`                                               | Set to `true` to display debug / logging information while performing the call.         |

### Outputs

This step returns a JSON object describing the build. It can be `null` if the build does not exist. If not, this object has the following properties:

| Property  | Type   | Description                                            |
|-----------|--------|--------------------------------------------------------|
| `id`      | String | ID of the build                                        |
| `branch`  | String | Name of the branch of the build                        |
| `name`    | String | Name of the build                                      |
| `release` | String | (optional) Release/label/version attached to the build |
| `commit`  | String | (optional) Git commit full SHA attached to the build   |

If the [`injectEnv`](#parameters) parameter is set to `true`, the following environment variables are injected:

* `ONTRACK_BUILD_LAST_PROMOTION` - `true` if the build is defined, `false` otherwise
* `ONTRACK_BUILD_ID` - ID of the build if defined
* `ONTRACK_BUILD_BRANCH_NAME` - name of the branch of the build if defined
* `ONTRACK_BUILD_NAME` - name of the build if defined
* `ONTRACK_BUILD_RELEASE` - release of the build if defined
* `ONTRACK_BUILD_COMMIT` - commit of the build if defined

### Example

```groovy
ontrackCliSetup(/* ... */)
def build = ontrackCliLastPromotionByProject(promotion: 'BRONZE')
```

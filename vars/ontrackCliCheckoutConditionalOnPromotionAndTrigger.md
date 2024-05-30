## [`ontrackCliCheckoutConditionalOnPromotionAndTrigger`](ontrackCliCheckoutConditionalOnPromotionAndTrigger.groovy)

This step performs a SCM checkout based on the following conditions:

* if the build was triggered manually:
  * normal checkout
  * uses the GIt commit to identify any Ontrack build
* if not triggered manually:
  * getting the latest promotion on the current branch
  * getting its commit and checking out this very commit

This can be used for accessory pipelines which can be triggered:

* manually - in such a case, we want to use the latest commit and if possible, identity a target Ontrack build
* automatically - in such case, we want to use the exact commit with the target promotion

### Parameters

| Parameter   | Type    | Default                                              | Description                                                                                              |
|-------------|---------|------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| `project`   | String  | _Required_                                           | Name of the Ontrack project (required since we cannot "compute" the project name without a SCM checkout) |
| `promotion` | String  | _Required_                                           | Promotion to use to identity the target Ontrack build in case of automated build.                        |
| `logging`   | boolean | `false`                                              | Set to `true` to display debug / logging information while performing the call.                          |

### Outputs

This step returns a JSON object describing the build. It can be `null` if the build does not exist. If not, this object has the following properties:

| Property  | Type   | Description                                            |
|-----------|--------|--------------------------------------------------------|
| `id`      | int    | ID of the build                                        |
| `name`    | String | Name of the build                                      |
| `release` | String | (optional) Release/label/version attached to the build |
| `commit`  | String | (optional) Git commit full SHA attached to the build   |
| `branch`  | String | Name of the Ontrack branch of the build                |

The following environment variables are injected:

* `ONTRACK_BUILD_ID` - name of the build if defined
* `ONTRACK_BUILD_NAME` - name of the build if defined
* `ONTRACK_BUILD_RELEASE` - release of the build if defined
* `ONTRACK_BUILD_COMMIT` - commit of the build if defined

Additionally, the [`ontrackCliSetup`](ontrackCliSetup.md) step is called and set the following environment variables:

* `ONTRACK_PROJECT_NAME`
* `ONTRACK_BRANCH_NAME`

### Example

```groovy
def ontrackBuild = ontrackCliCheckoutConditionalOnPromotionAndTrigger(
        project: 'liteevolutiontfr',
        promotion: 'BRONZE',
)
```

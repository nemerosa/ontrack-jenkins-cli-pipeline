## [`ontrackCliGetBuildByMetaVersion`](ontrackCliGetBuildByMetaVersion.groovy)

This step returns a build using a version stored in its meta information

### Parameters

| Parameter   | Type    | Default                                               | Description                                                                             |
|-------------|---------|-------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `project`   | String  | Value of the `ONTRACK_PROJECT_NAME` environment value | Name of the Ontrack project (see [`ontrackCliSetup`](ontrackCliSetup.md))               |
| `branch`    | String  | Value of the `ONTRACK_BRANCH_NAME` environment value  | Name of the Ontrack branch (see [`ontrackCliSetup`](ontrackCliSetup.md))                |
| `version`   | String  | _Required_                                            | Value of the release/label/version property to look for                                 |
| `injectEnv` | Boolean | `false`                                               | If `true`, results are injected as environment variables (see [output below](#outputs)) |
| `logging`   | boolean | `false`                                               | Set to `true` to display debug / logging information while performing the call.         |

### Outputs

This step returns a JSON object describing the build. It can be `null` if the build does not exist. If not, this object has the following properties:

| Property  | Type   | Description                                            |
|-----------|--------|--------------------------------------------------------|
| `id`      | int    | ID of the build                                        |
| `name`    | String | Name of the build                                      |
| `release` | String | (optional) Release/label/version attached to the build |
| `commit`  | String | (optional) Git commit full SHA attached to the build   |

If the [`injectEnv`](#parameters) parameter is set to `true`, the following environment variables are injected:

* `ONTRACK_BUILD_ID` - name of the build if defined
* `ONTRACK_BUILD_NAME` - name of the build if defined
* `ONTRACK_BUILD_RELEASE` - release of the build if defined
* `ONTRACK_BUILD_COMMIT` - commit of the build if defined

### Example

```groovy
ontrackCliSetup(/* ... */)
def build = ontrackCliGetBuildByMetaVersion(
        project: 'other-project',
        branch: 'other-branch',
        version: 'mvnVersion:1.0.0'
)
```

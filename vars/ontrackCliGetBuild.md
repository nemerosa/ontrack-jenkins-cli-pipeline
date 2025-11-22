## [`ontrackCliGetBuild`](ontrackCliGetBuild.groovy)

This step returns a build from the project, branch and build name.

### Parameters

| Parameter | Type    | Default                                     | Description                                          |
|-----------|---------|---------------------------------------------|------------------------------------------------------|
| `project` | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target             |
| `branch`  | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target              |
| `build`   | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack             |
| `logging` | boolean | `false`                                     | Set to `true` to display debug / logging information |

### Output

This step returns a JSON object describing the build. It can be `null` if the build does not exist. If not, this object has the following properties:

| Property  | Type   | Description                                            |
|-----------|--------|--------------------------------------------------------|
| `id`      | String | ID of the build                                        |
| `name`    | String | Name of the build                                      |
| `branch`  | String | Name of the branch of the build                        |
| `release` | String | (optional) Release/label/version attached to the build |
| `commit`  | String | (optional) Git commit full SHA attached to the build   |

### Examples

```groovy
def build = ontrackCliGetBuild()
```

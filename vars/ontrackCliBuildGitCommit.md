## [`ontrackCliBuildGitCommit`](ontrackCliBuildGitCommit.groovy)

This step sets the "Git commit" property on an existing build.

> In most of the cases, the [`ontrackCliBuild`](ontrackCliBuild.md) step is enough to set this property,
> based on the `GIT_COMMIT` environment variable. However, in some rare cases, the property cannot be set
> immediately (for example, for a Maven release build where the actual commit to be tagged is actually
> created _later during_ the build).

### Parameters

| Parameter   | Type    | Default                                     | Description                                                                          |
|-------------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project`   | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`    | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `build`     | String  | `ONTRACK_BUILD_NAME` environment variable   | Name of the build to validate in Ontrack                                             |
| `logging`   | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |
| `gitCommit` | String  | `GIT_COMMIT` environment variable           | Full commit SHA                                                                      |

### Examples

```groovy
ontrackCliSetup(/*...*/)
ontrackCliBuild(/*...*/ gitCommit: 'none') // Not setting a commit property now
/* Setting the GIT_COMMIT another way */
ontrackCliBuildGitCommit()
```

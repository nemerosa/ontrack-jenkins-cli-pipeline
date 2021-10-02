## [`ontrackCliBuildLink`](ontrackCliBuildLink.groovy)

Creates a link to another build from the current build.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `build` | String | `ONTRACK_BUILD_NAME` environment variable | Name of the build to link from in Ontrack |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |
| `toProject` | String | _Required_ | Name of the project to link to |
| `toBuild` | String | _Required_ | Name of the build to link to |

### Examples

```groovy
ontrackCliSetup(...)
ontrackCliBuild(...)
ontrackCliBuildLink toProject: 'target', toBuild: '...'
```

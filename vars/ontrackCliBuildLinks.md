## [`ontrackCliBuildLinks`](ontrackCliBuildLinks.groovy)

Creates a link to another builds from the current build.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `build` | String | `ONTRACK_BUILD_NAME` environment variable | Name of the build to link from in Ontrack |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |
| `to` | List of targets (see below) | `[]` | List of targets for the links |

Each target in the `to` list is an object with two required fields:

* `project` - target project name
* `build` - target build name

### Examples

```groovy
ontrackCliSetup(...)
ontrackCliBuild(...)
ontrackCliBuildLinks to: [
        [project: 'target-1', build: '100'],
        [project: 'target-2', build: '200'],
]
```

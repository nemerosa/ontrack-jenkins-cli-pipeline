## [`ontrackCliBuild`](ontrackCliBuild.groovy)

This step creates an Ontrack build based on the available information in your pipeline.

> This step relies strongly on [`ontrackCliSetup`](ontrackCliSetup.md) having been called but this is not strictly required and all parameters can be provided explicitly.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `branch` | String | `ONTRACK_BRANCH_NAME` environment variable | Name of the branch in Ontrack to target |
| `name` | String | `BUILD_NUMBER` environment variable | Name of the build to create in Ontrack |
| `release` | String | _None_ | If provided, will attach a release property to the build. |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |

### Output

The `ONTRACK_BUILD_NAME` will contain the name of the created build.

### Example

In most of the cases, the following call is enough after [`ontrackCliSetup`](ontrackCliSetup.md) has been called before in the pipeline:

```groovy
ontrackCliBuild()
```

If a release property must be attached to the build, you can use the `release` parameter:

```groovy
ontrackCliBuild(release: env.VERSION)
```

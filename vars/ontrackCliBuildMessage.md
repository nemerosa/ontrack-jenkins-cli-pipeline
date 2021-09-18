## [`ontrackCliBuildMessage`](ontrackCliBuildMessage.groovy)

This step sets a message property on an existing build.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `branch` | String | `ONTRACK_BRANCH_NAME` environment variable | Name of the branch in Ontrack to target |
| `build` | String | `ONTRACK_BUILD_NAME` environment variable | Name of the build to validate in Ontrack |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |
| `message` | String or Object - see below | _Required_ | Attaches the message property to the build |

The `message` property can either be:

* a `String` - in this case, the message type will be `INFO`
* an object containing two fields:
    * `text` - the message content, required
    * `type` - the message type, defaults to `INFO` but can also be `WARNING` or `ERROR`

### Examples

```groovy
ontrackCliSetup(...)
ontrackCliBuild(...)
ontrackCliBuildMessage message: 'This an information message'
ontrackCliBuildMessage message: [text: 'This a warning message', type: 'WARNING']
```

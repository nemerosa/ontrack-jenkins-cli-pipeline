## [`ontrackCliBuild`](ontrackCliBuild.groovy)

This step creates an Ontrack build based on the available information in your pipeline.

> This step relies strongly on [`ontrackCliSetup`](ontrackCliSetup.md) having been called but this is not strictly required and all parameters can be provided explicitly.

### Parameters

#### General parameters

| Parameter | Type    | Default                                     | Description                                                                          |
|-----------|---------|---------------------------------------------|--------------------------------------------------------------------------------------|
| `project` | String  | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                                             |
| `branch`  | String  | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                                              |
| `name`    | String  | _See below_                                 | Name of the build to create in Ontrack                                               |
| `logging` | boolean | `false`                                     | Set to `true` to display debug / logging information while performing the operation. |

If not provided, the name of the build is computed using `<branch>-<timestamp>`:

* `<branch>` is the normalized branch name (for example, `release/1.27` becomes `release-1.27`)
* `<timestamp>` is the current timestamp formatted as `yyyyMMddHHmmSS`

> Before, the name was made equal to the `BUILD_TAG` environment variable; this proved to be not unique enough since the build number may be reset to 1 and
> lead to some cycles and confiucts.

#### Properties

Extra parameters allow some properties to be set on the build at creation time. Specific steps can also be used, after the build has been created.

| Parameter   | Type                                  | Default                           | Description                                                                                           | Step equivalent                                           |
|-------------|---------------------------------------|-----------------------------------|-------------------------------------------------------------------------------------------------------|-----------------------------------------------------------|
| `release`   | String                                | _None_                            | If provided, will attach a release property to the build.                                             | [`ontrackCliBuildRelease`](ontrackCliBuildRelease.md)     |
| `gitCommit` | String                                | `GIT_COMMIT` environment variable | Git commit property to set for the build. If value is `none`, no Git commit property will be created. | [`ontrackCliBuildGitCommit`](ontrackCliBuildGitCommit.md) |
| `message`   | String or [Object](#message-property) | _None_                            | Attaches the message property to the build. See the [details](#message-property) below                | [`ontrackCliBuildMessage`](ontrackCliBuildMessage.md)     |
| `metaInfo`  | [List or Map](#meta-info-property)    | _None_                            | Attaches some meta-information to the created build                                                   | [`ontrackCliBuildMetaInfo`](ontrackCliBuildMetaInfo.md)   |

##### Message property

The `message` property can either be:

* a `String` - in this case, the message type will be `INFO`
* an object containing two fields:
  * `text` - the message content, required
  * `type` - the message type, defaults to `INFO` but can also be `WARNING` or `ERROR`

Examples:

````groovy
ontrackCliBuild message: 'This an information message'
ontrackCliBuild message: [text: 'This a warning message', type: 'WARNING']
````

##### Meta info property

The `metaInfo` parameter is either:

* a _list_ of objects having the following properties:
  * `name` - required - the key for the meta-info item
  * `value` - required - the value for the meta-info item
  * `link` - optional - a link to associate with the meta-info item
  * `category` - optional - the category for the meta-info item
* a _map_ of `name` x `value` items (using this syntax, no link nor category can be provided)

Examples:

```groovy
// List syntax
ontrackCliBuild metaInfo: [
        [
                name: 'name-1',
                value: 'value-1',
                link: 'A link',
                category: 'Some category',
        ],
        [
                name: 'name-2',
                value: 'value-2',
        ]
]
// Map syntax
ontrackCliBuild metaInfo: [
        'name-1': 'value-1',
        'name-2': 'value-2',
]
```

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

## [`ontrackCliBuildMetaInfo`](ontrackCliBuildMetaInfo.groovy)

This step sets or updates a meta-info properties on an existing build.

> Note that this step works best after [`ontrackCliSetup`](ontrackCliSetup.md) and [`ontrackCliBuild`](ontrackCliBuild.md) have been called before.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target |
| `branch` | String | `ONTRACK_BRANCH_NAME` environment variable | Name of the branch in Ontrack to target |
| `build` | String | `ONTRACK_BUILD_NAME` environment variable | Name of the build to validate in Ontrack |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the operation. |
| `append` | boolean | `true` | Set to `false` to override the existing meta-info properties |
| `metaInfo` | List or Map | _Required_ | Attaches some meta-information to the created build |

The `metaInfo` parameter is either:

* a _list_ of objects having the following properties:
  * `name` - required - the key for the meta-info item
  * `value` - required - the value for the meta-info item
  * `link` - optional - a link to associate with the meta-info item
  * `category` - optional - the category for the meta-info item
* a _map_ of `name` x `value` items (using this syntax, no link nor category can be provided)

### Examples

```groovy
ontrackCliSetup(...)
ontrackCliBuild(...)
// List syntax
ontrackCliBuildMetaInfo metaInfo: [
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
ontrackCliBuildMetaInfo metaInfo: [
        'name-1': 'value-1',
        'name-2': 'value-2',
]
// Replacing existing list
ontrackCliBuildMetaInfo metaInfo: [
        'name-1': 'value-1',
        'name-2': 'value-2',
], append: false
```

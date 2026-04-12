## [`ontrackCliChangelogSincePromotion`](ontrackCliChangelogSincePromotion.groovy)

This step returns a changelog from the current build since a last promotion.

### Parameters

| Parameter   | Type        | Default                                     | Description                                              |
|-------------|-------------|---------------------------------------------|----------------------------------------------------------|
| `project`   | String      | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target                 |
| `branch`    | String      | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to target                  |
| `build`     | String      | `ONTRACK_BUILD_NAME` environment variable   | Name or display name of the build to validate in Ontrack |
| `logging`   | boolean     | `false`                                     | Set to `true` to display debug / logging information     |
| `promotion` | String      | _Required_                                  | Promotion to get the changelog from                      |
| `renderer`  | String      | `plain`                                     | Renderer for the changelog (1)                           |
| `config`    | _See below_ | _See below_                                 | Configuration for the rendering                          |

(1) Available renderers: `plain`, `markdown`, `html`, `jira`, `slack`, ...

The `config` parameters are:

| Parameter                | Type                             | Default                                 | Description                                                                                                                                                                                     |
|--------------------------|----------------------------------|-----------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| empty                    | String                           | `""`                                    | String to use to render an empty or non existent change log                                                                                                                                     |
| dependencies             | List<String>                     | `emptyList()`                           | Comma-separated list of project links to follow one by one for a get deep change log. Each item in the list is either a project name, or a project name and qualifier separated by a colon (:). |
| title                    | Boolean                          | `false`                                 | Include a title for the change log                                                                                                                                                              |
| allQualifiers            | Boolean                          | `false`                                 | Loop over all qualifiers for the last level of `dependencies`, including the default one. Qualifiers at `dependencies` take precedence.                                                         |
| defaultQualifierFallback | Boolean                          | `false`                                 | If a qualifier has no previous link, uses the default qualifier (empty) qualifier.                                                                                                              |
| commitsOption            | ChangeLogTemplatingCommitsOption | `ChangeLogTemplatingCommitsOption.NONE` | Defines how to render commits for a change log (2)                                                                                                                                              |

(2) Available options: `NONE`, `ALWAYS`, `OPTIONAL`

### Output

The changelog as a string.

### Examples

```groovy
String changelog = ontrackCliChangelogSincePromotion(
        promotion: 'RELEASE',
        renderer: 'markdown',
        config: [
                title        : true,
                commitsOption: "ALWAYS",
        ],
)
```

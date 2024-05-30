## [`ontrackCliAutoVersioningProject`](ontrackCliAutoVersioningProject.groovy)

This step allows to configure auto-versioning rules at project level.

> This step relies strongly on [`ontrackCliSetup`](ontrackCliSetup.md) having been called but this is not strictly
> required and all parameters can be provided explicitly.

### Parameters

| Parameter          | Type            | Default                                     | Description                                                                                                                                                                |
|--------------------|-----------------|---------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `project`          | String          | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to target, usually provided by the [`ontrackCliSetup`](ontrackCliSetup.md) step                                                             |
| `branchIncludes`   | List<String>    | `[]`                                        | List of regular expressions. AV requests match if at least one regular expression is matched by the target branch name. If empty, all target branches match (the default). |
| `branchExcludes`   | List<String>    | `[]`                                        | List of regular expressions. AV requests match if no regular expression is matched by the target branch name. If empty, the target branch is considered matching.          |
| `lastActivityDate` | Date/time (UTC) | _None_                                      | If defined, any target branch whose last activity (last build creation) is before this date will be ignored by the auto-versioning                                         |
| `logging`          | boolean         | `false`                                     | Set to `true` to display debug / logging information while performing the operation.                                                                                       |

Note: the list of branches are the _Ontrack branch names_, not the SCM branch names.

### Output

None.

### Example

```groovy
ontrackCliAutoVersioningProject(
        branchIncludes: ['release-.*'],
        branchExcludes: ['release-1.*'],
        lastActivityDate: '2023-12-31T12:00:00',
)
```

## [`ontrackCliLastBranch`](ontrackCliLastBranch.groovy)

This step returns the last branch of a project based on a regex pattern.

> Note that the Ontrack branch name is used for matching and the value being returned.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---
| `project` | String | Value of the `ONTRACK_PROJECT_NAME` environment value | Name of the Ontrack project |
| `pattern` | String | `.*` | Regular expression to match against the Ontrack branch name |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the call. |

### Outputs

This step returns the name of the last Ontrack branch of the project which matches the pattern regular expression. `null` is returned if nothing is found.

### Example

```groovy
String branch = ontrackCliLastBranch(pattern: "release-.*")
```

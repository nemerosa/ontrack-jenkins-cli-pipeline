## [`ontrackCliGraphQL`](ontrackCliGraphQL.groovy)

This steps performs a GraphQL call to Ontrack and returns the JSON response.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `query` | String | _Required_ | The GraphQL query or mutation |
| `variables` | Map | _Optional_ | Map of GraphQL variables to pass to the call |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the call. |

### Outputs

The response as a JSON object.

### Example

To get the list of projects in Ontrack:

```groovy
script {
    def answer = ontrackCliGraphQL(query: '''
        {
            projects {
                name
            }
        }
    ''')
    // Printing all projects
    answer.data.projects.each { project ->
        echo "Project = ${project.name}"
    }
}
```

## [`ontrackCliGraphQL`](ontrackCliGraphQL.groovy)

This steps performs a GraphQL call to Ontrack and returns the JSON response.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `query` | String | _Required_ | The GraphQL query or mutation |
| `variables` | Map | _Optional_ | Map of GraphQL variables to pass to the call |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the call. |
| `url` | String | _Computed_ | Ontrack URL. See [below](#ontrack-url). |
| `token` | String | _Computed_ | Ontrack API token. See [below](#ontrack-credentials). |

### Environment

#### Ontrack URL

`ONTRACK_URL` - the URL of the Ontrack instance to connect to. It'll be typically declared globally but can also be explicitly defined in your pipeline.

#### Ontrack credentials

`ONTRACK_TOKEN` - the API token to use for the connection to Ontrack.

If this environment variable is not defined, this step will look for the `ONTRACK_TOKEN_ID` environment variable (using `ONTRACK_TOKEN` as a default value) in order to identify a credentials entry. This credentials entry will then be used to get the value of the token.

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

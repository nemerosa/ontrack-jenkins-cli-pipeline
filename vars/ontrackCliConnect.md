## [`ontrackCliConnect`](ontrackCliConnect.groovy)

> This step assumes the Ontrack CLI has been downloaded and is available in the PATH, for example with the [`ontrackCliDownload`](ontrackCliDownload.md) step.

This steps configures a connection to an Ontrack instance.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `url` | String | _Required_ or value of the `ONTRACK_URL` environment variable | URL to your Ontrack installation. If `ONTRACK_URL` is available as an environment variable, it will be used. |
| `credentialsId` | String | `ONTRACK_TOKEN` | ID of the Jenkins credentials which contains the authentication token to Ontrack |
| `name` | String | `prod` | Name of the configuration to create to hold the connection parameters.
| `logging` | boolean | `false` | Set to `true` to display logging information while connecting the CLI. |

### Outputs

None.

### Example

If `ONTRACK_URL` is defined as a global environment variable and if 
the `ONTRACK_TOKEN` credentials entry is available, the configuration is as simple as:

```groovy
ontrackCliConnect()
```

With explicit values:

```groovy
ontrackCliConnect(
        url: 'https://ontrack.nemerosa.net',
        credentialsId: 'ONTRACK_TOKEN'
)
```

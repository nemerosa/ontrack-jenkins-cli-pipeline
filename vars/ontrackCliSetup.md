## [`ontrackCliSetup`](ontrackCliSetup.groovy)

This step downloads the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli), puts it on the `PATH` for being used by other steps, configures its connection and performs some basic setup in Ontrack for your very pipeline.

### Parameters

#### General CLI parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `version` | String | _none_ | [Version](https://github.com/nemerosa/ontrack-cli/releases) of the CLI to download. If not specified, the latest release will be downloaded. |
| `executable` | String | `ontrack-cli` | Name of the Ontrack CLI executable to put in the path |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the setup operations. |
| `tracing` | boolean | `false` | Set to `true` to display debug / low level information while performing the setup operations. |

#### Ontrack connection parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `url` | String | _Required_ or value of the `ONTRACK_URL` environment variable | URL to your Ontrack installation. If `ONTRACK_URL` is available as an environment variable, it will be used. |
| `credentialsId` | String | `ONTRACK_TOKEN` | ID of the Jenkins credentials which contains the authentication token to Ontrack |
| `name` | String | `prod` | Name of the configuration to create to hold the connection parameters.

### Outputs

The following environment variables are created:

* `ONTRACK_PROJECT_NAME` - the name of the project in Ontrack
* `ONTRACK_BRANCH_NAME` - the name of the branch in Ontrack
* `ONTRACK_CLI_DIR` - absolute path to the directory where the CLI was downloaded
* `ONTRACK_CLI_NAME` - name of the Ontrack CLI executable file (including any Windows extension)
* `ONTRACK_CLI` - absolute path to the Ontrack CLI

The `PATH` environment variable is appended with the `ONTRACK_CLI_DIR` path.

### Example

Assuming `ONTRACK_URL` is defined and that you can use the `ONTRACK_TOKEN` credentials, setting up Ontrack in your pipeline is as simple as:

```groovy
pipeline {
    agent any
    stages {
        stage("Setup") {
            steps {
                ontrackCliSetup()
            }
        }
    }
}
```

This will:

* download and set the Ontrack CLI in the PATH
* create a connection to Ontrack using the URL defined as `ONTRACK_URL` and the token available in `ONTRACK_TOKEN`


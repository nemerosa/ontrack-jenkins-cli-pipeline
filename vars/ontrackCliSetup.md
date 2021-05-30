## [`ontrackCliSetup`](ontrackCliSetup.groovy)

This step downloads the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli), puts it on the `PATH` for being used by other steps, configures its connection and performs some basic setup in Ontrack for your very pipeline.

### Parameters

#### Setup parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `project` | String | _Computed_ | Name of the Ontrack project. If not provided, it'll be computed from the `GIT_URL` environment variable. The result will be stored into the `ONTRACK_PROJECT_NAME` environment variable. |
| `branch` | String | _Computed_ | Name of the Ontrack branch. If not provided, it'll be computed from the `BRANCH_NAME` environment variable. The result will be stored into the `ONTRACK_BRANCH_NAME` environment variable. |
| `setup` | boolean | `true` | Performs the setup of the project and the branch in Ontrack, based on the provided information. It's enabled by default but can be disabled if you prefer to do this yourself using the Ontrack CLI directly. |
| `autoValidationStamps` | String | _None_ | This option allows the setup of the automatic creation of validation stamps for all branches in the Ontrack project. See [auto validation stamps](#auto-validation-stamps) for the possible values. |
| `autoPromotionLevels` | boolean | _None_ | This option allows the setup of the automatic creation of promotion levels for all branches in the Ontrack project. |
| `scm` | String | Value of `ONTRACK_SCM` or `github` | Type of SCM for this project. See [SCM Configuration](#scm-configuration) for more information. |
| `scmIndexation` | int | `30` | SCM indexation interval in minutes. Set to 0 to disable. |
| `scmConfiguration` | String | Value of `ONTRACK_SCM_CONFIG` or `github.com` | Name of the SCM configuration in Ontrack holding connection information about the SCM. |
| `scmIssues` | String | Value of `ONTRACK_SCM_ISSUES` | ID of the issue service to use for mapping issues to commits in Ontrack. Can be provided explicitely, though the `ONTRACK_SCM_ISSUES` environment variable. If not defined, and if using GitHub or GitHub, defaults to `self` (meaning that the GitHub or GitLab issues are used). |

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

### Auto validation stamps

The `autoValidationStamps` parameter can have the following values:

* `true` - auto creation of validation stamps based on predefined validation stamps
* `force` - auto creation even if no predefined validation stamp if defined (they will be created)
* any other value - disable the auto creation of validation stamps

### SCM configuration

The `scm` parameter defines which type of SCM (GitHub, GitLab, Bitbucket, etc.) must be associated with your pipeline.

By default, if not provided, the value will be `github` or the value stored in the `ONTRACK_SCM` environment variable.

Possible values are:

* `github` - default
* `gitlab`
* `bitbucket`
* `git` - for a generic Git support

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


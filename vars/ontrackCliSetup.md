## [`ontrackCliSetup`](ontrackCliSetup.groovy)

This step performs some basic setup in Ontrack for your pipeline.

### Parameters

#### Setup parameters

| Parameter              | Type                                  | Default                                       | Description                                                                                                                                                                                                                                                                        |
|------------------------|---------------------------------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `project`              | String                                | _Computed_                                    | Name of the Ontrack project. If not provided, it'll be computed from the `GIT_URL` environment variable. The result will be stored into the `ONTRACK_PROJECT_NAME` environment variable.                                                                                           |
| `branch`               | String                                | _Computed_                                    | Name of the Ontrack branch. If not provided, it'll be computed from the `BRANCH_NAME` environment variable. The result will be stored into the `ONTRACK_BRANCH_NAME` environment variable.                                                                                         |
| `setup`                | boolean                               | `true`                                        | Performs the setup of the project and the branch in Ontrack, based on the provided information. If disabled, only environment variables will be exported.                                                                                                                          |
| `autoValidationStamps` | String                                | `true`                                        | This option allows the setup of the automatic creation of validation stamps for all branches in the Ontrack project. See [auto validation stamps](#auto-validation-stamps) for the possible values.                                                                                |
| `autoPromotionLevels`  | boolean                               | `true`                                        | This option allows the setup of the automatic creation of promotion levels for all branches in the Ontrack project.                                                                                                                                                                |
| `validations`          | See [validations](#validations) below | _None_                                        | Configuration of the validation stamps                                                                                                                                                                                                                                             |
| `promotions`           | See [promotions](#promotions) below   | _None_                                        | Configuration of the auto promotions                                                                                                                                                                                                                                               |
| `scm`                  | String                                | Value of `ONTRACK_SCM` or `github`            | Type of SCM for this project. See [SCM Configuration](#scm-configuration) for more information.                                                                                                                                                                                    |
| `scmIndexation`        | int                                   | `30`                                          | SCM indexation interval in minutes. Set to 0 to disable.                                                                                                                                                                                                                           |
| `scmConfiguration`     | String                                | Value of `ONTRACK_SCM_CONFIG` or `github.com` | Name of the SCM configuration in Ontrack holding connection information about the SCM.                                                                                                                                                                                             |
| `scmIssues`            | String                                | Value of `ONTRACK_SCM_ISSUES`                 | ID of the issue service to use for mapping issues to commits in Ontrack. Can be provided explicitely, though the `ONTRACK_SCM_ISSUES` environment variable. If not defined, and if using GitHub or GitHub, defaults to `self` (meaning that the GitHub or GitLab issues are used). |
| `releaseValidation`    | String                                | _None_                                        | If set, the branch will automatically set this validation on the builds which have been labelled with a release tag.                                                                                                                                                               |
| `useLabel`             | Boolean                               | `true`                                        | To be set to `true` if your builds must use their release label for the versioning                                                                                                                                                                                                 |

#### General parameters

| Parameter | Type    | Default | Description                                                                                   |
|-----------|---------|---------|-----------------------------------------------------------------------------------------------|
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the setup operations.   |
| `tracing` | boolean | `false` | Set to `true` to display debug / low level information while performing the setup operations. |

### Auto validation stamps

The `autoValidationStamps` parameter can have the following values:

* `true` - auto creation of validation stamps based on predefined validation stamps
* `force` - auto creation even if no predefined validation stamp if defined (they will be created)
* any other value - disable the auto creation of validation stamps

### Validations

The `validations` parameter is used to predefine some validation stamps for the Ontrack branch.

It's a list of objects with the following attributes:

* `name` - required, the name of the validation stamp

By default, a generic validation stamp is created, without any data validation type being associated with it.

You can specify the data type and its configuration using:

* `dataType` - FQCN of the data type
* `dataConfig` - JSON for the data type configuration

For example:

```groovy
validations: [
 [
    name: "BUILD",
    dataType: "net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
    dataConfig: [
        warningIfSkipped: true,
    ],
 ],
]
```

Additionally, specific attributes are available for common data types.

#### Test validation stamp

`tests` - for validation based on test summary, containing the following attributes:

* `warningIfSkipped` - defaults to `false`
    
Example:

```groovy
validations: [
 [
    name: "BUILD",
    tests: [
        warningIfSkipped: true,
    ]
 ],
]
```

#### CHML validation stamp

`chml` - for validation based on CHML data, containing the following attributes:
 
* `failed` - with `level` and minimal `value`
* `warning` - with `level` and minimal `value`

Example:

```groovy
validations: [
 [
    name: "CHML",
    chml: [
        failed: [
            level: 'CRITICAL',
            value: 1,
        ],
        warning: [
            level: 'HIGH',
            value: 1,
        ]
    ],
 ],
```

#### Percentage validation stamp

`percentage` - for validation based on percentage data, containing the following attributes:
 
* `failure` - failure threshold
* `warning` - warning threshold
* `okIfGreater` - direction of the validity

Example:

```groovy
validations: [
 [
     name: "PERCENTAGE",
     percentage: [
             failure: 80,
             warning: 50,
             okIfGreater: false,
     ],
 ],
```

#### Metrics validation stamp

`metrics` - for validation based on metrics data. This parameter just need to present, with whatever value.

Example:

```groovy
validations: [
 [
     name: "METRICS",
     metrics: true,
 ],
```

### Promotions

The `promotions` parameter is used to set up the auto promotions for the Ontrack branch.

It contains a map indexed by promotion name and containing promotion definitions:

* `validations` - list of validation stamps which must be passed for the promotion to be granted
* `promotions` - list of promotion levels which must be granted

For example:

```groovy
ontrackCliSetup(
        promotions: [
                BRONZE: [
                        validations: [
                                "BUILD"
                        ]
                ],
                SILVER: [
                        promotions: [
                                "BRONZE",
                        ],
                        validations: [
                                "CHML",
                                "PERCENTAGE",
                                "METRICS",
                        ],
                ],
        ]
)
```

In this example, we define two promotion levels:

* `BRONZE` - granted whenever the `BUILD` validation stamp is passed
* `SILVER` - granted whenever all the mentioned validation stamps are passed _and_ the `BRONZE` promotion has been granted

### SCM configuration

The `scm` parameter defines which type of SCM (GitHub, GitLab, Bitbucket, etc.) must be associated with your pipeline.

By default, if not provided, the value will be `github` or the value stored in the `ONTRACK_SCM` environment variable.

Possible values are:

* `github` - default
* `gitlab`
* `bitbucket-server` - for Bitbucket Server
* `bitbucket-cloud` - for Bitbucket Cloud
* `git` - for a generic Git support

### Outputs

The following environment variables are created:

* `ONTRACK_PROJECT_NAME` - the name of the project in Ontrack
* `ONTRACK_BRANCH_NAME` - the name of the branch in Ontrack

### Examples

#### Simplest example with GitHub

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

#### Bitbucket Cloud

Given a configured [Bitbucket Cloud configuration](https://static.nemerosa.net/ontrack/release/4.0.11/docs/doc/index.html#integration-bitbucket-cloud) in Ontrack, you can configure your pipeline based on Bitbucket Cloud to use this configuration and configure all the items (project & branch) automatically:

```groovy
pipeline {
    agent any
    stages {
        stage("Setup") {
            steps {
                ontrackCliSetup(
                        scm: 'bitbucket-cloud',
                        scmConfiguration: '<name of your config in Ontrack>',
                        // ... any other option
                )
            }
        }
    }
}
```

If you use _only_ Bitbucket Cloud in your Jenkins instance, you can set the Jenkins global variable `ONTRACK_SCM` to `bitbucket-cloud` and you can drop the `scm` parameter from `ontrackCliSetup`.
> 
If additionally you use only one configuration, you can set the Jenkins global variable `ONTRACK_SCM_CONFIG` to hold it and you can drop the `scmConfiguration` parameter from `ontrackCliSetup`.

Note that you can always override those values at pipeline level.

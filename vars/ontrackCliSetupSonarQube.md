## [`ontrackCliSetupSonarQube`](ontrackCliSetupSonarQube.groovy)

Use this step after [`ontrackCliSetup`](ontrackCliSetup.md) to setup
the SonarQube properties on the Ontrack project, so that SonarQube measures
can be collected automatically and attached to the validations.

> This step _must_ be called after [`ontrackCliSetup`](ontrackCliSetup.md).

### Parameters

| Parameter         | Type         | Default                                     | Description                                                                                                            |
|-------------------|--------------|---------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `project`         | String       | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to configure                                                                            |
| configuration     | String       | `SonarQube`                                 | Name of the SQ configuration in Ontrack, used to connect to the SQ server                                              |
| key               | String       | _Required_                                  | Key of the project in SonarQube                                                                                        |
| validationStamp   | String       | `sonarqube`                                 | Name of the validation which triggers the collection of measures and which be enriched with the SQ measures            |
| measures          | List<String> | `[]`                                        | List of specific measures for the project                                                                              |
| override          | Boolean      | `false`                                     | `true` if the specific project measures must replace the global ones, or just appended to them                         |
| branchModel       | Boolean      | `false`                                     | If the branches eligible for the collection of SQ measures are restricted by the branch model attached to the project  |
| branchPattern     | String       | `null`                                      | If defined, regular expression to restrict the branches eligible to the collection of measures                         |
| validationMetrics | Boolean      | `true`                                      | By default, SQ measures are attached as validation data. Use `false` to disable this and use only the exported metrics |
| logging           | boolean      | `false`                                     | Set to `true` to display debug / logging information while performing the operation.                                   |

### Examples

Default configuration using only the project key in SonarQube:

```groovy
ontrackCliSetupSonarQube(
    // configuration: "SonarQube",
    key: sonarQubeKey,
    // validationStamp: "sonarqube",
    // measures: [],
    // override: false,
    // branchModel: false,
    // branchPattern: null,
    // validationMetrics: true,
)
```

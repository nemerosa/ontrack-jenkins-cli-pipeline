## [`ontrackCliSetupBranchNotifications`](ontrackCliSetupBranchNotifications.groovy)

This step is used to define notifications and subscriptions at branch level.

> While it's possible to setup all needed parameters manually, this step works better when the [`ontrackCliSetup`](ontrackCliSetup.md) step has been called first.

### Parameters

| Parameter         | Type         | Default                                     | Description                                                       |
|-------------------|--------------|---------------------------------------------|-------------------------------------------------------------------|
| `project`         | String       | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to configure                       |
| `branch`          | String       | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to configure                        |
| `name`            | String       | _Optional_ (*)                              | Name of the subscription                                          |
| `channel`         | String       | _Required_                                  | ID of the notification channel                                    |
| `channelConfig`   | JSON         | _Required_                                  | Configuration of the notification                                 |
| `events`          | List<String> | _Required_                                  | List of events to listen to                                       |
| `keywords`        | String       | _Optional_                                  | Optional space-separated list of tokens to look for in the events |
| `contentTemplate` | String       | _Optional_                                  | Template for the notification text                                |

(*) The _name_ of the subscription:

* should not be passed when using Ontrack <= 4.8
* should be passed (but not required) when using Ontrack >= 4.9
* will be required starting from Ontrack 5

### Examples

To trigger a Jenkins job whenever a new build is created on the branch:

```groovy
ontrackCliSetupBranchNotifications(
        channel: 'jenkins',
        channelConfig: [
                config: "my-jenkins-config-name",
                job: "my-folder/my-pipeline/initBranch",
                parameters: [
                        [
                            name: "PROJECT",
                            value: "{Project}",
                        ],
                        [
                            name: "BRANCH",
                            value: "{scmBranch}",
                        ],
                        [
                            name: "BUILD",
                            value: "{Build}",
                        ],
                ],
                callModel: "ASYNC",
        ],
        events: [
                "new_build",
        ]
)
```

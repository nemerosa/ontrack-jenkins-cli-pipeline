## [`ontrackCliSetupProjectNotifications`](ontrackCliSetupProjectNotifications.groovy)

This step is used to define notifications and subscriptions at project level.

> While it's possible to setup all needed parameters manually, this step works better when the [`ontrackCliSetup`](ontrackCliSetup.md) step has been called first.

### Parameters

| Parameter         | Type         | Default                                     | Description                                                       |
|-------------------|--------------|---------------------------------------------|-------------------------------------------------------------------|
| `project`         | String       | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to configure                       |
| `channel`         | String       | _Required_                                  | ID of the notification channel                                    |
| `channelConfig`   | JSON         | _Required_                                  | Configuration of the notification                                 |
| `events`          | List<String> | `[]`                                        | List of events to listen to                                       |
| `keywords`        | String       | _Optional_                                  | Optional space-separated list of tokens to look for in the events |
| `contentTemplate` | String       | _Optional_                                  | Template for the notification text                                |

### Examples

To trigger a Jenkins job whenever a new branch is created on the project:

```groovy
ontrackCliSetupProjectNotifications(
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
                ],
                callModel: "ASYNC",
        ],
        events: [
                "new_branch",
        ]
)
```

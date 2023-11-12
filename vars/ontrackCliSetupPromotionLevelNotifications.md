## [`ontrackCliSetupPromotionLevelNotifications`](ontrackCliSetupPromotionLevelNotifications.groovy)

This step is used to define notifications and subscriptions at promotion level.

> While it's possible to setup all needed parameters manually, this step works better when the [`ontrackCliSetup`](ontrackCliSetup.md) step has been called first.

### Parameters

| Parameter       | Type         | Default                                     | Description                                                       |
|-----------------|--------------|---------------------------------------------|-------------------------------------------------------------------|
| `project`       | String       | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to configure                       |
| `branch`        | String       | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to configure                        |
| `promotion`     | String       | _Required_                                  | Name of the promotion level in Ontrack to configure               |
| `channel`       | String       | _Required_                                  | ID of the notification channel                                    |
| `channelConfig` | JSON         | _Required_                                  | Configuration of the notification                                 |
| `events`        | List<String> | _Required_                                  | List of events to listen to                                       |
| `keywords`      | String       | _Optional_                                  | Optional space-separated list of tokens to look for in the events |

### Examples

To trigger a Jenkins job whenever a new `GOLD` promotion is created:

```groovy
ontrackCliSetupPromotionLevelNotifications(
        promotion: "GOLD",
        channel: 'jenkins',
        channelConfig: [
                config: "my-jenkins-config-name",
                job: "my-folder/my-pipeline/{scmBranch|unicode}",
                callModel: "ASYNC",
        ],
        events: [
                "new_promotion_run",
        ],
)
```

## [`ontrackCliSetupValidationStampNotifications`](ontrackCliSetupValidationStampNotifications.groovy)

This step is used to define notifications and subscriptions at validation stamp level.

> While it's possible to setup all needed parameters manually, this step works better when the [`ontrackCliSetup`](ontrackCliSetup.md) step has been called first.

### Parameters

| Parameter         | Type         | Default                                     | Description                                                       |
|-------------------|--------------|---------------------------------------------|-------------------------------------------------------------------|
| `project`         | String       | `ONTRACK_PROJECT_NAME` environment variable | Name of the project in Ontrack to configure                       |
| `branch`          | String       | `ONTRACK_BRANCH_NAME` environment variable  | Name of the branch in Ontrack to configure                        |
| `validation`      | String       | _Required_                                  | Name of the validation stamp in Ontrack to configure              |
| `channel`         | String       | _Required_                                  | ID of the notification channel                                    |
| `channelConfig`   | JSON         | _Required_                                  | Configuration of the notification                                 |
| `events`          | List<String> | _Required_                                  | List of events to listen to                                       |
| `keywords`        | String       | _Optional_                                  | Optional space-separated list of tokens to look for in the events |
| `contentTemplate` | String       | _Optional_                                  | Template for the notification text                                |

### Examples

To send a notification on Slack whenever a new `tfc` validation is failed:

```groovy
ontrackCliSetupValidationStampNotifications(
        validation: "tfc",
        channel: 'slack',
        channelConfig: [
                channel: '#notifications',
                type: 'ERROR',
        ],
        events: [
                "new_validation_run",
        ],
        keywords: "failed",
        contentTemplate: '''\
Deployment using TFC has failed for build ${build}.
'''
)
```

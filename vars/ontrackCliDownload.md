## [`ontrackCliDownload`](ontrackCliDownload.groovy)

This step downloads the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli) and puts it on the `PATH` for being used by other steps.

### Parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `version` | String | _none_ | [Version](https://github.com/nemerosa/ontrack-cli/releases) of the CLI to download. If not specified, the latest release will be downloaded. |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while downloading and setting up the CLI. |

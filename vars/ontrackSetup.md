## [`ontrackSetup`](ontrackSetup.groovy)

This step downloads the [Ontrack CLI](https://github.com/nemerosa/ontrack-cli), puts it on the `PATH` for being used by other steps, configures its connection and performs some basic setup in Ontrack for your very pipeline.

### Parameters

#### General parameters

| Parameter | Type | Default | Description |
|---|---|---|---|
| `version` | String | _none_ | [Version](https://github.com/nemerosa/ontrack-cli/releases) of the CLI to download. If not specified, the latest release will be downloaded. |
| `executable` | String | `ontrack-cli` | Name of the Ontrack CLI executable to put in the path |
| `logging` | boolean | `false` | Set to `true` to display debug / logging information while performing the setup operations. |
| `tracing` | boolean | `false` | Set to `true` to display debug / low level information while performing the setup operations. |

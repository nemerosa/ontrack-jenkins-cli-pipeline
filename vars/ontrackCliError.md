## [`ontrackCliError`](ontrackCliError.groovy)

This step raises an error or logs it, depending on the global settings.

### Parameters

| Parameter | Type   | Default    | Description                          |
|-----------|--------|------------|--------------------------------------|
| `message` | String | _Required_ | Error message (positional parameter) |

### Outputs

None

### Example

```groovy
ontrackCliError("Not OK")
```

import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    String pattern = ParamUtils.getParam(params, "pattern", "**/build/test-results/**/*.xml")
    boolean allowEmptyResults = ParamUtils.getBooleanParam(params, "allowEmptyResults", true)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Parsing the JUnit results
    def results = junit(testResults: pattern, allowEmptyResults: allowEmptyResults)

    // Getting results details
    int passed = results.passCount
    int skipped = results.skipCount
    int failed = results.failCount

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate-tests")
    List<String> args = validation.cli(this, params, false)

    // Tests args
    args += ['tests', '--passed', passed, '--skipped', skipped, '--failed', failed]

    // Calling the CLI
    Cli.call(this, logging, args)

    // Returning the results
    return results
}

import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String name = ParamUtils.getParam(params, "name", env.BUILD_NUMBER as String)
    String release = ParamUtils.getConditionalParam(params, "release", false, null)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    env.ONTRACK_BUILD_NAME = name

    List<String> args = ['build', 'setup', '--project', project, '--branch', branch, '--build', name]
    if (release) {
        args += '--release'
        args += release
    }

    Cli.call(this, logging, args)

}

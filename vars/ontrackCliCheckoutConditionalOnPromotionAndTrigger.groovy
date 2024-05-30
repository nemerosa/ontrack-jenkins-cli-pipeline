import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String,?> params) {
    if (ontrackCliFailsafe()) {
        // In this case, no Ontrack, just performing a std checkout
        standardCheckout(this)
        // Not returning any thing
        return [:]
    }

    // Parameters
    String project = ParamUtils.getParam(params, "project")
    String promotion = ParamUtils.getParam(params, "promotion")
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    def ontrackBuild = null
    def userIdCauses = currentBuild.getBuildCauses("hudson.model.Cause\$UserIdCause")
    def triggeredByUser = (userIdCauses.size() > 0)
    if (triggeredByUser) {
        def scm = standardCheckout(this)
        // Ontrack setup
        ontrackCliSetup(setup: false)
        // Gets the build using the commit
        ontrackBuild = ontrackCliGetBuildByCommit(injectEnv: true, commit: scm.GIT_COMMIT)
    } else {
        // Ontrack setup
        // We don't know the project here because the SCM checkout has not been done yet
        ontrackCliSetup(setup: false, project: project)
        // Gets the latest BRONZE
        ontrackBuild = ontrackCliLastPromotion(promotion: promotion, injectEnv: true)
        if (ontrackBuild && ontrackBuild.commit) {
            // Checkout the specific commit
            def scm = checkout([
                    $class: 'GitSCM',
                    branches: [[name: ontrackBuild.commit]],
                    userRemoteConfigs: scm.userRemoteConfigs,
            ])
            env.GIT_URL = scm.GIT_URL
            env.GIT_COMMIT = scm.GIT_COMMIT
        } else {
            // No build found, defaulting to the HEAD
            standardCheckout(this)
        }
    }

    // OK
    return ontrackBuild
}

def standardCheckout(def dsl) {
    def scm = dsl.checkout(scm)
    dsl.env.GIT_URL = scm.GIT_URL
    dsl.env.GIT_COMMIT = scm.GIT_COMMIT
    return scm
}
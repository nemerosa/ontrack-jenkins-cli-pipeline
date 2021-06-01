import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.GitHubUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {
    boolean setup = ParamUtils.getBooleanParam(params, "setup", true)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-setup] $it")
        }
    }

    // CLI download
    ontrackCliDownload(params)
    // CLI setup
    ontrackCliConnect(params)

    // Computing the Ontrack project name from the Git URL
    env.ONTRACK_PROJECT_NAME = ParamUtils.getParam(params, "project", OntrackUtils.getProjectName(env.GIT_URL))
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_PROJECT_NAME = ${env.ONTRACK_PROJECT_NAME}")
    }

    // Computing the Ontrack branch name from the branch name
    env.ONTRACK_BRANCH_NAME = ParamUtils.getParam(params, "branch", OntrackUtils.getBranchName(env.BRANCH_NAME))
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_BRANCH_NAME = ${env.ONTRACK_BRANCH_NAME}")
    }

    // Setting up the branch
    if (setup) {

        // Project & branch creation

        List<String> setupArgs = ['branch', 'setup', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME]

        String autoVS = ParamUtils.getConditionalParam(params, "autoValidationStamps", false, "")
        if (autoVS == 'true') {
            setupArgs += "--auto-create-vs"
        } else if (autoVS == 'force') {
            setupArgs += "--auto-create-vs"
            setupArgs += "--auto-create-vs-always"
        } else if (autoVS == 'false') {
            setupArgs += "--auto-create-vs=false"
        }

        String autoPL = ParamUtils.getConditionalParam(params, "autoPromotionLevels", false, "")
        if (autoPL == 'true') {
            setupArgs += "--auto-create-pl"
        } else if (autoPL == 'false') {
            setupArgs += "--auto-create-pl=false"
        }

        Cli.call(this, logging, setupArgs)

        // Git configuration for the project & branch

        String scm = ParamUtils.getParam(params, "scm", env.ONTRACK_SCM ?: 'github')
        logger("SCM = $scm")

        int scmIndexation = ParamUtils.getIntParam(params, "scmIndexation", 30)
        logger("SCM Indexation = $scmIndexation")

        if (scm == 'github') {
            String scmConfig = ParamUtils.getParam(params, "scmConfiguration", env.ONTRACK_SCM_CONFIG ?: 'github.com')
            String owner = GitHubUtils.getOwner(env.GIT_URL)
            String repository = GitHubUtils.getRepository(env.GIT_URL)

            String issueService = ParamUtils.getParam(params, "scmIssues", env.ONTRACK_SCM_ISSUES ?: 'self')

            Cli.call(this, logging, ['project', 'set-property', '--project', env.ONTRACK_PROJECT_NAME, 'github', '--configuration', scmConfig, '--repository', "${owner}/${repository}", '--indexation', scmIndexation, '--issue-service', issueService])
        } else {
            throw new RuntimeException("SCM not supported: $scm")
        }

        // Branch Git configuration

        Cli.call(this, logging, ['branch', 'set-property', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, 'git', '--git-branch', env.BRANCH_NAME])

        // Validation stamps setup

        def createdValidations = [:]
        def validations = params.validations
        if (validations) {
            validations.each { validation ->
                String name = validation.name as String
                createdValidations[name] = validation
                // Generic data
                if (validation.dataType) {
                    def vsArgs = ['validation', 'setup', 'generic', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name]
                    vsArgs += '--data-type'
                    vsArgs += validation.dataType
                    if (validation.dataConfig) {
                        vsArgs += '--data-config'
                        vsArgs += "'${JsonUtils.toJSON(validation.dataConfig)}'".toString()
                    }
                }
                // Tests
                else if (validation.tests) {
                    def vsArgs = ['validation', 'setup', 'tests', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name]
                    if (validation.tests.warningIfSkipped != null) {
                        vsArgs += "--warning-if-skipped=${validation.tests.warningIfSkipped as boolean}" as String
                    }
                    Cli.call(this, logging, vsArgs)
                }
                // CHML
                else if (validation.chml) {
                    def vsArgs = ['validation', 'setup', 'chml', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name]
                    if (validation.chml.failed != null) {
                        vsArgs += '--failed'
                        vsArgs += "${validation.chml.failed.level}=${validation.chml.failed.value}"
                    }
                    if (validation.chml.warning != null) {
                        vsArgs += '--warning'
                        vsArgs += "${validation.chml.warning.level}=${validation.chml.warning.value}"
                    }
                    Cli.call(this, logging, vsArgs)
                }
                // Percentage
                else if (validation.percentage) {
                    def vsArgs = ['validation', 'setup', 'percentage', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name]
                    if (validation.percentage.failure != null) {
                        vsArgs += '--failure'
                        vsArgs += validation.percentage.failure as int
                    }
                    if (validation.percentage.warning != null) {
                        vsArgs += '--warning'
                        vsArgs += validation.percentage.warning as int
                    }
                    if (validation.percentage.okIfGreater != null) {
                        vsArgs += "--ok-if-greater=${validation.percentage.okIfGreater as boolean}" as String
                    }
                    Cli.call(this, logging, vsArgs)
                }
                // Metrics
                else if (validation.metrics) {
                    Cli.call(this, logging, ['validation', 'setup', 'metrics', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name])
                }
                // Generic stamp
                else {
                    Cli.call(this, logging, ['validation', 'setup', 'generic', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', name])
                }
            }
        }

        // Auto promotion configuration

        def promotions = params.promotions
        if (promotions) {

            // List of validations and promotions to setup
            List<String> validationStamps = []
            List<String> promotionLevels = []

            // Going over all promotions
            promotions.each { promotion, promotionConfig ->
                promotionLevels += promotion
                if (promotionConfig.validations) {
                    promotionConfig.validations.each { validation ->
                        validationStamps += validation
                    }
                }
            }

            // Creates all the validations
            validationStamps.each { validation ->
                if (!createdValidations.containsKey(validation)) {
                    Cli.call(this, logging, ['validation', 'setup', 'generic', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--validation', validation])
                }
            }

            // Creates all the promotions
            promotionLevels.each { promotion ->
                Cli.call(this, logging, ['promotion', 'setup', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--promotion', promotion])
            }

            // Auto promotion setup
            promotions.each { promotion, promotionConfig ->
                List<String> promotionArgs = ['promotion', 'setup', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME, '--promotion', promotion]
                if (promotionConfig.validations) {
                    promotionConfig.validations.each { validation ->
                        promotionArgs += '--validation'
                        promotionArgs += validation
                    }
                }
                if (promotionConfig.promotions) {
                    promotionConfig.promotions.each { other ->
                        promotionArgs += '--depends-on'
                        promotionArgs += other
                    }
                }
                Cli.call(this, logging, promotionArgs)
            }

        }

    }
}

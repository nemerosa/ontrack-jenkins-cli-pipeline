import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.server.BitbucketServerUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.server.BitbucketServerRepository
import net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.cloud.BitbucketCloudUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.cloud.BitbucketCloudRepository
import net.nemerosa.ontrack.jenkins.pipeline.utils.GitLabUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.GitHubUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.validate.ValidationStampUtils
import net.nemerosa.ontrack.jenkins.pipeline.promote.PromotionLevelUtils

def call(Map<String, ?> params = [:]) {
    boolean setup = ParamUtils.getBooleanParam(params, "setup", true)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)
    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-setup] $it")
        }
    }

    // Computing the Ontrack project name from the Git URL
    String project = ParamUtils.getParam(params, "project", OntrackUtils.getProjectName(env.GIT_URL))
    env.ONTRACK_PROJECT_NAME = project
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_PROJECT_NAME = ${env.ONTRACK_PROJECT_NAME}")
    }

    // Computing the Ontrack branch name from the branch name
    String branch = ParamUtils.getParam(params, "branch", OntrackUtils.getBranchName(env.BRANCH_NAME))
    env.ONTRACK_BRANCH_NAME = branch
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_BRANCH_NAME = ${env.ONTRACK_BRANCH_NAME}")
    }

    // Setting up the branch
    if (setup) {

        // GraphQL query

        String query = '''
			mutation ProjectSetup(
				$project: String!, 
				$branch: String!,
				$autoCreateVSProperty: Boolean!,
				$autoCreateVS: Boolean!,
				$autoCreateVSIfNotPredefined: Boolean!,
				$autoCreatePLProperty: Boolean!
				$autoCreatePL: Boolean!
				$releaseValidation: String!,
				$releaseValidationProperty: Boolean!,
				$useLabel: Boolean!,
			) {
				createProjectOrGet(input: {name: $project}) {
					errors {
					    message
					}
				}
				createBranchOrGet(input: {projectName: $project, name: $branch}) {
					errors {
					    message
					}
				}
				setProjectAutoValidationStampProperty(input: {
					project: $project,
					isAutoCreate: $autoCreateVS,
					isAutoCreateIfNotPredefined: $autoCreateVSIfNotPredefined
				}) @include(if: $autoCreateVSProperty) {
					errors {
						message
					}
				}
				setProjectAutoPromotionLevelProperty(input: {
					project: $project,
					isAutoCreate: $autoCreatePL
				}) @include(if: $autoCreatePLProperty) {
					errors {
						message
					}
				}
				setProjectBuildLinkDisplayProperty(input: {
					project: $project,
					useLabel: true
				}) @include(if: $useLabel) {
					errors {
						message
					}
				}
				setBranchReleaseValidationProperty(input: {
					project: $project,
					branch: $branch,
					validation: $releaseValidation,
				}) @include(if: $releaseValidationProperty) {
					errors {
						message
					}
				}
			}
        '''

        // GraphQL variables

        Map<String,?> variables = [
                project: env.ONTRACK_PROJECT_NAME,
                branch: env.ONTRACK_BRANCH_NAME,
        ]

        // Auto validation stamps
        variables.autoCreateVSProperty = false
        variables.autoCreateVS = false
        variables.autoCreateVSIfNotPredefined = false
        String autoVS = ParamUtils.getConditionalParam(params, "autoValidationStamps", false, "")
        if (autoVS == 'true') {
            variables.autoCreateVSProperty = true
            variables.autoCreateVS = true
            variables.autoCreateVSIfNotPredefined = false
        } else if (autoVS == 'force') {
            variables.autoCreateVSProperty = true
            variables.autoCreateVS = true
            variables.autoCreateVSIfNotPredefined = true
        } else if (autoVS == 'false') {
            variables.autoCreateVSProperty = true
            variables.autoCreateVS = false
            variables.autoCreateVSIfNotPredefined = false
        }

        // Auto promotion levels
        variables.autoCreatePLProperty = false
        variables.autoCreatePL = false
        String autoPL = ParamUtils.getConditionalParam(params, "autoPromotionLevels", false, "")
        if (autoPL == 'true') {
            variables.autoCreatePLProperty = true
            variables.autoCreatePL = true
        } else if (autoPL == 'false') {
            variables.autoCreatePLProperty = true
            variables.autoCreatePL = false
        }

        // Project use label
        variables.useLabel = ParamUtils.getBooleanParam(params, "useLabel", false)

        // Branch release validation property
        String releaseValidation = params.releaseValidation
        if (releaseValidation) {
            variables.releaseValidation = releaseValidation
            variables.releaseValidationProperty = true
        } else {
            variables.releaseValidation = ''
            variables.releaseValidationProperty = false
        }

        // GraphQL call for the general setup
        def setupResponse = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
        )
        GraphQL.checkForMutationErrors(setupResponse, 'createProjectOrGet')
        GraphQL.checkForMutationErrors(setupResponse, 'createBranchOrGet')
        GraphQL.checkForMutationErrors(setupResponse, 'setProjectAutoValidationStampProperty')
        GraphQL.checkForMutationErrors(setupResponse, 'setProjectAutoPromotionLevelProperty')
        GraphQL.checkForMutationErrors(setupResponse, 'setProjectBuildLinkDisplayProperty')
        GraphQL.checkForMutationErrors(setupResponse, 'setBranchReleaseValidationProperty')

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

            def gitHubProjectResponse = ontrackCliGraphQL(
                    query: '''
                        mutation SetProjectGitHubProperty(
                            $project: String!,
                            $configuration: String!,
                            $repository: String!,
                            $indexationInterval: Int,
                            $issueServiceConfigurationIdentifier: String
                        ) {
                            setProjectGitHubConfigurationProperty(input: {
                                project: $project,
                                configuration: $configuration,
                                repository: $repository,
                                indexationInterval: $indexationInterval,
                                issueServiceConfigurationIdentifier: $issueServiceConfigurationIdentifier
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    ''',
                    variables: [
                            project: project,
                            configuration: scmConfig,
                            repository: "${owner}/${repository}" as String,
                            indexationInterval: scmIndexation,
                            issueServiceConfigurationIdentifier: issueService,
                    ],
                    logging: logging,
            )
            GraphQL.checkForMutationErrors(gitHubProjectResponse, 'setProjectGitHubConfigurationProperty')
        } else if (scm == 'bitbucket-server') {
            String scmConfig = ParamUtils.getParam(params, "scmConfiguration", env.ONTRACK_SCM_CONFIG)
            BitbucketServerRepository repo = BitbucketServerUtils.getBitbucketRepository(env.GIT_URL)

            String issueService = ParamUtils.getConditionalParam(params, "scmIssues", false, env.ONTRACK_SCM_ISSUES)

            def bitbucketProjectResponse = ontrackCliGraphQL(
                    query: '''
                        mutation SetProjectBitbucketProperty(
                            $project: String!,
                            $configuration: String!,
                            $bitbucketProject: String!,
                            $bitbucketRepository: String!,
                            $indexationInterval: Int,
                            $issueServiceConfigurationIdentifier: String
                        ) {
                            setProjectBitbucketConfigurationProperty(input: {
                                project: $project,
                                configuration: $configuration,
                                bitbucketProject: $bitbucketProject,
                                bitbucketRepository: $bitbucketRepository,
                                indexationInterval: $indexationInterval,
                                issueServiceConfigurationIdentifier: $issueServiceConfigurationIdentifier
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    ''',
                    variables: [
                            project: project,
                            configuration: scmConfig,
                            bitbucketProject: repo.project,
                            bitbucketRepository: repo.repository,
                            indexationInterval: scmIndexation,
                            issueServiceConfigurationIdentifier: issueService,
                    ],
                    logging: logging,
            )
            GraphQL.checkForMutationErrors(bitbucketProjectResponse, 'setProjectBitbucketConfigurationProperty')
        } else if (scm == 'bitbucket-cloud') {
            String scmConfig = ParamUtils.getParam(params, "scmConfiguration", env.ONTRACK_SCM_CONFIG)
            BitbucketCloudRepository repo = BitbucketCloudUtils.getBitbucketRepository(env.GIT_URL)

            String issueService = ParamUtils.getConditionalParam(params, "scmIssues", false, env.ONTRACK_SCM_ISSUES)

            def bitbucketProjectResponse = ontrackCliGraphQL(
                    query: '''
                        mutation SetProjectBitbucketCloudProperty(
                            $project: String!,
                            $configuration: String!,
                            $repository: String!,
                            $indexationInterval: Int,
                            $issueServiceConfigurationIdentifier: String
                        ) {
                            setProjectBitbucketCloudConfigurationProperty(input: {
                                project: $project,
                                configuration: $configuration,
                                repository: $repository,
                                indexationInterval: $indexationInterval,
                                issueServiceConfigurationIdentifier: $issueServiceConfigurationIdentifier
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    ''',
                    variables: [
                            project: project,
                            configuration: scmConfig,
                            repository: repo.repository,
                            indexationInterval: scmIndexation,
                            issueServiceConfigurationIdentifier: issueService,
                    ],
                    logging: logging,
            )
            GraphQL.checkForMutationErrors(bitbucketProjectResponse, 'setProjectBitbucketCloudConfigurationProperty')
        } else if (scm == 'gitlab') {
            String scmConfig = ParamUtils.getParam(params, "scmConfiguration", env.ONTRACK_SCM_CONFIG)
            String repository = GitLabUtils.getRepository(env.GIT_URL)

            String issueService = ParamUtils.getConditionalParam(params, "scmIssues", false, env.ONTRACK_SCM_ISSUES)

            def gitLabProjectResponse = ontrackCliGraphQL(
                    query: '''
                        mutation SetProjectGitLabProperty(
                            $project: String!,
                            $configuration: String!,
                            $repository: String!,
                            $indexationInterval: Int,
                            $issueServiceConfigurationIdentifier: String
                        ) {
                            setProjectGitLabConfigurationProperty(input: {
                                project: $project,
                                configuration: $configuration,
                                repository: $repository,
                                indexationInterval: $indexationInterval,
                                issueServiceConfigurationIdentifier: $issueServiceConfigurationIdentifier
                            }) {
                                errors {
                                    message
                                }
                            }
                        }
                    ''',
                    variables: [
                            project: project,
                            configuration: scmConfig,
                            repository: repository,
                            indexationInterval: scmIndexation,
                            issueServiceConfigurationIdentifier: issueService,
                    ],
                    logging: logging,
            )
            GraphQL.checkForMutationErrors(gitLabProjectResponse, 'setProjectGitLabConfigurationProperty')
        } else {
            throw new RuntimeException("SCM not supported: $scm")
        }

        // Branch Git configuration

        def branchGitResponse = ontrackCliGraphQL(
                query: '''
                    mutation SetBranchGitConfigProperty(
                        $project: String!,
                        $branch: String!,
                        $gitBranch: String!
                    ) {
                        setBranchGitConfigProperty(input: {
                            project: $project,
                            branch: $branch,
                            gitBranch: $gitBranch
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: [
                        project: project,
                        branch: branch,
                        gitBranch: env.BRANCH_NAME as String,
                ]
        )
        GraphQL.checkForMutationErrors(branchGitResponse, 'setBranchGitConfigProperty')

        // Validation stamps setup

        def createdValidations = [:]
        def validations = params.validations
        if (validations) {
            validations.each { validation ->
                String name = validation.name as String
                createdValidations[name] = validation
                // Generic data
                if (validation.dataType) {
                    ValidationStampUtils.setupGenericValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                            validation.dataType,
                            validation.dataConfig
                    )
                    def response = ontrackCliGraphQL(
                            query: '''
                                mutation SetupValidationStamp(
                                    $project: String!,
                                    $branch: String!,
                                    $validation: String!,
                                    $description: String,
                                    $dataType: String,
                                    $dataTypeConfig: JSON
                                ) {
                                    setupValidationStamp(input: {
                                        project: $project,
                                        branch: $branch,
                                        validation: $validation,
                                        description: $description,
                                        dataType: $dataType,
                                        dataTypeConfig: $dataTypeConfig
                                    }) {
                                        errors {
                                            message
                                        }
                                    }
                                }
                            ''',
                            logging: logging,
                            variables: [
                                    project: project,
                                    branch: branch,
                                    validation: name,
                                    description: '',
                                    dataType: validation.dataType,
                                    dataTypeConfig: validation.dataConfig,
                            ]
                    )
                    GraphQL.checkForMutationErrors(response, 'setupValidationStamp')
                }
                // Tests
                else if (validation.tests) {
                    boolean warningIfSkipped = false
                    if (validation.tests.warningIfSkipped != null) {
                        warningIfSkipped = validation.tests.warningIfSkipped as boolean
                    }
                    ValidationStampUtils.setupTestsValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                            warningIfSkipped,
                    )
                }
                // CHML
                else if (validation.chml) {
                    ValidationStampUtils.setupCHMLValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                            validation.chml.warning.level as String,
                            validation.chml.warning.value as int,
                            validation.chml.failed.level as String,
                            validation.chml.failed.value as int,
                    )
                }
                // Percentage
                else if (validation.percentage) {
                    ValidationStampUtils.setupPercentageValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                            validation.percentage.failure as int,
                            validation.percentage.warning as int,
                            validation.percentage.okIfGreater as boolean,
                    )
                }
                // Metrics
                else if (validation.metrics) {
                    ValidationStampUtils.setupMetricsValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                    )
                }
                // Generic stamp
                else {
                    ValidationStampUtils.setupGenericValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            name,
                            '',
                    )
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
                    ValidationStampUtils.setupGenericValidationStamp(
                            this,
                            logging,
                            project,
                            branch,
                            validation,
                            '',
                    )
                }
            }

            // Auto promotion setup
            promotions.each { promotion, promotionConfig ->
                PromotionLevelUtils.setupPromotionLevel(this, logging, project, branch, promotion, promotionConfig.validations, promotionConfig.promotions)
            }

        }

    }
}

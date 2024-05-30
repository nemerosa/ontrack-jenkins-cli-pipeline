package net.nemerosa.ontrack.jenkins.pipeline.promote

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

class PromotionLevelUtils {

    static void setupPromotionLevel(def dsl, boolean logging, String project, String branch, String promotion, List<String> validations, List<String> promotions) {
        String query = '''
			mutation SetupPromotionLevel(
				$project: String!,
				$branch: String!,
				$promotion: String!,
				$description: String,
				$autoPromotion: Boolean!,
				$validationStamps: [String!],
				$promotionLevels: [String!]
			) {
				setupPromotionLevel(input: {
					project: $project,
					branch: $branch,
					promotion: $promotion,
					description: $description
				}) {
					errors {
						message
					}
				}
				setPromotionLevelAutoPromotionProperty(input: {
					project: $project,
					branch: $branch,
					promotion: $promotion,
					validationStamps: $validationStamps,
					promotionLevels: $promotionLevels
				}) @include(if: $autoPromotion) {
					errors {
						message
					}
				}
			}
        '''

        boolean autoPromotion = false
        if (validations || promotions) {
            autoPromotion = true
        }

        def variables = [
                project: project,
                branch: branch,
                promotion: promotion,
                description: '',
                autoPromotion: autoPromotion,
                validationStamps: validations,
                promotionLevels: promotions,
        ]

        def response = dsl.ontrackCliGraphQL(query: query, variables: variables, logging: logging)
        GraphQL.checkForMutationErrors(response, 'setupPromotionLevel', dsl.ontrackCliIgnoreErrors())
        GraphQL.checkForMutationErrors(response, 'setPromotionLevelAutoPromotionProperty', dsl.ontrackCliIgnoreErrors())
    }

}

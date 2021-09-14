package net.nemerosa.ontrack.jenkins.pipeline.properties

class MessagePropertyUtils {

    static boolean setVariables(Map<String,?> params, Map<String,?> variables) {
        boolean messageProperty = false
        variables.messagePropertyType = 'INFO'
        variables.messagePropertyText = ''
        if (params.message) {
            messageProperty = true
            if (params.message instanceof String) {
                variables.messagePropertyText = params.message as String
            } else {
                variables.messagePropertyType = params.message.type ?: 'INFO'
                variables.messagePropertyText = params.message.text ?: ''
            }
        }
        return messageProperty
    }

}
package net.nemerosa.ontrack.jenkins.pipeline.properties

class MetaInfoPropertyUtils {

    static boolean setVariables(Map<String, ?> params, Map<String, ?> variables) {
        boolean metaInfoProperty = false
        if (params.metaInfo) {
            metaInfoProperty = true
            if (params.metaInfo instanceof Map) {
                variables.metaInfoPropertyItems = params.metaInfo.collect { name, value ->
                    [
                            name : name,
                            value: value,
                    ]
                }
            } else {
                variables.metaInfoPropertyItems = params.metaInfo
            }
        }
        return metaInfoProperty
    }

}

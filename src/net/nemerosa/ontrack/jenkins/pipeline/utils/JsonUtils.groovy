package net.nemerosa.ontrack.jenkins.pipeline.utils

import groovy.json.JsonOutput

class JsonUtils {

    static String toJSON(Object data) {
        return JsonOutput.toJson(data)
    }

}

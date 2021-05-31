package net.nemerosa.ontrack.jenkins.pipeline.utils

import net.sf.json.JSONSerializer

class JsonUtils {

    static String toJSON(Object data) {
        return JSONSerializer.toJSON(data)
    }

}

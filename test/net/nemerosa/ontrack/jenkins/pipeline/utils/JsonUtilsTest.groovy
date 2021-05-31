package net.nemerosa.ontrack.jenkins.pipeline.utils


import org.junit.Test

import static org.junit.Assert.assertEquals

class JsonUtilsTest {

    @Test
    void 'JSON formatting'() {
        def data = [
                passed : 15,
                skipped: 8,
                failed : 1,
        ]
        def json = JsonUtils.toJSON(data)
        assertEquals('{"passed":15,"skipped":8,"failed":1}', json)
    }

}

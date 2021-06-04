package net.nemerosa.ontrack.jenkins.pipeline.utils

import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class ParamUtilsTest {

    @Test
    void 'Logging'() {
        assertFalse(ParamUtils.getLogging([:], null))
        assertFalse(ParamUtils.getLogging([:], ''))
        assertFalse(ParamUtils.getLogging([:], 'false'))
        assertTrue(ParamUtils.getLogging([:], 'true'))
        assertFalse(ParamUtils.getLogging([logging: false], 'true'))
        assertTrue(ParamUtils.getLogging([logging: true], 'true'))
        assertTrue(ParamUtils.getLogging([logging: true], null))
    }

}

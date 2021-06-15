package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.server

import org.junit.Test

import static org.junit.Assert.assertEquals

class BitbucketServerUtilsTest {

    @Test(expected = RuntimeException)
    void 'Null'() {
        BitbucketServerUtils.getBitbucketRepository(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank'() {
        BitbucketServerUtils.getBitbucketRepository('')
    }

    @Test
    void 'Bitbucket server'() {
        BitbucketServerUtils.getBitbucketRepository('ssh://git@bitbucket.example.com/prj/test.git').with {
            assertEquals('prj', project)
            assertEquals('test', repository)
        }
        BitbucketServerUtils.getBitbucketRepository('https://bitbucket.example.com/scm/prj/test.git').with {
            assertEquals('prj', project)
            assertEquals('test', repository)
        }
    }

}

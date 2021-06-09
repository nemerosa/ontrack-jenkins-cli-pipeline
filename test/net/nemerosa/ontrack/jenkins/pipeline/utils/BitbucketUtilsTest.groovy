package net.nemerosa.ontrack.jenkins.pipeline.utils


import org.junit.Test

import static org.junit.Assert.assertEquals

class BitbucketUtilsTest {

    @Test(expected = RuntimeException)
    void 'Null'() {
        BitbucketUtils.getBitbucketRepository(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank'() {
        BitbucketUtils.getBitbucketRepository('')
    }

    @Test
    void 'Bitbucket server'() {
        BitbucketUtils.getBitbucketRepository('ssh://git@bitbucket.example.com/prj/test.git').with {
            assertEquals('prj', project)
            assertEquals('test', repository)
        }
        BitbucketUtils.getBitbucketRepository('https://bitbucket.example.com/scm/prj/test.git').with {
            assertEquals('prj', project)
            assertEquals('test', repository)
        }
    }

    @Test
    void 'Bitbucket cloud'() {
        BitbucketUtils.getBitbucketRepository('https://nemerosa_net@bitbucket.org/nemerosa_net/ontrack-test.git').with {
            assertEquals('nemerosa_net', project)
            assertEquals('ontrack-test', repository)
        }
        BitbucketUtils.getBitbucketRepository('git@bitbucket.org:nemerosa_net/ontrack-test.git').with {
            assertEquals('nemerosa_net', project)
            assertEquals('ontrack-test', repository)
        }
    }

}

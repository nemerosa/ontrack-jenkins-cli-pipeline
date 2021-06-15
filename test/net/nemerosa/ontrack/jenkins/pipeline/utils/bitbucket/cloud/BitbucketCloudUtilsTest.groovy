package net.nemerosa.ontrack.jenkins.pipeline.utils.bitbucket.cloud

import org.junit.Test

import static org.junit.Assert.assertEquals

class BitbucketCloudUtilsTest {

    @Test(expected = RuntimeException)
    void 'Null'() {
        BitbucketCloudUtils.getBitbucketRepository(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank'() {
        BitbucketCloudUtils.getBitbucketRepository('')
    }

    @Test
    void 'Bitbucket cloud'() {
        BitbucketCloudUtils.getBitbucketRepository('https://nemerosa_net@bitbucket.org/nemerosa_net/ontrack-test.git').with {
            assertEquals('nemerosa_net', workspace)
            assertEquals('ontrack-test', repository)
        }
        BitbucketCloudUtils.getBitbucketRepository('git@bitbucket.org:nemerosa_net/ontrack-test.git').with {
            assertEquals('nemerosa_net', workspace)
            assertEquals('ontrack-test', repository)
        }
    }

}

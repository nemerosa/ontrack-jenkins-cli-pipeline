package net.nemerosa.ontrack.jenkins.pipeline.utils


import org.junit.Test

import static org.junit.Assert.assertEquals

class GitHubUtilsTest {

    @Test(expected = RuntimeException)
    void 'Null for owner'() {
        GitHubUtils.getOwner(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank for owner'() {
        GitHubUtils.getOwner('')
    }

    @Test
    void 'Owner'() {
        assertEquals('nemerosa', GitHubUtils.getOwner('https://github.com/nemerosa/ontrack.git'))
        assertEquals('nemerosa', GitHubUtils.getOwner('git@github.com:nemerosa/ontrack.git'))
    }

    @Test(expected = RuntimeException)
    void 'Null for repository'() {
        GitHubUtils.getRepository(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank for repository'() {
        GitHubUtils.getRepository('')
    }

    @Test
    void 'Repository'() {
        assertEquals('ontrack', GitHubUtils.getRepository('https://github.com/nemerosa/ontrack.git'))
        assertEquals('ontrack', GitHubUtils.getRepository('git@github.com:nemerosa/ontrack.git'))
    }

}

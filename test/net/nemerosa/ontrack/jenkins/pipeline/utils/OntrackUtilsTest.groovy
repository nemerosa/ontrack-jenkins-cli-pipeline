package net.nemerosa.ontrack.jenkins.pipeline.utils


import org.junit.Test

import static org.junit.Assert.assertEquals

class OntrackUtilsTest {

    @Test(expected = RuntimeException)
    void 'Missing URL for project name'() {
        OntrackUtils.getProjectName(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank URL for project name'() {
        OntrackUtils.getProjectName("")
    }

    @Test
    void 'Project names'() {
        assertEquals("ontrack-jenkins-cli-pipeline", OntrackUtils.getProjectName("git@github.com:nemerosa/ontrack-jenkins-cli-pipeline.git"))
        assertEquals("cli", OntrackUtils.getProjectName("ssh://git@bitbucket.nemerosa.com/prj/cli.git"))
        assertEquals("cli", OntrackUtils.getProjectName("https://bitbucket.nemerosa.com/scm/prj/cli.git"))
        assertEquals("ontrack", OntrackUtils.getProjectName("https://github.com/nemerosa/ontrack.git"))
    }

    @Test(expected = RuntimeException)
    void 'Missing env for branch name'() {
        OntrackUtils.getBranchName(null)
    }

    @Test(expected = RuntimeException)
    void 'Blank env for branch name'() {
        OntrackUtils.getBranchName("")
    }

    @Test
    void 'Branch names'() {
        assertEquals("main", OntrackUtils.getBranchName("main"))
        assertEquals("release-1.0", OntrackUtils.getBranchName("release/1.0"))
        assertEquals("feature-123-test", OntrackUtils.getBranchName("feature/123-test"))
    }

}

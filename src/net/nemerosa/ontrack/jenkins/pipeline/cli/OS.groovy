package net.nemerosa.ontrack.jenkins.pipeline.cli

/**
 * Name and arch of the OS, mapped to what is needed by GitHub release paths.
 */
class OS {

    private final String name
    private final String arch

    OS(String name, String arch) {
        this.name = name
        this.arch = arch
    }

    /**
     * Name of the OS
     */
    String getName() {
        return name
    }

    /**
     * Name of the OS Arch
     */
    String getArch() {
        return arch
    }

    /**
     * Extension (for Windows)
     */
    String getExtension() {
        if (name == 'windows') {
            return '.exe'
        } else {
            return ''
        }
    }
}

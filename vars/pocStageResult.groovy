import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode

def call() {
    def stageName = env.STAGE_NAME
    def build = currentBuild.rawBuild
    def startNode = build.getExecution().getNodes().find {
        it instanceof StepStartNode &&
                it.getDisplayName() == stageName
    }
    if (startNode != null) {
        for (def action in startNode.allActions) {
            println("action = $action")
        }
        def result = startNode.getAction(org.jenkinsci.plugins.workflow.actions.StageStatusAction.class)?.getResult()
        if (result != null) {
            println "Result of stage '${stageName}': ${result}"
        }
    }

}
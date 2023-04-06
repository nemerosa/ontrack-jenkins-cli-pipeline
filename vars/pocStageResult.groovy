import org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode
import org.jenkinsci.plugins.workflow.actions.StageStatusStepExecutionMonitor

def call() {
    def stageName = env.STAGE_NAME
    def build = currentBuild.rawBuild
    def startNode = build.execution.nodes.find {
        it instanceof StepStartNode &&
                it.getDisplayName() == stageName
    }
    if (startNode != null) {
        def result = startNode.getAction(StageStatusStepExecutionMonitor.Status.class)?.getResult()
        if (result != null) {
            println "Result of stage '${stageName}': ${result}"
        }
    }

}
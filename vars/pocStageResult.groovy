import org.jenkinsci.plugins.workflow.actions.LabelAction

def call() {
    def stageName = env.STAGE_NAME
    def action = currentBuild.rawBuild.getAction(LabelAction.class)
    String result = action ? action.getShortDescription() : "UNKNOWN"
    echo "Result of stage '${stageName}': ${result}"
}
import org.jenkinsci.plugins.workflow.actions.LabelAction

def call() {
    def stageName = env.STAGE_NAME
    def actions = currentBuild.rawBuild.allActions
    for (def action in actions) {
        echo "Action: $action"
    }
    def action = currentBuild.rawBuild.getAction(LabelAction.class)
    String result = action ? action.getShortDescription() : "UNKNOWN"
    echo "Result of stage '${stageName}': ${result}"
}
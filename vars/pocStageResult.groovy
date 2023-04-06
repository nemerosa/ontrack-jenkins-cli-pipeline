import net.nemerosa.ontrack.jenkins.pipeline.utils.JenkinsUtils
import org.jenkinsci.plugins.workflow.graph.FlowNode

def call() {
    String stageName = env.STAGE_NAME
    FlowNode flowNode = getContext(FlowNode)
    def stageNode = JenkinsUtils.getStageNode(flowNode, stageName)
    println("stageNode = $stageNode")
    println("stageNode.allActions = ${stageNode.allActions}")
}
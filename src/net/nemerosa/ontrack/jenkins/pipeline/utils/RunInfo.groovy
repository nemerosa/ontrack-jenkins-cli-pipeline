package net.nemerosa.ontrack.jenkins.pipeline.utils

class RunInfo {

    private String sourceType = null
    private String sourceUri = null
    private String triggerType = null
    private String triggerData = null
    private Long runTime = null

    String getSourceType() {
        return sourceType
    }

    void setSourceType(String sourceType) {
        this.sourceType = sourceType
    }

    String getSourceUri() {
        return sourceUri
    }

    void setSourceUri(String sourceUri) {
        this.sourceUri = sourceUri
    }

    String getTriggerType() {
        return triggerType
    }

    void setTriggerType(String triggerType) {
        this.triggerType = triggerType
    }

    String getTriggerData() {
        return triggerData
    }

    void setTriggerData(String triggerData) {
        this.triggerData = triggerData
    }

    Long getRunTime() {
        return runTime
    }

    void setRunTime(Long runTime) {
        this.runTime = runTime
    }

    boolean isEmpty() {
        return sourceType == null &&
                sourceUri == null &&
                triggerType == null &&
                triggerData == null &&
                runTime == null
    }


    @Override
    String toString() {
        return "RunInfo{" +
                "sourceType='" + sourceType + '\'' +
                ", sourceUri='" + sourceUri + '\'' +
                ", triggerType='" + triggerType + '\'' +
                ", triggerData='" + triggerData + '\'' +
                ", runTime=" + runTime +
                '}'.toString()
    }
}

package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

class AutoVersioningDependency {

    private final String sourceProject
    private final String sourceBranch
    private final String sourcePromotion
    private final String targetPath
    private final String targetRegex
    private final String targetProperty
    private final String targetPropertyRegex
    private final String targetPropertyType
    private final Boolean autoApproval
    private final String upgradeBranchPattern
    private final String validationStamp
    private final String postProcessing
    private final Map<String, ?> postProcessingConfig
    private final String autoApprovalMode

    AutoVersioningDependency(String sourceProject, String sourceBranch, String sourcePromotion, String targetPath, String targetRegex, String targetProperty, String targetPropertyRegex, String targetPropertyType, Boolean autoApproval, String upgradeBranchPattern, String validationStamp, String postProcessing, Map<String, ?> postProcessingConfig, String autoApprovalMode) {
        this.sourceProject = sourceProject
        this.sourceBranch = sourceBranch
        this.sourcePromotion = sourcePromotion
        this.targetPath = targetPath
        this.targetRegex = targetRegex
        this.targetProperty = targetProperty
        this.targetPropertyRegex = targetPropertyRegex
        this.autoApproval = autoApproval
        this.upgradeBranchPattern = upgradeBranchPattern
        this.validationStamp = validationStamp
        this.postProcessing = postProcessing
        this.postProcessingConfig = postProcessingConfig
        this.targetPropertyType = targetPropertyType
        this.autoApprovalMode = autoApprovalMode
    }

    String getSourceProject() {
        return sourceProject
    }

    String getSourceBranch() {
        return sourceBranch
    }

    String getSourcePromotion() {
        return sourcePromotion
    }

    String getTargetPath() {
        return targetPath
    }

    String getTargetRegex() {
        return targetRegex
    }

    String getTargetProperty() {
        return targetProperty
    }

    String getTargetPropertyRegex() {
        return targetPropertyRegex
    }

    String getTargetPropertyType() {
        return targetPropertyType
    }

    Boolean getAutoApproval() {
        return autoApproval
    }

    String getUpgradeBranchPattern() {
        return upgradeBranchPattern
    }

    String getValidationStamp() {
        return validationStamp
    }

    String getPostProcessing() {
        return postProcessing
    }

    Map<String, ?> getPostProcessingConfig() {
        return postProcessingConfig
    }

    String getAutoApprovalMode() {
        return autoApprovalMode
    }


    @Override
    String toString() {
        return "AutoVersioningDependency{" +
                "sourceProject='" + sourceProject + '\'' +
                ", sourceBranch='" + sourceBranch + '\'' +
                ", sourcePromotion='" + sourcePromotion + '\'' +
                ", targetPath='" + targetPath + '\'' +
                ", targetRegex='" + targetRegex + '\'' +
                ", targetProperty='" + targetProperty + '\'' +
                ", targetPropertyRegex='" + targetPropertyRegex + '\'' +
                ", targetPropertyType='" + targetPropertyType + '\'' +
                ", autoApproval=" + autoApproval +
                ", upgradeBranchPattern='" + upgradeBranchPattern + '\'' +
                ", validationStamp='" + validationStamp + '\'' +
                ", postProcessing='" + postProcessing + '\'' +
                ", postProcessingConfig=" + postProcessingConfig +
                ", autoApprovalMode='" + autoApprovalMode + '\'' +
                '}' as String
    }
}

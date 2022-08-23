package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

import groovy.transform.Canonical

@Canonical
class AutoVersioningDependency {

    String sourceProject
    String sourceBranch
    String sourcePromotion
    String targetPath
    String targetRegex
    String targetProperty
    String targetPropertyRegex
    String targetPropertyType
    Boolean autoApproval
    String upgradeBranchPattern
    String validationStamp
    String postProcessing
    Map<String, ?> postProcessingConfig
    String autoApprovalMode

}

def call(Map<String,?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    String stamp = ParamUtils.getParam(params, "stamp")
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

}

def call(Map<String,?> params = [:]) {
    // CLI download
    ontrackCliDownload(params)
    // CLI setup
    ontrackCliConnect(params)
}

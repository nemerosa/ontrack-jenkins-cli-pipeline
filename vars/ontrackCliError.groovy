def call(String message) {
    if (ontrackCliIgnoreErrors()) {
        echo("[Yontrack ERROR] $message")
    } else {
        error(message)
    }
}
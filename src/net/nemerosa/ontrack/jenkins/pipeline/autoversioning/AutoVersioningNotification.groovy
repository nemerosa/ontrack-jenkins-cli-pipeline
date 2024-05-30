package net.nemerosa.ontrack.jenkins.pipeline.autoversioning

class AutoVersioningNotification {

    private final String channel
    private final def config
    private final List<String> scope
    private final String notificationTemplate

    AutoVersioningNotification(String channel, config, List<String> scope, String notificationTemplate) {
        this.channel = channel
        this.config = config
        this.scope = scope
        this.notificationTemplate = notificationTemplate
    }

    String getChannel() {
        return channel
    }

    def getConfig() {
        return config
    }

    List<String> getScope() {
        return scope
    }

    String getNotificationTemplate() {
        return notificationTemplate
    }

    Map<String, ?> toMap() {
        return [
                channel             : channel,
                config              : config,
                scope               : scope,
                notificationTemplate: notificationTemplate,
        ]
    }
}
package a.gleb.universitytgspeechbot.exception

class BotRegistrationException(message: String?) : RuntimeException(message) {}

class BotExecuteCommandException(message: String?) : RuntimeException(message) {}

class InvalidBotCommandException(message: String?) : RuntimeException(message) {}
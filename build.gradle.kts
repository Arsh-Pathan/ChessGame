plugins {
    application
    java
}

application {
    mainClass.set("io.arsh.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
}
group = "io.arsh"
version = "2.0.2"

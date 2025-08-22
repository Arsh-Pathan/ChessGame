plugins {
    application
    java
}

application {
    mainClass.set("io.arsh.Main")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}

group = "io.arsh"
version = "2.0.2"

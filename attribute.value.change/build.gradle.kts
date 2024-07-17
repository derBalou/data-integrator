plugins {
    id("java")
}

group = "de.henrik.bt"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

configurations {
    create("invoker")
}

dependencies {
    // Every function needs this dependency to get the Functions Framework API.
    compileOnly("com.google.cloud.functions:functions-framework-api:1.1.0")

    //invoker("com.google.cloud.functions.invoker:java-function-invoker:1.3.1")

    // These dependencies are only used by the tests.
    testImplementation("com.google.cloud.functions:functions-framework-api:1.1.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.4.0")
    testImplementation("org.mockito:mockito-core:5.10.0")

}

tasks.test {
    useJUnitPlatform()
}

/* register a task to run functions locally
tasks.register<JavaExec>("runFunction") {
    group = "application"
    description = "Run functions locally"
    main = "com.google.cloud.functions.invoker.runner.Invoker"
    classpath = configurations.invoker
    inputs.files(configurations.runtimeClasspath, sourceSets.main)
    args = listOf("--target", "de.henrik.bt.HelloFunctions")
}
*/

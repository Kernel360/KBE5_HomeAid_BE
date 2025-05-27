plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":global"))
    implementation(project(":admin"))
    implementation(project(":board"))
    implementation(project(":matching"))
    implementation(project(":payment"))
    implementation(project(":reservation"))
    implementation(project(":user"))
    implementation(project(":worklog"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    runtimeOnly("com.h2database:h2")
}

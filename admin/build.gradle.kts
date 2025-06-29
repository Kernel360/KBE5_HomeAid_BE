plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    implementation(project(":common-domain"))
    implementation(project(":global"))
    implementation(project(":payment"))
    implementation(project(":user"))
    implementation(project(":board"))
    implementation(project(":reservation"))
    implementation(project(":review"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    runtimeOnly("com.mysql:mysql-connector-j")
}

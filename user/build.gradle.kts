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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation ("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation ("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation ("io.jsonwebtoken:jjwt-jackson:0.12.3")

    implementation ("org.springframework.boot:spring-boot-starter-security")
    testImplementation ("org.springframework.security:spring-security-test")


    implementation(project(":common-domain"))
    implementation(project(":global"))
}
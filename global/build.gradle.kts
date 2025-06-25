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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring Cloud AWS Starter
    implementation("io.awspring.cloud:spring-cloud-aws-starter:3.1.1")
    // AWS Java SDK For Amazon S3
    implementation("com.amazonaws:aws-java-sdk-s3:1.12.767")

    // jwt
    implementation ("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation ("io.jsonwebtoken:jjwt-impl:0.12.3")
    implementation ("io.jsonwebtoken:jjwt-jackson:0.12.3")

}

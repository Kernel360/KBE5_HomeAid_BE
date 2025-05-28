plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


dependencies {
    implementation(project(":global"))
    implementation(project(":common-domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")
    runtimeOnly("com.mysql:mysql-connector-j")
}

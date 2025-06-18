plugins {
	java
	id("org.springframework.boot") version "3.4.6"
	id("io.spring.dependency-management") version "1.1.7"
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

allprojects {
	apply(plugin = "java")
	apply(plugin = "io.spring.dependency-management")

	group = "com.homeaid"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}


}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation(project(":user"))
	implementation(project(":payment"))
	implementation(project(":reservation"))
	implementation(project(":board"))
	implementation(project(":worklog"))
	implementation(project(":global"))
	implementation(project(":admin"))
	implementation(project(":review"))
	implementation(project(":common-domain"))
}

subprojects {
	dependencies {
		implementation("org.springframework.boot:spring-boot-starter")
		compileOnly("org.projectlombok:lombok")
		annotationProcessor("org.projectlombok:lombok")
		testImplementation("com.h2database:h2")
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")

		// redis
		implementation ("org.springframework.boot:spring-boot-starter-data-redis")

		implementation ("org.springframework.boot:spring-boot-starter-security")
		testImplementation ("org.springframework.security:spring-security-test")

	}

	configurations {
		compileOnly {
			extendsFrom(configurations.annotationProcessor.get())
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}

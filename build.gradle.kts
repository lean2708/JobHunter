plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.freefair.lombok") version "8.6"
}

group = "anb52"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation ("org.modelmapper:modelmapper:2.4.4")
	implementation("org.springframework.boot:spring-boot-starter-web")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation ("org.springframework.boot:spring-boot-starter-security")
	implementation ("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.turkraft.springfilter:jpa:3.1.7")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

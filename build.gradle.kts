import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
}

group = "com.polarbookshop"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2023.0.1"
extra["otelVersion"] = "2.6.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.cloud:spring-cloud-stream-binder-rabbit")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.springframework.cloud:spring-cloud-stream-test-binder")

	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	runtimeOnly("io.opentelemetry.javaagent:opentelemetry-javaagent:${property("otelVersion")}")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.bootBuildImage {
	imageName.set(project.name)
	environment.set(mapOf("BP_JVM_VERSION" to "17.*"))

	docker {
		publishRegistry {
			project.findProperty("registryUsername")?.let { username.set(it as String) }
			project.findProperty("registryToken")?.let { password.set(it as String) }
			project.findProperty("registryUrl")?.let { url.set(it as String) }
		}
	}
}

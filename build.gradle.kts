import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.6"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.31"
	kotlin("plugin.spring") version "1.5.31"
	id("jacoco")
}

group = "com.fif.payments"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2020.0.4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	//implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	//implementation("org.springframework.boot:spring-boot-starter-data-redis")
	//implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2-native-mt")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2-native-mt")

	implementation("net.logstash.logback:logstash-logback-encoder:6.6")

	implementation("io.springfox:springfox-boot-starter:3.0.0")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
	}

	// Arch unit
	testImplementation("com.tngtech.archunit:archunit:0.22.0")
	testImplementation("com.tngtech.archunit:archunit-junit5:0.22.0")

	testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
	//testImplementation("org.testcontainers:junit-jupiter")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict", "-Werror")
		jvmTarget = "11"
		allWarningsAsErrors = true
	}
}

tasks.withType<JacocoReport> {
	reports {
		xml.required.set(true)
	}
}


tasks.getByName<Jar>("jar") {
	enabled = false
}

tasks.register<Copy>("copy")

tasks {

	var suffix = "macos"
	if (org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)) {
		suffix = "windows"
	}

	"copy"(Copy::class) {
		from("hooks/pre-push-$suffix") {
			rename { it.removeSuffix("-$suffix") }
		}
		into(".git/hooks").fileMode = 0b111101101
	}

	"build" {
		dependsOn("copy")
	}
}

springBoot {
	buildInfo()
}

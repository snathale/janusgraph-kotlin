import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
plugins {
    application
    java
    val kotlinVersion = "1.2.60"
    id("org.springframework.boot") version "2.0.2.RELEASE"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
}

group = "br.com.ntopus"
version = "0.0.1-SNAPSHOT"

application {
    mainClassName = "br.com.ntopus.accesscontrol.AccessControlApplication"
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
val kotlinVersion = "1.2.51"
dependencies {
	compile ("org.springframework.boot:spring-boot-starter-web")
	compile ("org.springframework.boot:spring-boot-starter-actuator")
	compile ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
	compile ("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    compile ("org.mockito:mockito-all:1.9.5")
//	runtime ("org.springframework.boot:spring-boot-devtools")
	testCompile ("org.springframework.boot:spring-boot-starter-test")
//    compile ("com.github.pm-dev:kotlin-janusgraph-ogm:0.13.1")
    compile ("org.apache.tinkerpop:gremlin-core:3.3.3")
//    compile ("org.apache.tinkerpop:gremlin-driver:3.3.3")
//    compile ("org.apache.tinkerpop:hadoop-gremlin:3.3.3")
//    compile ("org.apache.tinkerpop:spark-gremlin:3.3.3")
    compile("com.syncleus.ferma:ferma:3.2.1")
    compile ("org.janusgraph:janusgraph-core:0.2.1")
    compile ("org.janusgraph:janusgraph-cassandra:0.2.1")
    compile ("org.janusgraph:janusgraph-es:0.2.1")
    compile ("org.janusgraph:janusgraph-cql:0.2.1")
    compile ("org.jetbrains.kotlin:kotlin-runtime:${kotlinVersion}")
    compile ("com.google.code.gson:gson:2.8.5")
}
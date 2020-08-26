import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

version = "0.0.2"

tasks {
    "bootJar"(BootJar::class) {
        enabled = false
    }
    "bootRun"(BootRun::class) {
        enabled = false
    }
    "jar"(Jar::class){
        enabled = true
    }
}


dependencies {
    api(project(":bialydunajec-ddd:bialydunajec-ddd-application"))
    api(project(":bialydunajec-registrations:bialydunajec-registrations-domain"))
    api(project(":bialydunajec-camp-edition:bialydunajec-camp-edition-messages"))
    api(project(":bialydunajec-academic-ministry:bialydunajec-academic-ministry-messages"))
    api(project(":bialydunajec-registrations:bialydunajec-registrations-messages"))
    api(project(":bialydunajec-registrations:bialydunajec-registrations-dto"))
    api(project(":bialydunajec-email:bialydunajec-email-messages"))

    compile("org.springframework.boot:spring-boot-starter-cache")
    compile("org.springframework.boot:spring-boot-starter-validation")

    testCompile("org.springframework.boot:spring-boot-starter-test")
}

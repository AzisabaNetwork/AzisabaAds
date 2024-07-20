plugins {
    id("java")
}

group = "net.azisaba"
version = "1.0.1"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

repositories {
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/groups/public/") }
    maven { url = uri("https://jitpack.io/") }
    maven { url = uri("https://repo.azisaba.net/repository/maven-public/") }
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:Vault:1.7.3") {
        exclude("org.bstats", "bstats-bukkit")
        exclude("net.milkbowl.vault", "VaultAPI")
    }
    compileOnly("net.azisaba:LifeCore:6.12.1")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
}

import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.compiler
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings

plugins {
    java
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.4.0"
    id("com.gradleup.shadow") version "9.4.1"
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.4.1"
    id("xyz.wagyourtail.unimined") version "1.4.23-kappa"
    id("net.kyori.blossom") version "2.2.0"
}

val mod_version: String by project
val root_package: String by project
val mod_id: String by project
val mod_name: String by project

require(mod_version.isNotEmpty()) { "mod_version is empty!" }
require(root_package.isNotEmpty()) { "root_package is empty!" }
require(mod_id.isNotEmpty()) { "mod_id is empty!" }
require(mod_name.isNotEmpty()) { "mod_name is empty!" }

val generate_sources_jar: String by project
val generateSourcesJar = generate_sources_jar.toBoolean()
val generate_javadocs_jar: String by project
val generateJavadocsJar = generate_javadocs_jar.toBoolean()
val minecraft_username: String by project
val extra_jvm_args: String by project
val enable_shadow: String by project
val enableShadow = enable_shadow.toBoolean()
val use_access_transformer: String by project
val useAccessTransformer = use_access_transformer.toBoolean()
val is_coremod: String by project
val isCoremod = is_coremod.toBoolean()
val coremod_includes_mod: String by project
val coremodIncludesMod = coremod_includes_mod.toBoolean()
val coremod_plugin_class_name: String by project
val use_asset_mover: String by project
val useAssetMover = use_asset_mover.toBoolean()
val asset_mover_version: String by project
val enable_junit_testing: String by project
val enableJunitTesting = enable_junit_testing.toBoolean()
val show_testing_output: String by project
val showTestingOutput = show_testing_output.toBoolean()
val enable_foundation_debug: String by project
val enableFoundationDebug = enable_foundation_debug.toBoolean()
val mod_description: String by project
val mod_authors: String by project
val mod_credits: String by project
val mod_url: String by project
val mod_update_json: String by project
val mod_logo_path: String by project

val access_transformer_locations: String = "${mod_id}_at.cfg"

if (useAccessTransformer) {
    require(access_transformer_locations.isNotEmpty()) { "access_transformer_locations is empty!" }
}
if (isCoremod) {
    require(coremod_plugin_class_name.isNotEmpty()) { "coremod_plugin_class_name is empty!" }
}
if (useAssetMover) {
    require(asset_mover_version.isNotEmpty()) { "asset_mover_version is empty!" }
}

version = mod_version
group = root_package

base {
    archivesName.set(mod_id)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    if (generateSourcesJar) {
        withSourcesJar()
    }
    if (generateJavadocsJar) {
        withJavadocJar()
    }
}

kotlin {
    jvmToolchain(25)
}

configurations {
    val contain by creating
    implementation { extendsFrom(contain) }
    val modCompileOnly by creating
    compileOnly { extendsFrom(modCompileOnly) }
    val modRuntimeOnly by creating
    runtimeOnly { extendsFrom(modRuntimeOnly) }
}

val remapTaskName = if (enableShadow) "remapShadowJar" else "remapJar"

unimined.minecraft {
    version("1.12.2")

    mappings {
        mcp("stable", "39-1.12")
    }

    cleanroom {
        if (useAccessTransformer) {
            accessTransformer("${rootProject.projectDir}/src/main/resources/$access_transformer_locations")
        }
        loader("0.5.14-alpha")
        runs.all {
            args.addAll(listOf("--username", minecraft_username))
            if (extra_jvm_args.isNotEmpty()) {
                jvmArgs(extra_jvm_args.split("\\s+"))
            }
            if (enableFoundationDebug) {
                systemProperties.apply {
                    set("foundation.dump", "true")
                    set("foundation.verbose", "true")
                }
            }
            if (isCoremod) {
                systemProperty("fml.coreMods.load", coremod_plugin_class_name)
            }
        }
    }

    defaultRemapJar = false

    val jarTaskName = if (enableShadow) "shadowJar" else "jar"

    remap(tasks.named(jarTaskName).get()) {
        mixinRemap {
            enableBaseMixin()
            enableMixinExtra()
            disableRefmap()
        }
    }

    mods {
        val modCompileOnly by configurations.getting
        val modRuntimeOnly by configurations.getting
        remap(modCompileOnly)
        remap(modRuntimeOnly)
    }
}

dependencies {
    if (useAssetMover) {
        implementation("com.cleanroommc:assetmover:$asset_mover_version")
    }
    if (enableJunitTesting) {
        testImplementation("org.junit.jupiter:junit-jupiter:6.0.3")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

apply(plugin = "dependencies")

tasks.processResources {
    rename("(.+_at.cfg)", "META-INF/$1")
}

sourceSets {
    main {
        blossom {
            kotlinSources {
                property("mod_id", mod_id)
                property("mod_name", mod_name)
                property("mod_version", mod_version)
                property("package", "$root_package.$mod_id")
            }
            resources {
                property("mod_id", mod_id)
                property("mod_name", mod_name)
                property("mod_version", mod_version)
                property("mod_description", mod_description)
                property("mod_authors", mod_authors.takeIf { it.isNotBlank() }
                    ?.split(",")?.filter { it.isNotBlank() }
                    ?.joinToString("\", \"") { it.trim() } ?: "")
                property("mod_credits", mod_credits)
                property("mod_url", mod_url)
                property("mod_update_json", mod_update_json)
                property("mod_logo_path", mod_logo_path)
            }
        }
    }
}

if (!enableShadow) {
    tasks.shadowJar { enabled = false }
}

idea {
    module {
        inheritOutputDirs = true
    }
    project {
        settings {
            runConfigurations {
                add(Gradle("1. Build").apply {
                    setProperty("taskNames", listOf("build"))
                })
                add(Gradle("2. Run Client").apply {
                    setProperty("taskNames", listOf("runClient"))
                })
                add(Gradle("3. Run Server").apply {
                    setProperty("taskNames", listOf("runServer"))
                })
            }
            compiler.javac {
                afterEvaluate {
                    javacAdditionalOptions = "-encoding utf8"
                }
            }
        }
    }
}

tasks.jar {
    archiveClassifier = "dev"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    val contain by configurations.getting
    if (!contain.isEmpty) {
        into("/") {
            from(contain)
        }
    }
    doFirst {
        manifest {
            val attributeMap = mutableMapOf<String, Any>()
            attributeMap["ModType"] = "CRL"
            if (!contain.isEmpty) {
                attributeMap["ContainedDeps"] = contain.joinToString(" ") { it.name }
                attributeMap["NonModDeps"] = true
            }
            if (isCoremod) {
                attributeMap["FMLCorePlugin"] = coremod_plugin_class_name
                if (coremodIncludesMod) {
                    attributeMap["FMLCorePluginContainsFMLMod"] = true
                }
            }
            if (useAccessTransformer) {
                attributeMap["FMLAT"] = access_transformer_locations
            }
            attributes(attributeMap)
        }
    }
    finalizedBy(tasks.named(remapTaskName).get())
}

tasks.shadowJar {
    configurations.add(project.configurations.shadow)
    archiveClassifier = "shadow"
}

tasks.named(remapTaskName) {
    doFirst {
        logging.captureStandardOutput(LogLevel.INFO)
    }
    doLast {
        logging.captureStandardOutput(LogLevel.QUIET)
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.test {
    useJUnitPlatform()
    javaLauncher =
        javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(25)
        }

    if (showTestingOutput) {
        testLogging {
            showStandardStreams = true
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

apply(plugin = "publishing")
apply(plugin = "extra")

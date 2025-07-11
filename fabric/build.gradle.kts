import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask

val isSnapshot = !hasProperty("snapshot") || findProperty("snapshot") == "true"

architectury {
	platformSetupLoomIde()
	fabric()
}

loom {
	accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

configurations {
	getByName("developmentFabric").extendsFrom(configurations["common"])
}

repositories {
	maven("https://maven.terraformersmc.com/releases/")
}

dependencies {
	modImplementation "curse.maven:pandas-falling-trees-880630:6579954"
	modApi("net.fabricmc.fabric-api:fabric-api:${properties["fabric_api_version"]}")

	modImplementation "curse.maven:pandas-falling-trees-880630:6579954"
	modApi("dev.architectury:architectury-fabric:${properties["deps_architectury_version"]}")
	modApi("com.terraformersmc:modmenu:${properties["deps_modmenu_version"]}")

	modCompileOnly("maven.modrinth:jade:${properties["deps_jade_fabric_version"]}+fabric-fabric,${properties["deps_jade_mc_version"]}")
//	modLocalRuntime("maven.modrinth:jade:${properties["deps_jade_fabric_version"]}+fabric-fabric,${properties["deps_jade_mc_version"]}")

	common(project(":common", "namedElements")) { isTransitive = false }
	shadowBundle(project(":common", "transformProductionFabric"))
}

tasks.remapJar {
	injectAccessWidener.set(true)
}

tasks.withType<RemapJarTask> {
	val shadowJar = tasks.getByName<ShadowJar>("shadowJar")
	inputFile.set(shadowJar.archiveFile)
}

publishing {
	publications {
		register("mavenJava", MavenPublication::class) {
			groupId = properties["maven_group"] as String
			artifactId = "${properties["mod_id"]}-${project.name}"
			version = "${project.version}"

			from(components["java"])
		}
	}
}

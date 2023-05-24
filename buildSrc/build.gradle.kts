plugins {
	kotlin("jvm") version "1.8.21"
	`java-gradle-plugin`
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(gradleApi())
}

base {
	archivesName.set("assetgen")
}

gradlePlugin {
	plugins {
		register("assetgen") {
			id = "com.balduvian.assetgen"
			displayName = "CNGE Asset Code Generation"
			description = "Coming soon..."
			implementationClass = "AssetGenPlugin"
		}
	}
}

pluginManagement{
    repositories{
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement{
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories{
        google()
        mavenCentral()
        maven{url=uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-js-wrappers")}
    }
}

rootProject.name = "KreuzwortApp"
include(":app")
 
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyJetpackProject"
include(":app")
include(":core:model")
include(":core:common")
include(":core:network")
include(":core:datastore")
include(":core:ui")
include(":data")

include(":feature:auth")
include(":feature:search")
include(":feature:booking")
include(":navigation")
include(":core:common-vm")

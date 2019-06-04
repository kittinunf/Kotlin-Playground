import com.github.kittinunf.gradle.ReleaseToMasterMergeTask

buildscript {
    repositories {
        maven { setUrl("https://kotlin.bintray.com/kotlinx") }

        mavenCentral()
        google()
    }

    dependencies {
        //kotlin
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
        classpath("com.android.tools.build:gradle:3.4.1")
    }
}

allprojects {
    repositories {
        maven { setUrl("https://kotlin.bintray.com/kotlinx") }

        google()
        jcenter()
    }
}

tasks.register<ReleaseToMasterMergeTask>("mergeReleaseToMaster") {
    val tokenKey = "GITHUB_API_TOKEN"
    token = project.properties.getOrDefault(tokenKey, "") as String
}

repositories {
    jcenter()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())

    implementation("org.eclipse.jgit:org.eclipse.jgit:5.3.0.201903130848-r")
    implementation("com.github.kittinunf.fuel:fuel:2.1.0")
    implementation("com.github.kittinunf.fuel:fuel-forge:2.1.0") {
        exclude(group = "com.github.kittinunf.forge")
    }
    implementation("com.github.kittinunf.forge:forge:1.0.0-alpha2")
}

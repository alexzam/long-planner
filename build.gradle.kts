allprojects {
    group = "com.github.alexzam.home"
    version = "0.0.1-SNAPSHOT"

    val gitlabToken: String by project

    repositories {
        mavenCentral()

        maven {
            url = uri("https://gitlab.com/api/v4/projects/19126424/packages/maven")
            name = "GitLab-entityvc"
            credentials(HttpHeaderCredentials::class) {
                name = "Private-Token"
                value = gitlabToken
            }
            authentication {
                create("header", HttpHeaderAuthentication::class)
            }
        }
    }

    val ktorVer by extra("1.6.2")
    val logbackVer by extra("1.2.5")
    val entityvcVer by extra("0.4.1")
}

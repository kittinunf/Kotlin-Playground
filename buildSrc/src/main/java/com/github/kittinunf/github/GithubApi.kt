package com.github.kittinunf.github

import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.core.Method.*
import com.github.kittinunf.fuel.util.FuelRouting

val MERPAY = GithubApi.Repo("kouzoh", "merpay-android")
val MERCARI = GithubApi.Repo("kouzoh", "mercari-android")

// CRUD style
sealed class GithubApi(repo: Repo) : FuelRouting {

    data class Repo(val owner: String, val name: String)

    private var authToken: String = ""

    override val basePath: String = "https://api.github.com/repos/${repo.owner}/${repo.name}"

    override val headers: Map<String, HeaderValues>?
        get() = mapOf("Authorization" to listOf("token $authToken"))

    override val params: Parameters? = null

    override val bytes: ByteArray? = null

    fun withToken(token: String): GithubApi {
        authToken = token
        return this
    }

    abstract class Issue(repo: Repo) : GithubApi(repo) {

        override val path: String = "/issues"

        class Update(repo: Repo, number: Int, user: String) : Issue(repo) {

            override val path: String = "/issues/$number"

            override val body: String? = """
            {
              "labels" : [],
              "assignees" : [ "$user" ]
            }
            """.trimIndent()

            override val method: Method = PATCH

            override fun call(): Request = FuelManager.instance.request(this).appendHeader(Headers.CONTENT_TYPE, "application/json")
        }
    }

    abstract class PR(repo: Repo) : GithubApi(repo) {

        enum class State(val value: String) {
            OPEN("open"),
            CLOSED("closed")
        }

        override val path: String = "/pulls"

        class Create(repo: Repo, title: String, body: String, branch: String, base: String = "master") : PR(repo) {

            override val body: String? = """
            {
              "title" : "$title",
              "head" : "$branch",
              "base" : "$base",
              "body" : "$body"
            }
            """.trimIndent()

            override val method: Method = POST
        }

        class Read(repo: Repo, head: String) : PR(repo) {

            override val body: String? = null

            override val method: Method = GET

            override val params: Parameters? = listOf("head" to "kouzoh:$head")
        }

        class Update(
            repo: Repo,
            number: Int,
            title: String,
            body: String,
            state: State = State.OPEN,
            base: String = "master"
        ) : PR(repo) {

            override val body: String? = """
            {
              "title" : "$title",
              "state" : "${state.value}",
              "base" : "$base",
              "body" : "$body"
            }
            """.trimIndent()

            override val method: Method = PATCH

            override val path: String = "/pulls/$number"

            override fun call(): Request = FuelManager.instance.request(this).appendHeader(Headers.CONTENT_TYPE, "application/json")
        }
    }

    abstract class Release(repo: Repo) : GithubApi(repo) {

        override val path: String = "/releases"

        sealed class Status(val value: String) {
            object Latest : Status("latest")
            class Tag(version: String) : Status("tags/$version")
        }

        class Read(repo: Repo, status: Status? = null) : Release(repo) {

            override val body: String? = null

            override val path: String = if (status != null) "/releases/${status.value}" else "/releases"

            override val method: Method = GET
        }

        class Create(
            repo: Repo,
            tag: GitTag,
            releaseName: String = tag.toString(),
            branch: String = "master",
            body: String = "",
            draft: Boolean = false,
            prerelease: Boolean = true
        ) : Release(repo) {

            override val body: String? = """
            {
              "tag_name": "$tag",
              "target_commitish": "$branch",
              "name": "$releaseName",
              "body": "$body",
              "draft": $draft,
              "prerelease": $prerelease
            }
            """.trimIndent()

            override val method: Method = POST

            override fun call(): Request = FuelManager.instance.request(this).appendHeader(Headers.CONTENT_TYPE, "application/json")
        }
    }

    open fun call() = FuelManager.instance.request(this)
}

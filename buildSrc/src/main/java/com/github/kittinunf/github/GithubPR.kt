package com.github.kittinunf.github

import com.github.kittinunf.forge.core.JSON
import com.github.kittinunf.forge.core.apply
import com.github.kittinunf.forge.core.at
import com.github.kittinunf.forge.core.map
import com.github.kittinunf.forge.util.create

data class GithubPR(val url: String, val id: Int) {
    val number = url.split("/").last().toInt()
}

val githubPRDeserializer = { j: JSON ->
    ::GithubPR.create
        .map(j at "url")
        .apply(j at "id")
}

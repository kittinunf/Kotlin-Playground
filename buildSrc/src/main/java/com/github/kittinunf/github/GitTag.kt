package com.github.kittinunf.github

import kotlin.math.pow

data class GitTag(val stringRepresentation: String) {

    private val splitors = stringRepresentation.split(".")

    val major = splitors[0]
    val minor = splitors[1]
    val patch = splitors[2]
    val build = splitors.getOrNull(3)

    fun calculateTagPriority(): Int = splitors.foldRightIndexed(0) { index, i, acc ->
        val iAsInt = i.toIntOrNull() ?: return@foldRightIndexed acc
        val c = 10.0f.pow(splitors.lastIndex - index) * iAsInt
        (acc + c).toInt()
    }

    override fun toString(): String = "$major.$minor.$patch${if (build != null) ".$build" else ""}"

    operator fun plus(i: Int): GitTag {
        return if (build != null) {
            val buildAsInt = build.toInt()
            GitTag("$major.$minor.$patch.${buildAsInt + i}")
        } else {
            val patchAsInt = patch.toInt()
            GitTag("$major.$minor.${patchAsInt + i}")
        }
    }
}

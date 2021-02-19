package de.twometer.neko.res

import java.io.File
import java.nio.charset.Charset

object ShaderLoader {

    private fun tokenize(text: String): List<String> {
        val result = ArrayList<String>()
        val builder = StringBuilder()

        fun addToResults() {
            if (builder.isNotEmpty()) {
                result.add(builder.toString())
                builder.clear()
            }
        }

        text.forEach {
            if (!it.isWhitespace()){
                builder.append(it)
            } else addToResults()
        }

        addToResults()

        return result
    }

    fun load(path: String) {
        val text = File(path).readText(Charset.forName("UTF-8"))
        val tokens = tokenize(text)

        tokens.forEach {

            print("$it ")

        }
    }

}
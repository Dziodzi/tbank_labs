package io.github.dziodzi

import java.io.File

@DslMarker
annotation class PrettyPrintDSL

@PrettyPrintDSL
class ReportBuilder {
    private val content = StringBuilder()

    fun header(level: Int, block: ReportBuilder.() -> Unit) {
        content.append("#".repeat(level)).append(" ")
        this.block()
        content.append("\n")
    }

    fun text(block: ReportBuilder.() -> Unit) {
        this.block()
        content.append("\n")
    }

    fun bold(text: String) {
        content.append("**").append(text).append("**")
    }

    fun link(link: String, text: String) {
        content.append("[").append(text).append("](").append(link).append(")").append("\n")
    }

    fun underlined(text: String) {
        content.append("__").append(text).append("__")
    }

    enum class ProgrammingLanguage { KOTLIN }

    fun code(language: ProgrammingLanguage, block: CodeBlock.() -> Unit) {
        content.append("```").append(language).append("\n")
        val codeBlock = CodeBlock()
        codeBlock.block()
        content.append(codeBlock.toString())
        content.append("\n```")
    }

    fun divider() {
        content.append("\n---\n")
    }

    fun list(block: ListBuilder.() -> Unit) {
        val listBuilder = ListBuilder()
        block(listBuilder)
        content.append(listBuilder.toString()).append("\n")
    }

    operator fun String.unaryPlus() {
        content.append(this)
    }

    override fun toString() = content.toString()

    @PrettyPrintDSL
    class CodeBlock {
        private val codeContent = StringBuilder()

        operator fun String.unaryPlus() {
            codeContent.append(this)
        }

        override fun toString() = codeContent.toString()
    }

    @PrettyPrintDSL
    class ListBuilder {
        private val listContent = StringBuilder()

        fun item(text: String) {
            listContent.append("- ").append(text).append("\n")
        }

        override fun toString() = listContent.toString()
    }
}

fun readme(block: ReportBuilder.() -> Unit): String {
    val builder = ReportBuilder()
    builder.block()
    return builder.toString()
}

fun writeToMarkdownFile(content: String, fileName: String) {
    File(fileName).writeText(content)
}


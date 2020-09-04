package com.github.ajalt.mordant.rendering.markdown

import com.github.ajalt.mordant.Terminal
import com.github.ajalt.mordant.rendering.*
import com.github.ajalt.mordant.rendering.internal.parseText
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.MarkdownTokenTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser


fun main() {
    val src = """
    For *example*, to **always** output ANSI RGB color codes, even if stdout is currently directed to a file,
    you can do this:
    
    ```
    pre {
        *format*
    }
    ```
    """.trimIndent()
    val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
    val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(src)
    println(parsedTree.children.joinToString("\n"))
    println("---")
    println(MarkdownRenderer(src, DEFAULT_THEME).render())
    println("---")
}

internal class MarkdownDocument(private val parts: List<Renderable>) : Renderable {
    override fun measure(t: Terminal, width: Int): WidthRange {
        return parts.maxWidthRange(t, width)
    }

    override fun render(t: Terminal, width: Int): Lines {
        return parts.fold(Lines(emptyList())) { l, r -> l + r.render(t, width) }
    }
}

private val EOL_LINES = Lines(listOf(emptyList(), emptyList()))
private val EOL_TEXT = Text(EOL_LINES, whitespace = Whitespace.PRE)


internal class MarkdownRenderer(
        private val input: String,
        private val theme: Theme
) {
    fun render(): MarkdownDocument {
        val flavour: MarkdownFlavourDescriptor = GFMFlavourDescriptor()
        val parsedTree = MarkdownParser(flavour).buildMarkdownTreeFromString(input)
        println(parsedTree.children.joinToString("\n"))
        return parseFile(parsedTree)
    }

    private fun parseFile(node: ASTNode): MarkdownDocument {
        require(node.type == MarkdownElementTypes.MARKDOWN_FILE)
        return MarkdownDocument(node.children.map { parseStructure(it) })
    }

    private fun parseStructure(node: ASTNode): Renderable {
        return when (node.type) {
            // ElementTypes
            MarkdownElementTypes.UNORDERED_LIST -> {
                UnorderedList(node.children
                        .filter { it.type == MarkdownElementTypes.LIST_ITEM }
                        .map { parseStructure(it.children[1]) }
                )
            }
            MarkdownElementTypes.ORDERED_LIST -> {
                OrderedList(node.children
                        .filter { it.type == MarkdownElementTypes.LIST_ITEM }
                        .map { parseStructure(it.children[1]) }
                )
            }
            MarkdownElementTypes.BLOCK_QUOTE -> {
                BlockQuote(MarkdownDocument(node.children.drop(1)
                        .filter { it.type != MarkdownTokenTypes.WHITE_SPACE }
                        .map { parseStructure(it) }))
            }
            MarkdownElementTypes.CODE_FENCE -> {
                // TODO better start/end linebreak handling
                Text(innerInlines(node), whitespace = Whitespace.PRE)
            }
            MarkdownElementTypes.CODE_BLOCK -> TODO("CODE_BLOCK")
            MarkdownElementTypes.CODE_SPAN -> TODO("CODE_SPAN")
            MarkdownElementTypes.HTML_BLOCK -> TODO("HTML_BLOCK")
            MarkdownElementTypes.PARAGRAPH -> {
                Text(innerInlines(node, drop = 0))
            }
            MarkdownElementTypes.LINK_DEFINITION -> TODO("LINK_DEFINITION")
            MarkdownElementTypes.LINK_LABEL -> TODO("LINK_LABEL")
            MarkdownElementTypes.LINK_DESTINATION -> TODO("LINK_DESTINATION")
            MarkdownElementTypes.LINK_TITLE -> TODO("LINK_TITLE")
            MarkdownElementTypes.LINK_TEXT -> TODO("LINK_TEXT")
            MarkdownElementTypes.INLINE_LINK -> TODO("INLINE_LINK")
            MarkdownElementTypes.FULL_REFERENCE_LINK -> TODO("FULL_REFERENCE_LINK")
            MarkdownElementTypes.SHORT_REFERENCE_LINK -> TODO("SHORT_REFERENCE_LINK")
            MarkdownElementTypes.IMAGE -> TODO("IMAGE")
            MarkdownElementTypes.AUTOLINK -> TODO("AUTOLINK")
            MarkdownElementTypes.SETEXT_1 -> TODO("SETEXT_1")
            MarkdownElementTypes.SETEXT_2 -> TODO("SETEXT_2")
            MarkdownElementTypes.ATX_1 -> TODO("ATX_1")
            MarkdownElementTypes.ATX_2 -> TODO("ATX_2")
            MarkdownElementTypes.ATX_3 -> TODO("ATX_3")
            MarkdownElementTypes.ATX_4 -> TODO("ATX_4")
            MarkdownElementTypes.ATX_5 -> TODO("ATX_5")
            MarkdownElementTypes.ATX_6 -> TODO("ATX_6")
            MarkdownTokenTypes.EOL -> EOL_TEXT
            else -> error("Unexpected token when parsing structure: $node")
        }
    }

    private fun parseInlines(node: ASTNode): Lines {
        return when (node.type) {
            // ElementTypes
            MarkdownElementTypes.UNORDERED_LIST -> TODO("UNORDERED_LIST")
            MarkdownElementTypes.ORDERED_LIST -> TODO("ORDERED_LIST")
            MarkdownElementTypes.LIST_ITEM -> TODO("LIST_ITEM")
            MarkdownElementTypes.BLOCK_QUOTE -> TODO("BLOCK_QUOTE")
            MarkdownElementTypes.CODE_BLOCK -> TODO("CODE_BLOCK")
            MarkdownElementTypes.CODE_SPAN -> TODO("CODE_SPAN")
            MarkdownElementTypes.HTML_BLOCK -> TODO("HTML_BLOCK")
            MarkdownElementTypes.EMPH -> {
                innerInlines(node).withStyle(theme.markdownEmph)
            }
            MarkdownElementTypes.STRONG -> {
                innerInlines(node, drop = 2).withStyle(theme.markdownStrong)
            }
            MarkdownElementTypes.LINK_DEFINITION -> TODO("LINK_DEFINITION")
            MarkdownElementTypes.LINK_LABEL -> TODO("LINK_LABEL")
            MarkdownElementTypes.LINK_DESTINATION -> TODO("LINK_DESTINATION")
            MarkdownElementTypes.LINK_TITLE -> TODO("LINK_TITLE")
            MarkdownElementTypes.LINK_TEXT -> TODO("LINK_TEXT")
            MarkdownElementTypes.INLINE_LINK -> TODO("INLINE_LINK")
            MarkdownElementTypes.FULL_REFERENCE_LINK -> TODO("FULL_REFERENCE_LINK")
            MarkdownElementTypes.SHORT_REFERENCE_LINK -> TODO("SHORT_REFERENCE_LINK")
            MarkdownElementTypes.IMAGE -> TODO("IMAGE")
            MarkdownElementTypes.AUTOLINK -> TODO("AUTOLINK")
            MarkdownElementTypes.SETEXT_1 -> TODO("SETEXT_1")
            MarkdownElementTypes.SETEXT_2 -> TODO("SETEXT_2")
            MarkdownElementTypes.ATX_1 -> TODO("ATX_1")
            MarkdownElementTypes.ATX_2 -> TODO("ATX_2")
            MarkdownElementTypes.ATX_3 -> TODO("ATX_3")
            MarkdownElementTypes.ATX_4 -> TODO("ATX_4")
            MarkdownElementTypes.ATX_5 -> TODO("ATX_5")
            MarkdownElementTypes.ATX_6 -> TODO("ATX_6")

            // TokenTypes
            MarkdownTokenTypes.CODE_LINE -> TODO("CODE_LINE")
            MarkdownTokenTypes.BLOCK_QUOTE -> TODO("BLOCK_QUOTE")
            MarkdownTokenTypes.HTML_BLOCK_CONTENT -> TODO("HTML_BLOCK_CONTENT")
            MarkdownTokenTypes.SINGLE_QUOTE -> TODO("SINGLE_QUOTE")
            MarkdownTokenTypes.DOUBLE_QUOTE -> TODO("DOUBLE_QUOTE")
            MarkdownTokenTypes.HARD_LINE_BREAK -> TODO("HARD_LINE_BREAK")
            MarkdownTokenTypes.LINK_ID -> TODO("LINK_ID")
            MarkdownTokenTypes.ATX_HEADER -> TODO("ATX_HEADER")
            MarkdownTokenTypes.ATX_CONTENT -> TODO("ATX_CONTENT")
            MarkdownTokenTypes.SETEXT_1 -> TODO("SETEXT_1")
            MarkdownTokenTypes.SETEXT_2 -> TODO("SETEXT_2")
            MarkdownTokenTypes.SETEXT_CONTENT -> TODO("SETEXT_CONTENT")
            MarkdownTokenTypes.ESCAPED_BACKTICKS -> TODO("ESCAPED_BACKTICKS")
            MarkdownTokenTypes.LIST_BULLET -> TODO("LIST_BULLET")
            MarkdownTokenTypes.URL -> TODO("URL")
            MarkdownTokenTypes.HORIZONTAL_RULE -> TODO("HORIZONTAL_RULE")
            MarkdownTokenTypes.LIST_NUMBER -> TODO("LIST_NUMBER")
            MarkdownTokenTypes.FENCE_LANG -> TODO("FENCE_LANG")
            MarkdownTokenTypes.CODE_FENCE_START -> TODO("CODE_FENCE_START")
            MarkdownTokenTypes.CODE_FENCE_END -> TODO("CODE_FENCE_END")
            MarkdownTokenTypes.LINK_TITLE -> TODO("LINK_TITLE")
            MarkdownTokenTypes.AUTOLINK -> TODO("AUTOLINK")
            MarkdownTokenTypes.EMAIL_AUTOLINK -> TODO("EMAIL_AUTOLINK")
            MarkdownTokenTypes.HTML_TAG -> TODO("HTML_TAG")
            MarkdownTokenTypes.BAD_CHARACTER -> TODO("BAD_CHARACTER")
            MarkdownTokenTypes.TEXT,
            MarkdownTokenTypes.LPAREN,
            MarkdownTokenTypes.RPAREN,
            MarkdownTokenTypes.LBRACKET,
            MarkdownTokenTypes.RBRACKET,
            MarkdownTokenTypes.LT,
            MarkdownTokenTypes.GT,
            MarkdownTokenTypes.COLON,
            MarkdownTokenTypes.EXCLAMATION_MARK,
            MarkdownTokenTypes.EMPH,
            MarkdownTokenTypes.BACKTICK,
            MarkdownTokenTypes.CODE_FENCE_CONTENT,
            MarkdownTokenTypes.WHITE_SPACE -> {
                parseText(input.substring(node.startOffset, node.endOffset), TextStyle())
            }
            MarkdownTokenTypes.EOL -> EOL_LINES
            else -> error("Unexpected token when parsing inlines: $node")
        }
    }

    private fun innerInlines(node: ASTNode, drop: Int = 1): Lines {
        return node.children.subList(drop, node.children.size - drop)
                .fold(Lines(emptyList())) { l, r -> l + parseInlines(r) }
    }
}

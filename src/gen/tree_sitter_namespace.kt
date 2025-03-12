package gen

import com.squareup.kotlinpoet.ClassName

class tree_sitter_namespace {
    private val tree_sitter_package = "tree_sitter"

    val Node = ClassName(tree_sitter_package, "Node")
    val Language = ClassName(tree_sitter_package, "Language")

}
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import java.nio.file.Path

class GenerateContext(
    val nodes: NodeTypes,
    val language: String,
    val packageName: String,
) {

    val NodeMemberName = "\$node"

    object tree_sitter_namespace {
        private val tree_sitter_package = "tree_sitter"

        val Node = ClassName(tree_sitter_package, "Node")
        val Language = ClassName(tree_sitter_package, "Language")
        val TreeCursor = ClassName(tree_sitter_package, "TreeCursor")
    }

    object lib_tree_sitter_namespace {
        private val lib_tree_sitter_package = "lib.tree_sitter"

        val TSTreeCursor = ClassName(lib_tree_sitter_package, "TSTreeCursor")
    }

    object lib_namespace {
        val tree_sitter = lib_tree_sitter_namespace
    }

    //    context(GenerateContext)
    class utils_namespace(private val packageName: String) {
        val useCursor = MemberName(packageName, "useCursor")
    }


    val lib = lib_namespace
    val tree_sitter = tree_sitter_namespace
    val utils = utils_namespace(packageName)


    val TSBaseNode = ClassName(packageName, "${language}TSBaseNode")
    val TSLanguage = ClassName(packageName, "TS${language}Language")
    val UnnamedNode = TSBaseNode.nestedClass("Unnamed")

    private val sources = mutableListOf<FileSpec.Builder>()

    private val nodeSources = mutableMapOf<Node, FileSpec.Builder>()


    fun getNodeSource(node: Node): FileSpec.Builder {
        return nodeSources.getOrPut(node) {
            FileSpec.builder(node.type.className)
        }
    }

    fun addSource(source: FileSpec.Builder) {
        sources.add(source)
    }


    fun build(out: Path) {
        sources.forEach { it.build().writeTo(out) }
        nodeSources.values.forEach { it.build().writeTo(out) }
    }

}
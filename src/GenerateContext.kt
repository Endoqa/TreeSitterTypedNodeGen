import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import gen.tree_sitter_namespace
import java.nio.file.Path

class GenerateContext(
    val nodes: NodeTypes,
    val language: String,
    val packageName: String,
) {

    val NodeMemberName = "\$node"

    val tree_sitter = tree_sitter_namespace()

    val TSBaseNode = ClassName(packageName, "${language}TSBaseNode")
    val TSLanguage = ClassName(packageName, "TS${language}Language")

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
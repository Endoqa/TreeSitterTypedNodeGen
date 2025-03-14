import gen.generate
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

fun main(args: Array<String>) {

    val nodeTypes: NodeTypes = Json.decodeFromString(File("node_types_tests/webidl-node-types.json").readText())


    println(nodeTypes)

    val context = GenerateContext(nodeTypes, "IDL", "tree_sitter.idl.node")

    generate(context)


    context.build(Path.of("examples", "webidl-nodes", "src"))
}
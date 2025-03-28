import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import gen.generate
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path

class GenerateCommand : CliktCommand(name = "gen") {
    private val nodeTypesFile by argument(
        help = "Path to the node-types.json file"
    )

    private val languageId by option("-id", help = "Language identifier")
        .required()

    private val shorthand by option("-s", "--shorthand", help = "Language shorthand")
        .required()

    private val outputDir by option("-o", "--output", help = "Output directory")
        .required()

    override fun run() {
        val nodeTypes: NodeTypes = Json.decodeFromString(File(nodeTypesFile).readText())

        val packageName = "tree_sitter.${languageId.lowercase()}.node"

        val context = GenerateContext(nodeTypes, languageId, packageName, shorthand)

        generate(context)
        context.build(Path.of(outputDir))
    }
}

fun main(args: Array<String>) = GenerateCommand().main(args)

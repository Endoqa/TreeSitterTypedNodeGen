package gen

import GenerateContext
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

context(GenerateContext)
fun generateCreateNode(): FileSpec.Builder {
    val file = FileSpec.builder(packageName, "createNode")

    val func = FunSpec.builder("createNode")
        .addParameter("node", tree_sitter.Node)
        .returns(TSBaseNode)


    val cb = CodeBlock.builder()

    cb.beginControlFlow("if (!%N.%N)", "node", "isNamed")
        .addStatement("error(%S)", "Node is unnamed")
        .endControlFlow()

    cb.beginControlFlow("return when (%N.%N)", "node", "symbol")

    nodes
        .filter { it.named }
        .forEach { node ->
            cb.addStatement(
                "%T.Lang.%N -> %T(node)",
                TSLanguage,
                node.type.name,
                node.type.className
            )
        }

    cb.addStatement("else -> error(%S)", "Unknown node type")
    cb.endControlFlow()

    func.addCode(cb.build())
    file.addFunction(func.build())
    return file
}
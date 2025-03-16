package gen

import GenerateContext
import com.squareup.kotlinpoet.*

context(GenerateContext)
fun generateBaseNode(): FileSpec.Builder {
    val file = FileSpec.builder(TSBaseNode)

    val baseType = TypeSpec.interfaceBuilder(TSBaseNode)
        .addModifiers(KModifier.SEALED)
        .addProperty(
            PropertySpec
                .builder(NodeMemberName, tree_sitter.Node)
                .build()
        )

    baseType.addType(
        TypeSpec.classBuilder("Unnamed")
            .inheritBaseNode()
            .addBaseNodeInitializer()
            .build()
    )







    file.addType(baseType.build())
    return file
}
package gen

import GenerateContext
import com.squareup.kotlinpoet.*

context(GenerateContext)
fun generateBaseNode(): FileSpec.Builder {
    val file = FileSpec.builder(TSBaseNode)

    val baseType = TypeSpec.interfaceBuilder(TSBaseNode)
        .addModifiers(KModifier.SEALED)
//        .primaryConstructor(
//            FunSpec.constructorBuilder()
//                .addParameter("node", tree_sitter.Node)
//                .build()
//        )
        .addProperty(
            PropertySpec
                .builder(NodeMemberName, tree_sitter.Node)
                .build()
        )








    file.addType(baseType.build())
    return file
}
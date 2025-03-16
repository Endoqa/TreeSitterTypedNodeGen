package gen

import GenerateContext
import Node
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

    // example
    //     class Unnamed(
    //        override val `$node`: Node
    //    ) : IDLTSBaseNode
    baseType.addType(
        TypeSpec.classBuilder("Unnamed")
            .addSuperinterface(TSBaseNode)
            .addProperty(
                PropertySpec
                    .builder(NodeMemberName, tree_sitter.Node)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer(NodeMemberName)
                    .build()
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(NodeMemberName, tree_sitter.Node)
                    .build()
            )
            .build()
    )







    file.addType(baseType.build())
    return file
}
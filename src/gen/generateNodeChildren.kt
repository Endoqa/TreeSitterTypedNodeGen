package gen

import GenerateContext
import InternalNode
import Node
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

context(GenerateContext)
fun generateNodeChildren(node: Node, children: InternalNode, clazz: TypeSpec.Builder, file: FileSpec.Builder) {

    val childrenClassName = node.type.childrenClassName

    val childrenType = TypeSpec.interfaceBuilder(childrenClassName)
        .addSuperinterface(TSBaseNode)
        .addModifiers(KModifier.SEALED)

    val companion = TypeSpec.companionObjectBuilder()
    companion.addFunction(generateInvokeWrapper(childrenClassName).build())
    childrenType.addType(companion.build())


    file.addType(childrenType.build())

    var childrenPropertyType: TypeName = childrenClassName
    if (children.multiple) {
        childrenPropertyType = ClassName("kotlin.collections", "List").parameterizedBy(childrenPropertyType)
    }
    if (!children.required) {
        childrenPropertyType = childrenPropertyType.copy(nullable = true)
    }

    val cb = CodeBlock.builder()

    if (!children.required) {
        cb.beginControlFlow("if (%N.namedChildCount == 0U)", NodeMemberName)
            .addStatement("return null")
            .endControlFlow()
    }

    if (children.multiple) {
        val mapCB = CodeBlock.builder()
            .beginControlFlow("return %N.namedChildren.map", NodeMemberName)
            .addStatement("%T(it)", childrenClassName)
            .endControlFlow()
            .build()

        cb.add(mapCB)

        val childrenFunction = FunSpec.builder("children")
            .returns(childrenPropertyType)
            .addCode(cb.build())

        clazz.addFunction(childrenFunction.build())
    } else {
        val getCB = CodeBlock.builder()
            .add("return %T(", childrenClassName)
            .add(
                "%N.getChild(0u) ?: error(%S)",
                NodeMemberName,
                "no child found for ${node.type.name}"
            )
            .add(")")
            .build()

        cb.add(getCB)

        val childrenFunction = FunSpec.builder("children")
            .returns(childrenPropertyType)
            .addCode(cb.build())

        clazz.addFunction(childrenFunction.build())

    }

}
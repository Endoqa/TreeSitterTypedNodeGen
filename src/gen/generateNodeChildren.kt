package gen

import GenerateContext
import InternalNode
import Node
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy


context(GenerateContext)
fun generateNodeChildren(node: Node, children: InternalNode, clazz: TypeSpec.Builder, file: FileSpec.Builder) {
    val childrenClassName = node.type.childrenClassName

    file.addType(generateChildrenInterface(childrenClassName))

    val childrenPropertyType = determineChildrenPropertyType(childrenClassName, children)

    val childrenFunction = generateChildrenFunction(childrenPropertyType, children, childrenClassName, node)

    clazz.addFunction(childrenFunction)
}

context(GenerateContext)
private fun generateChildrenInterface(childrenClassName: ClassName): TypeSpec {
    val childrenType = TypeSpec.interfaceBuilder(childrenClassName)
        .inheritBaseNode()
        .addModifiers(KModifier.SEALED)

    val companion = TypeSpec.companionObjectBuilder()
        .addFunction(generateInvokeWrapper(childrenClassName).build())

    return childrenType
        .addType(companion.build())
        .build()
}

private fun determineChildrenPropertyType(childrenClassName: ClassName, children: InternalNode): TypeName {
    val baseType: TypeName = childrenClassName

    return when {
        children.multiple -> ClassName("kotlin.collections", "List").parameterizedBy(baseType)
        !children.required -> baseType.copy(nullable = true)
        else -> baseType
    }
}

context(GenerateContext)
private fun generateChildrenFunction(
    returnType: TypeName,
    children: InternalNode,
    childrenClassName: ClassName,
    node: Node
): FunSpec {
    val codeBlock = if (children.multiple) {
        generateMultipleChildrenCodeBlock(children, childrenClassName)
    } else {
        generateSingleChildCodeBlock(children, childrenClassName, node)
    }

    return FunSpec.builder("children")
        .returns(returnType)
        .addCode(codeBlock)
        .build()
}

context(GenerateContext)
private fun generateMultipleChildrenCodeBlock(children: InternalNode, childrenClassName: ClassName): CodeBlock {
    val builder = CodeBlock.builder()

    if (!children.required) {
        builder.beginControlFlow("if (%N.namedChildCount == 0U)", NodeMemberName)
            .addStatement("return emptyList()")
            .endControlFlow()
    }

    val mapBlock = CodeBlock.builder()
        .beginControlFlow("return %N.namedChildren.map", NodeMemberName)
        .addStatement("%T(it)", childrenClassName)
        .endControlFlow()
        .build()

    return builder.add(mapBlock).build()
}

context(GenerateContext)
private fun generateSingleChildCodeBlock(
    children: InternalNode, 
    childrenClassName: ClassName,
    node: Node
): CodeBlock {
    val builder = CodeBlock.builder()

    if (!children.required) {
        builder.beginControlFlow("if (%N.namedChildCount == 0U)", NodeMemberName)
            .addStatement("return null")
            .endControlFlow()
    }

    val getChildBlock = CodeBlock.builder()
        .add("return %T(", childrenClassName)
        .add(
            "%N.getNamedChild(0u) ?: error(%S)",
            NodeMemberName,
            "no child found for ${node.type.name}"
        )
        .add(")")
        .build()

    return builder.add(getChildBlock).build()
}

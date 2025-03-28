package gen

import GenerateContext
import InternalNode
import Node
import camel
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

context(GenerateContext)
fun generateFields(node: Node, fields: Map<String, InternalNode>, clazz: TypeSpec.Builder, file: FileSpec.Builder) {
    fields.forEach { (fieldName, field) ->
        generateField(node, fieldName, field, clazz, file)
    }
}

context(GenerateContext)
fun generateField(node: Node, fieldName: String, field: InternalNode, clazz: TypeSpec.Builder, file: FileSpec.Builder) {
    val shouldInline = field.types.size == 1
    val fieldClassName = determineFieldClassName(node, fieldName, field, shouldInline)

    if (!shouldInline) {
        createFieldClass(fieldClassName, file)
    }

    val fieldPropertyType = determineFieldPropertyType(fieldClassName, field)

    if (field.multiple) {
        addMultipleFieldProperty(clazz, fieldName, fieldClassName, fieldPropertyType)
    } else {
        addSingleFieldProperty(clazz, fieldName, fieldClassName, fieldPropertyType, field.required)
    }
}

context(GenerateContext)
private fun determineFieldClassName(node: Node, fieldName: String, field: InternalNode, shouldInline: Boolean): ClassName {
    return if (shouldInline) {
        val inlineNode = field.types.first()
        if (inlineNode.named) {
            inlineNode.type.className
        } else {
            UnnamedNode
        }
    } else {
        node.type.fieldClassName(fieldName)
    }
}

context(GenerateContext)
private fun createFieldClass(fieldClassName: ClassName, file: FileSpec.Builder) {
    val fieldClazz = TypeSpec.interfaceBuilder(fieldClassName)
        .inheritBaseNode()
        .addModifiers(KModifier.SEALED)

    val companion = TypeSpec.companionObjectBuilder()
    companion.addFunction(generateInvokeWrapper(fieldClassName).build())
    fieldClazz.addType(companion.build())

    file.addType(fieldClazz.build())
}

context(GenerateContext)
private fun determineFieldPropertyType(fieldClassName: ClassName, field: InternalNode): TypeName {
    var fieldPropertyType: TypeName = fieldClassName
    if (field.multiple) {
        fieldPropertyType = ClassName("kotlin.collections", "List").parameterizedBy(fieldPropertyType)
    } else if (!field.required) {
        fieldPropertyType = fieldPropertyType.copy(nullable = true)
    }
    return fieldPropertyType
}

context(GenerateContext)
private fun addMultipleFieldProperty(
    clazz: TypeSpec.Builder,
    fieldName: String,
    fieldClassName: ClassName,
    fieldPropertyType: TypeName
) {
    val cb = CodeBlock.builder()
        .beginControlFlow("%M { cursor ->", utils.useCursor)
        .addStatement("%N.%N(%S, cursor)", NodeMemberName, "childrenByFieldName", fieldName)
        .addStatement(".asSequence()")
        .addStatement(".map { %T(it) }", fieldClassName)
        .addStatement(".toList()")
        .endControlFlow()
        .build()

    clazz.addProperty(
        PropertySpec.builder(fieldName.camel(), fieldPropertyType)
            .getter(FunSpec.getterBuilder().addCode("return %L", cb).build())
            .build()
    )
}

context(GenerateContext)
private fun addSingleFieldProperty(
    clazz: TypeSpec.Builder,
    fieldName: String,
    fieldClassName: ClassName,
    fieldPropertyType: TypeName,
    isRequired: Boolean
) {
    var cb = CodeBlock.of(
        "%N.%N(%S)",
        "\$node", "getChildByFieldName", fieldName
    )

    if (isRequired) {
        cb = CodeBlock.of(
            "%L ?: error(%S)",
            cb, "required field $fieldName is null"
        )

        cb = CodeBlock.builder()
            .add("%T(%L)", fieldClassName, cb)
            .build()
    } else {
        cb = CodeBlock.builder()
            .add("(%L)?.let { %T(it) }", cb, fieldClassName)
            .build()
    }

    clazz.addProperty(
        PropertySpec.builder(fieldName.camel(), fieldPropertyType)
            .getter(FunSpec.getterBuilder().addCode("return %L", cb).build())
            .build()
    )
}

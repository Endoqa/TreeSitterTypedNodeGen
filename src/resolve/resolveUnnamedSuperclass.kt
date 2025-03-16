package resolve


import GenerateContext
import Node
import com.squareup.kotlinpoet.ClassName

context(GenerateContext)
fun resolveUnnamedSuperclasses(): List<ClassName> {

    val parent = mutableListOf<ClassName>()

    parent.addAll(
        resolveFromSubtypes()
            .map {
                it.type.className
            }
    )

    parent.addAll(
        resolveFromChildren()
            .map {
                it.type.childrenClassName
            }
    )


    parent.addAll(
        resolveFromField()
    )


    return parent
}

context(GenerateContext)
private fun resolveFromSubtypes(): List<Node> {
    return nodes.filter { n -> n.subtypes.any { !it.named } }
}


context(GenerateContext)
private fun resolveFromChildren(): List<Node> {
    return nodes.filter { n -> n.children?.types?.any { !it.named } ?: false }
}

context(GenerateContext)
private fun resolveFromField(): List<ClassName> {
    return nodes.flatMap { n ->
        val possibleFields = n.fields.filter { (fieldName, field) ->
            field.types.any { !it.named }
        }
        possibleFields.mapNotNull { (fieldName, f) ->
            if (f.types.size == 1) {
                null
            } else {
                n.type.fieldClassName(fieldName)
            }
//            n.type.fieldClassName(fieldName)
        }
    }
}
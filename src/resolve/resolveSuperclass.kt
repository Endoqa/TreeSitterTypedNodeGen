package resolve

import GenerateContext
import Node
import com.squareup.kotlinpoet.ClassName

context(GenerateContext)
fun Node.resolveSuperclass(): List<ClassName> {
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


    return parent
}

context(GenerateContext)
private fun Node.resolveFromSubtypes(): List<Node> {
    return nodes.filter { n -> n.subtypes.any { it.type == type } }
}


context(GenerateContext)
private fun Node.resolveFromChildren(): List<Node> {
    return nodes.filter { n -> n.children?.types?.any { it.type == type } ?: false }
}
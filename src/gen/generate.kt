package gen

import GenerateContext
import Node
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import resolve.resolveSuperclass


fun generate(context: GenerateContext) {


    with(context) {
        addSource(generateBaseNode())
        addSource(generateLanguage())
        addSource(generateCreateNode())
        addSource(generateUtils())
        context.nodes
            .filter { it.named }
            .forEach { generateNode(it) }
    }
}


context(GenerateContext)
fun generateNode(node: Node) {
    val source = getNodeSource(node)

    val isSealed = node.subtypes.isNotEmpty()


    val spec: TypeSpec.Builder

    if (isSealed) {
        spec = TypeSpec.interfaceBuilder(node.type.className)
        spec.addModifiers(KModifier.SEALED)
    } else {
        spec = TypeSpec.classBuilder(node.type.className)
    }

    spec.addSuperinterface(TSBaseNode)

    val superInterfaces = node.resolveSuperclass()

    spec.addSuperinterfaces(superInterfaces)

    if (!isSealed) {
        spec
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter(NodeMemberName, tree_sitter.Node)
                    .build()
            )

            .addProperty(
                PropertySpec.builder(NodeMemberName, tree_sitter.Node)
                    .initializer(NodeMemberName)
                    .addModifiers(KModifier.OVERRIDE)
                    .build()
            )
    }




    if (node.children != null) {
        generateNodeChildren(node, node.children, spec, source)
    }


    if (isSealed) {
        val companion = TypeSpec.companionObjectBuilder()



        companion.addFunction(generateInvokeWrapper(node.type.className).build())


        spec.addType(companion.build())
    }

    if (node.fields.isNotEmpty()) {
        require(node.subtypes.isEmpty()) { "Node ${node.type.name} has fields but is also a subtype" }
        generateFields(node, node.fields, spec, source)
    }


    source.addType(spec.build())
}
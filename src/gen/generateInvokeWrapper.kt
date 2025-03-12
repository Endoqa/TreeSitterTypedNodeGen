package gen

import GenerateContext
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier


//operator fun invoke(node: Node): _ValueNode {
//            val n = createNode(node)
//            if (n is _ValueNode) {
//                return n
//            }
//            throw IllegalArgumentException("Node is not a ValueNode")
//        }
context(GenerateContext)
fun generateInvokeWrapper(node: ClassName): FunSpec.Builder {
    val func = FunSpec.builder("invoke")
        .addModifiers(KModifier.OPERATOR)
        .addParameter("node", tree_sitter.Node)
        .returns(node)

    val cb = CodeBlock.builder()
        .addStatement("val n = createNode(node)")
        .beginControlFlow("if (n is %T)", node)
        .addStatement("return n")
        .endControlFlow()
        .addStatement("throw IllegalArgumentException(%S)", "Node is not a ${node.simpleName}")

    func.addCode(cb.build())
    return func
}
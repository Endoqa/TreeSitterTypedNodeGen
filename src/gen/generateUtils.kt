package gen

import GenerateContext
import com.squareup.kotlinpoet.*
import java.lang.foreign.Arena

context(GenerateContext)
fun generateUtils(): FileSpec.Builder {
    val file = FileSpec.builder(packageName, "utils")

    file.addFunction(generateUseCursor().build())


    return file
}

/**
 * internal inline fun <R> useCursor(action: (TreeCursor) -> R): R {
 *     return Arena.ofConfined().use { alloc ->
 *         val cursor = TreeCursor(TSTreeCursor.allocate(alloc).`$mem`)
 *         val r = cursor.let(action)
 *         cursor.drop()
 *         r
 *     }
 * }
 */
context(GenerateContext)
private fun generateUseCursor(): FunSpec.Builder {
    val func = FunSpec.builder("useCursor")
        .addModifiers(KModifier.INLINE, KModifier.INTERNAL)

    val returnType = TypeVariableName("R")

    func
        .addTypeVariable(returnType)
        .addParameter(
            "action",
            LambdaTypeName.get(returnType = returnType, parameters = arrayOf(tree_sitter.TreeCursor))
        )
        .returns(TypeVariableName("R"))

    func.addCode(
        CodeBlock.builder()
            .beginControlFlow("return %T.%N().use { alloc ->", Arena::class, "ofConfined")
            .addStatement(
                "val cursor = %T(%T.allocate(alloc).`\$mem`)",
                tree_sitter.TreeCursor,
                lib.tree_sitter.TSTreeCursor
            )
            .addStatement("val r = cursor.let(action)")
            .addStatement("cursor.drop()")
            .addStatement("r")
            .endControlFlow()
            .build()
    )

    return func
}
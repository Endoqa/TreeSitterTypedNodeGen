package gen

import GenerateContext
import com.squareup.kotlinpoet.*

private const val LanguageMemberName = "lang"

context(GenerateContext)
fun generateLanguage(languageID: String = language.lowercase()): FileSpec.Builder {
    val file = FileSpec.builder(TSLanguage)

    val clazz = TypeSpec.classBuilder(TSLanguage)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter(LanguageMemberName, tree_sitter.Language)
                .build()
        )
        .addProperty(
            PropertySpec.builder(LanguageMemberName, tree_sitter.Language)
                .initializer(LanguageMemberName)
                .build()
        )

    val companion = TypeSpec.companionObjectBuilder()

    companion.addProperty(
        PropertySpec.builder("Lang", TSLanguage)
            .initializer("%T(%T.getLanguage(%S))", TSLanguage, tree_sitter.Language, languageID)
            .build()
    )




    nodes
        .filter { it.named }
        .forEach { node ->
            val property = PropertySpec.builder(node.type.name, UShort::class)
                .initializer(
                    CodeBlock.of(
                        "%N.getSymbolForName(%S, %L)",
                        LanguageMemberName,
                        node.type.name,
                        true
                    )
                )

            clazz.addProperty(property.build())
        }



    clazz.addType(companion.build())
    file.addType(clazz.build())
    return file
}
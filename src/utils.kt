private val String.prefix get() = if (startsWith("_")) "_" else ""

fun String.pascal(): String {
    return prefix + split("_").joinToString("") { it.replaceFirstChar { it.uppercase() } }

}

fun String.camel(): String {
    return prefix + pascal().replaceFirstChar { it.lowercase() }
}
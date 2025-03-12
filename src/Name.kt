import com.squareup.kotlinpoet.ClassName
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = Name.Serializer::class)
@JvmInline
value class Name(val name: String) {


    context(GenerateContext)
    val className
        get() = ClassName(packageName, "${name.pascal()}Node")

    context(GenerateContext)
    val childrenClassName
        get() = ClassName(packageName, "${name.pascal()}NodeChildren")


    override fun toString(): String {
        return name
    }


    object Serializer : KSerializer<Name> {
        override fun serialize(encoder: Encoder, value: Name) {
            encoder.encodeString(value.name)
        }

        override val descriptor: SerialDescriptor = String.serializer().descriptor

        override fun deserialize(decoder: Decoder): Name {
            return Name(decoder.decodeString())
        }
    }


}
import kotlinx.serialization.Serializable


typealias NodeTypes = List<Node>

interface NodeBasicInfo {
    val type: Name
    val named: Boolean
}

@Serializable
data class Subtype(
    override val type: Name,
    override val named: Boolean
) : NodeBasicInfo

@Serializable
data class Node(
    override val type: Name,
    override val named: Boolean,
    val root: Boolean = false,
    val extra: Boolean = false,
    val subtypes: List<Subtype> = emptyList(),
    val fields: Map<String, InternalNode> = emptyMap(),
    val children: InternalNode? = null
) : NodeBasicInfo

@Serializable
data class NodeType(
    override val type: Name,
    override val named: Boolean,
) : NodeBasicInfo


@Serializable
data class InternalNode(
    val multiple: Boolean,
    val required: Boolean,
    val types: List<NodeType>
)
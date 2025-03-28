# TreeSitterTypedNodeGen

A Kotlin code generator that creates type-safe wrappers for Tree-sitter parse trees.

## Overview

TreeSitterTypedNodeGen generates Kotlin classes from Tree-sitter's `node-types.json` files, providing a strongly-typed API for working with Tree-sitter parse trees. This makes it easier to navigate and manipulate parse trees with IDE code completion and type checking.

The generator creates:
- A base node class for the language
- A language class with node type IDs
- Typed classes for each node type in the grammar
- Helper methods for accessing children and fields

## Requirements

- JDK 22 or later
- Kotlin 1.9 or later (with context receivers support enabled)
- Tree-sitter grammar with a `node-types.json` file
- Tree-sitter library for using the generated code

## Dependencies

- [KotlinPoet](https://github.com/square/kotlinpoet) (1.18.1) - For Kotlin code generation
- [Clikt](https://github.com/ajalt/clikt) (5.0.1) - For command-line interface
- Kotlin Serialization - For parsing JSON

## Installation

Clone the repository and build the project using Gradle:

```bash
git clone https://github.com/yourusername/TreeSitterTypedNodeGen.git
cd TreeSitterTypedNodeGen
./gradlew build
```

## Usage

Generate typed node classes for a Tree-sitter grammar:

```bash
java -jar TreeSitterTypedNodeGen.jar gen \
  path/to/node-types.json \
  -id language_name \
  -s language_shorthand \
  -o output/directory
```

### Parameters

- `path/to/node-types.json`: Path to the Tree-sitter node-types.json file
- `-id, --language-id`: Language identifier (e.g., "c", "java")
- `-s, --shorthand`: Language shorthand for class naming
- `-o, --output`: Output directory for generated code

## Example

For a C language grammar:

```bash
java -jar TreeSitterTypedNodeGen.jar gen \
  tree-sitter-c/src/node-types.json \
  -id c \
  -s C \
  -o generated/c-nodes
```

This will generate Kotlin classes in the `generated/c-nodes` directory with a package structure like `tree_sitter.c.node`.

## Generated Code Structure

The generated code includes:

1. A base node interface (`CNodeBase` in the example)
2. A language class with node type IDs (`TSCLanguage`)
3. Classes for each node type (e.g., `AttributeSpecifierNode`)
4. Interfaces for node children (e.g., `AttributeSpecifierNodeChildren`)
5. Utility functions for node creation and traversal

### Example Usage of Generated Code

```kotlin
import tree_sitter.c.node.*

// Parse C code with Tree-sitter
val parser = Parser()
parser.setLanguage(C.language)
val tree = parser.parseString("int main() { return 0; }")
val rootNode = tree.rootNode

// Use the generated typed API
val translationUnit = TranslationUnitNode(rootNode)
val functionDef = translationUnit.children().first() as FunctionDefinitionNode
val returnType = functionDef.type()
val name = functionDef.declarator().name()
val body = functionDef.body()

println("Function: ${name.text}")
println("Return type: ${returnType.text}")
```

## Project Structure

- `src/` - Source code for the generator
  - `main.kt` - Command-line interface
  - `GenerateContext.kt` - Context for code generation
  - `NodeTypes.kt` - Data structures for node types
  - `gen/` - Code generation logic
- `examples/` - Example generated code
  - `c-nodes/` - Generated code for C language
  - `webidl-nodes/` - Generated code for WebIDL language

## Note on Kotlin Features

This project uses Kotlin's context receivers feature, which is currently experimental. This is enabled in the project using the `-Xcontext-receivers` compiler flag as specified in the `module.yaml` file.

## Contributing

Contributions to TreeSitterTypedNodeGen are welcome! Here are some ways you can contribute:

1. Report bugs and request features by creating issues
2. Submit pull requests to fix bugs or add new features
3. Improve documentation
4. Create examples for additional languages

## License

[MIT License](LICENSE)

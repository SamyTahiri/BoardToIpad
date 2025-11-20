package net.raphdf201.boardtoipad

const val robot = "robot.local"

enum class NTType {
    UNASSIGNED,
    BOOLEAN,
    DOUBLE,
    STRING,
    INT,
    FLOAT,
    RAW,
    BOOLEANARR,
    DOUBLEARR,
    STRINGARR,
    INTARR,
    FLOATARR;
}

data class DynamicResponse(
    val type: NTType,
    val value: Any
)

data class StringResponse(
    val value: String
)

data class DoubleResponse(
    val value: Double
)

data class TableDescription(
    val table: String,
    val value: String
)

data class Table(
    val keys: Set<String>,
    val subTables: Set<String>
)

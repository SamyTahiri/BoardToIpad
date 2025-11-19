package net.raphdf201.boardtoipad

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

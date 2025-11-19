package net.raphdf201.boardtoipad

import edu.wpi.first.networktables.NetworkTableType
import io.ktor.http.ContentType

fun NetworkTableType.toKtorType(): NTType {
    return when(this) {
        NetworkTableType.kUnassigned -> NTType.UNASSIGNED
        NetworkTableType.kBoolean -> NTType.BOOLEAN
        NetworkTableType.kDouble -> NTType.DOUBLE
        NetworkTableType.kString -> NTType.STRING
        NetworkTableType.kRaw -> NTType.RAW
        NetworkTableType.kBooleanArray -> NTType.BOOLEANARR
        NetworkTableType.kDoubleArray -> NTType.DOUBLEARR
        NetworkTableType.kStringArray -> NTType.STRINGARR
        NetworkTableType.kInteger -> NTType.INT
        NetworkTableType.kFloat -> NTType.FLOAT
        NetworkTableType.kIntegerArray -> NTType.INTARR
        NetworkTableType.kFloatArray -> NTType.FLOATARR
    }
}
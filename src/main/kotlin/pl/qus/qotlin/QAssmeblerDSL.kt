package pl.qus.qotlin

import pl.qus.qotlin.model.QAsm

interface Element {
    fun render(builder: StringBuilder, indent: String)
}


fun QasmBody.measure(qreg: Int, creg: Int) {
    source+="measure q[$qreg] -> c[$creg];\n"
}

fun QasmBody.measure() {
    source+="measure q -> c;\n"
}

fun QasmBody.barrier() {
    source+="barrier q;\n"
}

fun QasmBody.barrier(qreg : Int) {
    source+="barrier q[$qreg];\n"
}

fun QasmBody.creg(count: Int) {
    source+="creg c[$count];\n"
}

fun QasmBody.qreg(count: Int) {
    source+="qreg q[$count];\n"
}


/**
 * 3-parameter 2-pulse single qubit gate
 */
fun QasmBody.u3(theta : Double, phi : Double, lambda : Double) {
    source+="u3 ($theta,$phi,$lambda);\n"
}

/**
 * 2-parameter 1-pulse single qubit gate
 */
fun QasmBody.u2(phi : Double, lambda : Double) {
    source+="u2 ($phi,$lambda);\n"
}

/**
 *  1-parameter 0-pulse single qubit gate
 */
fun QasmBody.u1(lambda : Double) {
    source+="u1 ($lambda);\n"
}

/**
 * Controlled-NOT
 *
 * May be used to entangle quantum states
 *
 * @param qreg1 source register
 * @param qreg2 destination register
 */
fun QasmBody.cx(qreg1: Int, qreg2: Int) {
    source+="cx q[$qreg1],q[$qreg2];\n"
}

/**
 * NOP
 *
 * Idle gate (identity)
 */
fun QasmBody.id(qreg: Int) {
    source+="id q[$qreg];\n"
}

/**
 * Pauli gate: bit-flip
 */
fun QasmBody.x(qreg: Int) {
    source+="x q[$qreg];\n"
}

/**
 * Pauli gate: bit and phase flip
 */
fun QasmBody.y(qreg: Int) {
    source+="y q[$qreg];\n"
}

/**
 * Pauli gate: phase flip
 */
fun QasmBody.z(qreg: Int) {
    source+="z q[$qreg];\n"
}

/**
 * Clifford gate: hadamard
 *
 * May be used to put qbit in superposition state or measure
 */
fun QasmBody.h(qreg: Int) {
    source+="h q[$qreg];\n"
}

/**
 * Clifford gate: sqrt(Z) phase gate
 */
fun QasmBody.s(qreg: Int) {
    source+="s q[$qreg];\n"
}

/**
 * Clifford gate: conjugate of sqrt(Z)
 */
fun QasmBody.sdg(qreg: Int) {
    source+="sdg q[$qreg];\n"
}

/**
 *  C3 gate: sqrt(S) phase gate
 */
fun QasmBody.t(qreg: Int) {
    source+="t q[$qreg];\n"
}

/**
 * C3 gate: conjugate of sqrt(S)
 */
fun QasmBody.tdg(qreg: Int) {
    source+="tdg q[$qreg];\n"
}

/**
 * Rotation around X-axis
 */
fun QasmBody.rx(qreg: Int) {
    source+="rx q[$qreg];\n"
}

/**
 * rotation around Y-axis
 */
fun QasmBody.ry(qreg: Int) {
    source+="ry q[$qreg];\n"
}

/**
 * rotation around Z axis
 */
fun QasmBody.rz(qreg: Int) {
    source+="rz q[$qreg];\n"
}

/**
 * controlled-Phase
 */
fun QasmBody.cz(qreg1: Int, qreg2: Int) {
    source+="cz q[$qreg1],q[$qreg2];\n"
}

/**
 * controlled-Y
 */
fun QasmBody.cy(qreg1: Int, qreg2: Int) {
    source+="cy q[$qreg1],q[$qreg2];\n"
}

/**
 * controlled-H
 */
fun QasmBody.ch(qreg1: Int, qreg2: Int) {
    source+="ch q[$qreg1],q[$qreg2];\n"
}

/**
 * C3 gate: Toffoli
 */
fun QasmBody.ccx(qreg1: Int, qreg2: Int, qreg3: Int) {
    source+="ccx q[$qreg1],q[$qreg2],q[$qreg3];\n"
}

/**
 * controlled rz rotation
 */
fun QasmBody.crz(lambda: Double, qreg1: Int, qreg2: Int) {
    source+="crz ($lambda) q[$qreg1],q[$qreg2];\n"
}

/**
 * controlled phase rotation
 */
fun QasmBody.cu1(lambda: Double, qreg1: Int, qreg2: Int) {
    source+="cu1 ($lambda) q[$qreg1],q[$qreg2];\n"
}

/**
 * controlled-U
 *
 * implements controlled-U(theta,phi,lambda) with target t and control c
 */
fun QasmBody.cu3(theta : Double, phi : Double, lambda : Double) {
    source+="cu3 ($theta,$phi,$lambda);\n"
}


class TextElement(val text: String) : Element {
    override fun render(builder: StringBuilder, indent: String) {
        builder.append("$indent$text\n")
    }
}

class QasmBody {
    var source : String = "include \"qelib1.inc\";\n"
}


fun qasm(init: QasmBody.() -> Unit): QAsm {
    val html = QasmBody()
    html.init()
    return QAsm(html.source)
}
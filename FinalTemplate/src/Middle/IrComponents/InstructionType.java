package Middle.IrComponents;

public enum InstructionType {
    ADD, SUB, MUL, SDIV, SREM, BITAND,     // 算术运算
    ICMP, AND, OR,          // 逻辑运算
    CALL,
    ALLOCA, LOAD, STORE ,           // 内存操作
    PTR,
    PHI,
    ZEXT,
    TRUNC,
    BR,
    RET,
}

package Backend.MipsSymbolManager;

public class MipsValue {
    /** 若变量存入内存，需要记录相对于base的偏移量 */
    private int baseReg;
    private int offset;
    private final boolean isInReg;
    private int tmpReg;     // 如果变量在寄存器中，则记录临时的寄存器


    public MipsValue(int baseReg, int offset) {
        this.baseReg = baseReg;
        this.offset = offset;
        this.isInReg = false;
    }

    public MipsValue(int tmpReg) {
        this.tmpReg = tmpReg;
        this.isInReg = true;
    }

    public int getBaseReg() {
        return baseReg;
    }

    public int getOffset() {
        return offset;
    }

    public int getTmpReg() {
        return tmpReg;
    }

    public boolean isInReg() {
        return this.isInReg;
    }
}

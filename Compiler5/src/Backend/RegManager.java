package Backend;

import java.util.HashMap;
import java.util.Map;

public class RegManager {
    private final HashMap<Integer, Boolean> regMap;

    public RegManager() {
        regMap = new HashMap<>();
        for (int i = 0; i < 32; i++) {
            regMap.put(i, false);
        }
    }

    public int allocTmpReg() {      // 分配一个空闲的t类reg
        for (int i = 8; i <= 15; i++) {
            if (!isUsed(i)) {
                regMap.put(i, true);
                return i;
            }
        }
        for (int i = 16; i <= 25; i++) {        // 如果t类没有就分配一个s类
            if (!isUsed(i)) {
                regMap.put(i, true);
                if (i >= 22) {
                    System.out.println("WARNING: Using " + MipsBuilder.getReg(i));
                }
                return i;
            }
        }
        System.out.println("ERROR: No free Reg!");
        return -1;      // 无空闲临时寄存器
    }

    public boolean isUsed(int reg) {
        return regMap.get(reg);
    }

    public void setUsed(int reg) {
        regMap.put(reg, true);
    }

    public void unmap(int reg) {
        regMap.put(reg, false);
    }

    public void unmapAll() {
        for (Map.Entry<Integer, Boolean> entry : regMap.entrySet()) {
            if (entry.getValue()) {
                regMap.put(entry.getKey(), false);
            }
        }
    }
}

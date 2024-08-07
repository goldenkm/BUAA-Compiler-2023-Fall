package Frontend.SyntaxComponents;

import java.util.ArrayList;

public class Decl implements BlockItem {
    private boolean isConst;
    private ArrayList<Def> defs;

    public Decl(boolean isConst) {
        this.isConst = isConst;
        this.defs = new ArrayList<>();
    }

    public void addDef(Def def) {
        this.defs.add(def);
    }
}

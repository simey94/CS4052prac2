package lsv.grammar;

/**
 * Created by 120011995 on 21/11/15.
 */
public class AtomicProp extends FormulaElement {

    private String ap;

    public AtomicProp(String ap) {
        this.ap = ap;
    }

    public String getAp() {
        return ap;
    }
}

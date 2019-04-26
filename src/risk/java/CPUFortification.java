package risk.java;

public class CPUFortification {

    public Territory unfortified;
    public Territory fortified;
    public int delta;

    CPUFortification(Territory unfortified, Territory fortified, int delta) {
        this.unfortified = unfortified;
        this.fortified = fortified;
        this.delta = delta;
    }

}

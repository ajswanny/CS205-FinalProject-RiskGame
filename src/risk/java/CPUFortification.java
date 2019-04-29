package risk.java;

public class CPUFortification {

    public final Territory deFortifiedTerritory;
    public final Territory fortifiedTerritory;
    public final int delta;

    CPUFortification(Territory deFortifiedTerritory, Territory fortifiedTerritory, int delta) {
        this.deFortifiedTerritory = deFortifiedTerritory;
        this.fortifiedTerritory = fortifiedTerritory;
        this.delta = delta;
    }

}

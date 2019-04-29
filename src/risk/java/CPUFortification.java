package risk.java;

public class CPUFortification {

    public Territory deFortifiedTerritory;
    public Territory fortifiedTerritory;
    public int delta;

    CPUFortification(Territory deFortifiedTerritory, Territory fortifiedTerritory, int delta) {
        this.deFortifiedTerritory = deFortifiedTerritory;
        this.fortifiedTerritory = fortifiedTerritory;
        this.delta = delta;
    }

}

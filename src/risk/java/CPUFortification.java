package risk.java;

/**
 * Stores data about a CPU Fortify phase: the Territory which was subtracted of armies, the one which was given more
 * armies, and the amount of this change of armies.
 */
public class CPUFortification {

    /* Fields */
    /** The Territory that has lost armies. */
    public final Territory deFortifiedTerritory;

    /** The Territory which was given armies. */
    public final Territory fortifiedTerritory;

    /** The change in armies for both Territories. */
    public final int delta;


    /* Constructor */
    CPUFortification(Territory deFortifiedTerritory, Territory fortifiedTerritory, int delta) {
        this.deFortifiedTerritory = deFortifiedTerritory;
        this.fortifiedTerritory = fortifiedTerritory;
        this.delta = delta;
    }

}

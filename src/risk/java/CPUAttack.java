package risk.java;

/**
 * Stores data about a CPU Attack phase: the Territories in involved in the attack and whether a target Territory was
 * conquered.
 */
public class CPUAttack {

    /* Fields */
    /**
     * Origin Territory of attack.
     */
    public final Territory attackOrigin;

    /**
     * Territory subject to attack.
     */
    public final Territory attackTarget;

    public final boolean targetWasConquered;


    /* Constructor */
    CPUAttack(Territory attackOrigin, Territory attackTarget, boolean targetWasConquered) {
        this.attackOrigin = attackOrigin;
        this.attackTarget = attackTarget;
        this.targetWasConquered = targetWasConquered;
    }

}

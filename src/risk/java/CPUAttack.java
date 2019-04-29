package risk.java;

public class CPUAttack {

    public final Territory attackOrigin;
    public final Territory attackTarget;
    public final boolean targetWasConquered;

    CPUAttack(Territory attackOrigin, Territory attackTarget, boolean targetWasConquered) {
        this.attackOrigin = attackOrigin;
        this.attackTarget = attackTarget;
        this.targetWasConquered = targetWasConquered;
    }
}

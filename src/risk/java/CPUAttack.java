package risk.java;

public class CPUAttack {

    public Territory attackOrigin;
    public Territory attackTarget;
    public boolean targetWasConquered;

    CPUAttack(Territory attackOrigin, Territory attackTarget, boolean targetWasConquered) {
        this.attackOrigin = attackOrigin;
        this.attackTarget = attackTarget;
        this.targetWasConquered = targetWasConquered;
    }
}

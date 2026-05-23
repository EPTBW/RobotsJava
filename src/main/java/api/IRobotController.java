package api;

public interface IRobotController {
    void setTarget(double x, double y);
    void update(double duration, IGameContext context);

    double getX();
    double getY();
    double getDirection();
    double getTargetX();
    double getTargetY();

    void takeDamage();
    void resetHp();
    void heal();
    int getHp();

    double getEnergy();
    double getMaxEnergy();
    void addEnergy(double amount);
    boolean consumeEnergy(double amount);

    void dash();
    double getDashCooldownRemaining();
    double getDashCooldown();

    boolean isShieldActive();
    void toggleShield();
}

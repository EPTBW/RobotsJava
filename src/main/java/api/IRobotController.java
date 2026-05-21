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
    int getHp();
    void dash();
    double getDashCooldownRemaining();
    double getDashCooldown();
}

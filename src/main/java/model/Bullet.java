package model;

public class Bullet {
    private double x;
    private double y;

    private final double velocityX;
    private final double velocityY;

    public Bullet(double startX, double startY, double VelX, double VelY) {
        this.x = startX;
        this.y = startY;
        this.velocityX = VelX;
        this.velocityY = VelY;
    }

    public void update(double duration) {
        this.x += velocityX * duration;
        this.y += velocityY * duration;
    }

    public double getX() {return x;}
    public double getY() {return y;}
}

package model;

import api.IBullet;

public class Bullet implements IBullet {
    private double x;
    private double y;

    private double velocityX;
    private double velocityY;

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

    public void reflect() {
        this.velocityX = -this.velocityX;
        this.velocityY = -this.velocityY;
    }

    public double getX() {return x;}
    public double getY() {return y;}
}

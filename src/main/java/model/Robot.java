package model;

public class Robot {
    private double x;
    private double y;
    private double direction;

    private double targetX;
    private double targetY;

    private static final double MAX_VELOCITY = 0.2;
    private static final double MAX_ANGULAR_VELOCITY = 0.005;

    public Robot(double startX, double startY) {
        this.x = startX;
        this.y = startY;
        this.direction = 0;
        this.targetX = startX;
        this.targetY = startY;
    }

    public void setTarget(double x, double y) {
        this.targetX = x;
        this.targetY = y;
    }

    public void update(double duration, double fieldWidth, double fieldHeight) {
        double distance = distance(targetX, targetY, x ,y);
        if (distance < 0.5)
        {
            return;
        }

        double angleToTarget = angleTo(x, y, targetX, targetY);
        double angleDifference = asNormalizedRadians(angleToTarget - direction);
        double angularVelocity = 0;
        double tolerance = 0.05;

        if (angleDifference < tolerance || angleDifference > 2 * Math.PI - tolerance) {
            angularVelocity = 0;
        } else if (angleDifference < Math.PI) {
            angularVelocity = MAX_ANGULAR_VELOCITY;
        } else {
            angularVelocity = -MAX_ANGULAR_VELOCITY;
        }

        move(MAX_VELOCITY, angularVelocity, duration, fieldWidth, fieldHeight);
    }

    private void move(double velocity, double angularVelocity, double duration, double width, double height) {
        double newX = x + velocity / angularVelocity * (Math.sin(direction + angularVelocity * duration) - Math.sin(direction));
        if (!Double.isFinite(newX)) {
            newX = x + velocity * duration * Math.cos(direction);
        }

        double newY = y - velocity / angularVelocity * (Math.cos(direction + angularVelocity * duration) - Math.cos(direction));
        if (!Double.isFinite(newY)) {
            newY = y + velocity * duration * Math.sin(direction);
        }

        // Ограничиваем выезд за пределы поля
        x = applyLimits(newX, 15, width - 15);
        y = applyLimits(newY, 15, height - 15);
        direction = asNormalizedRadians(direction + angularVelocity * duration);
    }

    private double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    private double angleTo(double fromX, double fromY, double toX, double toY) {
        return asNormalizedRadians(Math.atan2(toY - fromY, toX - fromX));
    }

    private double asNormalizedRadians(double angle) {
        while (angle < 0) angle += 2 * Math.PI;
        while (angle >= 2 * Math.PI) angle -= 2 * Math.PI;
        return angle;
    }

    private double applyLimits(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDirection() { return direction; }
    public double getTargetX() { return targetX; }
    public double getTargetY() { return targetY; }
}


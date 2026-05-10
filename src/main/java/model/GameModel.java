package model;

public class GameModel {
    private final Robot robot;
    private Apple apple;
    private int score;

    private int fieldWidth;
    private int fieldHeight;

    public GameModel(int width, int height) {
        this.fieldHeight = height;
        this.fieldWidth = width;
        this.robot = new Robot(100, 100);
        this.apple = new Apple(width, height);
        this.score = 0;
    }

    public void setFieldSize(int width, int height) {
        this.fieldWidth = width;
        this.fieldHeight = height;
    }

    public void setTarget(double x, double y) {
        robot.setTarget(x, y);
    }

    public void update() {
        robot.update(10, fieldWidth, fieldHeight);

        double distToApple = Math.sqrt(Math.pow(robot.getX() - apple.getX(), 2) + Math.pow(robot.getY() - apple.getY(), 2));

        if (distToApple < 20) {
            score++;
            apple.relocate(fieldWidth, fieldHeight);
        }
    }

    public Robot getRobot() {return robot;}
    public Apple getApple() {return apple;}
    public int getScore() {return score;}
}

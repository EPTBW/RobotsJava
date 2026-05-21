package model;

import api.IBullet;
import api.IGameContext;
import api.IRobotController;
import api.IRobotPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameModel {
    private IRobotPlugin activeRobot;
    private Apple apple;
    private int score;

    private int fieldWidth;
    private int fieldHeight;

    private final List<Bullet> bullets = new ArrayList<>();
    private int timeUntilNextBullet = 0;
    private final Random random = new Random();

    public GameModel(int width, int height) {
        this.fieldHeight = height;
        this.fieldWidth = width;
        this.activeRobot = null;
        this.apple = new Apple(width, height);
        this.score = 0;
    }

    public void setFieldSize(int width, int height) {
        this.fieldWidth = width;
        this.fieldHeight = height;
    }

    public void setTarget(double x, double y) {
        activeRobot.getController().setTarget(x, y);
    }

    public void update() {
        if (activeRobot == null) return;

        IGameContext context = new IGameContext() {
            @Override
            public int getAppleX() {
                return apple.getX();
            }

            @Override
            public int getAppleY() {
                return apple.getY();
            }

            @Override
            public List<IBullet> getBullets() {
                return new ArrayList<>(bullets);
            }

            @Override
            public int getFieldWidth() {
                return fieldWidth;
            }

            @Override
            public int getFieldHeight() {
                return fieldHeight;
            }
        };

        IRobotController robot = activeRobot.getController();
        robot.update(10, context);

        double distToApple = Math.sqrt(Math.pow(robot.getX() - apple.getX(), 2) + Math.pow(robot.getY() - apple.getY(), 2));

        if (distToApple < 20) {
            score++;
            apple.relocate(fieldWidth, fieldHeight);
        }

        timeUntilNextBullet -= 10;
        if (timeUntilNextBullet <= 0 && fieldWidth > 0) {
            spawnBullet();
            timeUntilNextBullet = Math.max(300, 1500 - (score * 50));
        }

        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet b = iterator.next();
            b.update(10);

            if (b.getX() < -50 || b.getX() > fieldWidth + 50 || b.getY() < -50 || b.getY() > fieldWidth + 50) {
                iterator.remove();
                continue;
            }

            double distToRobot = Math.sqrt(Math.pow(robot.getX() - b.getX(), 2) + Math.pow(robot.getY() - b.getY(), 2));
            if (distToRobot < 20) {
                iterator.remove();
                robot.takeDamage();

                if(robot.getHp() <= 0) {
                    score = 0;
                    robot.resetHp();;
                    bullets.clear();
                    break;
                }
            }
        }
    }


    private void spawnBullet() {
        double startX = 0;
        double startY = 0;

        int edge = random.nextInt(4);
        // 0 - вверх, 1 - право, 2 - низ, 3 - лево
        if (edge == 0) {
            startX = random.nextInt(fieldWidth);
            startY = -10;
        } else if (edge == 1) {
            startX = fieldWidth + 10; startY = random.nextInt(fieldHeight);
        } else if (edge == 2) {
            startX = random.nextInt(fieldWidth);
            startY = fieldHeight + 10;
        } else {
            startX = -10;
            startY = random.nextInt(fieldHeight);
        }
        double angleToRobot = Math.atan2(activeRobot.getController().getY() - startY, activeRobot.getController().getX() - startX);
        double bulletSpeed = 0.15 + (score * 0.01);

        double vx = Math.cos(angleToRobot) * bulletSpeed;
        double vy = Math.sin(angleToRobot) * bulletSpeed;

        bullets.add(new Bullet(startX, startY, vx, vy));
    }

    public void triggerDash() {
        activeRobot.getController().dash();
    }


    public void setActiveRobot(IRobotPlugin newPlugin) {
        this.activeRobot = newPlugin;
    }

    public IRobotPlugin getActiveRobot() {return activeRobot;}
    public Apple getApple() {return apple;}
    public int getScore() {return score;}
    public List<Bullet> getBullets() {return  bullets;}
    }


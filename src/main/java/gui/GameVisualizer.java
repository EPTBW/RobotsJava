package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import model.Bullet;
import model.GameModel;
import model.Robot;
import model.Apple;

public class GameVisualizer extends JPanel {
    private final Timer m_timer = new Timer("events generaotor", true);
    private final GameModel model;

    public GameVisualizer() {
        model = new GameModel(400, 400);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventQueue.invokeLater(GameVisualizer.this::repaint);
            }
        }, 0, 50);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                model.update();
            }
        }, 0, 10);

        // поменяли mouseClicked на mousePressed
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                model.setTarget(e.getPoint().x, e.getPoint().y);
            }
        });

        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    model.triggerDash();
                }
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                model.setFieldSize(getWidth(), getHeight());
            }
        });

        setDoubleBuffered(true);
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        Graphics2D graphics2D = (Graphics2D) graphics;

        Robot robot = model.getRobot();
        Apple apple = model.getApple();

        drawRobot(graphics2D, (int) Math.round(robot.getX()), (int) Math.round(robot.getY()), robot.getDirection());
        drawTarget(graphics2D, (int) Math.round(robot.getTargetX()), (int) Math.round(robot.getTargetY()));
        drawApple(graphics2D, apple.getX(), apple.getY());

        graphics2D.setColor(Color.BLACK);
        for (Bullet b : model.getBullets()) {
            fillOval(graphics, (int) Math.round(b.getX()), (int) Math.round(b.getY()), 8, 8);
        }

        graphics2D.setColor(Color.YELLOW);
        graphics2D.drawString("Счет: " + model.getScore(), 10 , 20);

        graphics2D.setColor(Color.RED);
        graphics2D.drawString("Жизни: " + robot.getLives(), 10, 35);

        graphics2D.setColor(Color.CYAN);
        if (robot.getDashCooldownRemaining() > 0) {
            int cooldownSec = (int) Math.ceil(robot.getDashCooldownRemaining() / 1000.0);
            graphics2D.drawString("Рывок: " + cooldownSec + "с", 10 , 50);
        } else {
            graphics2D.drawString("Рывок: ГОТОВ (Пробел)", 10 , 50);
        }
    }

    private void drawRobot(Graphics2D graphics2D, int x, int y, double direction) {
        AffineTransform oldTransform = graphics2D.getTransform();

        graphics2D.rotate(direction, x ,y);

        graphics2D.setColor(Color.MAGENTA);
        fillOval(graphics2D, x, y, 30 ,10);
        graphics2D.setColor(Color.BLACK);
        fillOval(graphics2D, x, y, 30, 10);
        graphics2D.setColor(Color.WHITE);
        fillOval(graphics2D, x + 10, y, 5, 5);
        graphics2D.setColor(Color.BLACK);
        drawOval(graphics2D, x + 10, y, 5, 5);

        graphics2D.setTransform(oldTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void drawApple(Graphics2D g, int x, int y) {
        g.setColor(Color.RED);
        fillOval(g, x, y, 10, 10);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 10, 10);
    }
    
    private static void fillOval(Graphics graphics, int centerX, int centerY, int diam1, int diam2) {
        graphics.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    private static void drawOval(Graphics graphics, int centerX, int centerY, int diam1, int diam2) {
        graphics.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

}
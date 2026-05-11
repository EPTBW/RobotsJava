package gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;
import javax.swing.text.MaskFormatter;

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

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                model.setTarget(e.getPoint().x, e.getPoint().y);
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
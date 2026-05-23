package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

import api.IRobotController;
import api.IRobotPlugin;
import model.GameModel;


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

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    model.getActiveRobot().getController().toggleShield();
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

        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        IRobotPlugin plugin = model.getActiveRobot();

        if (plugin == null) {
            graphics2D.setColor(Color.RED);
            graphics2D.setFont(new Font("Arial", Font.BOLD, 14));
            graphics2D.drawString("Загрузите робота через меню 'Плагины'", 20, 35);

            graphics2D.setColor(Color.BLACK);
            graphics2D.drawString("Счет: " + model.getScore(), 20, 60);
            return;
        }

        for (api.ICollectible item : model.getCollectibles()) {
            switch (item.getType()) {
                case APPLE:
                    drawApple(graphics2D, (int) item.getX(), (int) item.getY());
                    break;
                case BATTERY:
                    drawBattery(graphics2D, (int) item.getX(), (int) item.getY());
                    break;
                case MEDKIT:
                    drawMedkit(graphics2D, (int) item.getX(), (int) item.getY());
                    break;
            }
        }

        for (model.Bullet bullet : model.getBullets()) {
            drawBullet(graphics2D, (int) Math.round(bullet.getX()), (int) Math.round(bullet.getY()));
        }



        IRobotController robot = plugin.getController();

        if (robot.isShieldActive() && robot.getEnergy() > 0) {
            drawShield(graphics2D, (int) Math.round(robot.getX()), (int) Math.round(robot.getY()));
        }

        plugin.getVisualizer().draw(graphics2D, robot);
        drawTarget(graphics2D, (int) Math.round(robot.getTargetX()), (int) Math.round(robot.getTargetY()));

        drawHUD(graphics2D, robot);

    }

    // Батарейка
    private void drawBattery(Graphics2D g, int x, int y) {
        g.setColor(Color.YELLOW);
        g.fillRect(x - 8, y - 12, 16, 24);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x - 4, y - 15, 8, 3);
        // Контур
        g.setColor(Color.BLACK);
        g.drawRect(x - 8, y - 12, 16, 24);
    }

    // Аптечка
    private void drawMedkit(Graphics2D g, int x, int y) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x - 12, y - 10, 24, 20, 6, 6);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x - 12, y - 10, 24, 20, 6, 6);

        // Красный крест
        g.setColor(Color.RED);
        g.fillRect(x - 2, y - 6, 4, 12);
        g.fillRect(x - 6, y - 2, 12, 4);
    }

    // Щит
    private void drawShield(Graphics2D g, int robotX, int robotY) {

        Composite originalComposite = g.getComposite();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));

        int radius = 28;
        g.setColor(new Color(0, 191, 255));
        g.fillOval(robotX - radius, robotY - radius, radius * 2, radius * 2);

        // Контур
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.setStroke(new BasicStroke(2.0f));
        g.drawOval(robotX - radius, robotY - radius, radius * 2, radius * 2);

        g.setStroke(new BasicStroke(1.0f));
        g.setComposite(originalComposite);
    }

    // HUD
    private void drawHUD(Graphics2D g, api.IRobotController robot) {
        g.setFont(new Font("Arial", Font.BOLD, 12));

        g.setColor(Color.BLACK);
        g.drawString("Счет: " + model.getScore(), 20, 25);

        g.setColor(robot.getHp() > 1 ? new Color(0, 128, 0) : Color.RED);
        g.drawString("Здоровье (HP): " + robot.getHp() + " / 3", 20, 45);

        g.setColor(new Color(0, 102, 204));
        g.drawString(String.format("Энергия: %.0f / %.0f", robot.getEnergy(), robot.getMaxEnergy()), 20, 65);

        if (robot.isShieldActive()) {
            g.setColor(new Color(0, 153, 76));
            g.drawString("ЩИТ АКТИВЕН (-0.1 энг/тик)", 20, 85);
        }

        // Рывок
        double dashCd = robot.getDashCooldownRemaining();
        double dashMax = robot.getDashCooldown();


        int baseY = robot.isShieldActive() ? 105 : 85;

        if (dashCd <= 0) {
            g.setColor(new Color(255, 140, 0));
            g.drawString("Рывок (Пробел): ГОТОВ", 20, baseY);
        } else {
            g.setColor(Color.GRAY);
            g.drawString(String.format("Рывок: перезарядка %.1f сек", dashCd / 1000.0), 20, baseY);

            int barWidth = 100;
            int filledWidth = (int) ((1.0 - (dashCd / dashMax)) * barWidth);

            g.drawRect(20, baseY + 5, barWidth, 4); // Контур
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(21, baseY + 6, filledWidth, 3); // Заливка
        }
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }

    private void drawApple(Graphics2D g, int x, int y) {

        g.setColor(new Color(220, 20, 60));
        g.fillOval(x - 7, y - 7, 14, 14);

        g.setColor(Color.BLACK);
        g.drawOval(x - 7, y - 7, 14, 14);

        g.setColor(new Color(139, 69, 19));
        g.drawLine(x, y - 7, x + 2, y - 11);

        g.setColor(new Color(34, 139, 34));
        g.fillOval(x + 1, y - 12, 6, 4);
    }

    private void drawBullet(Graphics2D g, int x, int y) {
        g.setColor(new Color(139, 0, 0));
        g.fillOval(x - 4, y - 4, 8, 8);

        g.setColor(Color.RED);
        g.fillOval(x - 2, y - 2, 4, 4);

        g.setColor(Color.YELLOW);
        g.fillOval(x - 1, y - 1, 2, 2);
    }

    private static void fillOval(Graphics graphics, int centerX, int centerY, int diam1, int diam2) {
        graphics.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }
    private static void drawOval(Graphics graphics, int centerX, int centerY, int diam1, int diam2) {
        graphics.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    public void setActiveRobotPlugin(IRobotPlugin newPlugin) {
        model.setActiveRobot(newPlugin);
    }
}
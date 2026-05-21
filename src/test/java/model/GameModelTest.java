package model;

import api.IBullet;
import api.IGameContext;
import api.IRobotController;
import api.IRobotPlugin;
import api.IRobotVisualizer;
import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;
import java.util.List;

public class GameModelTest {

    // 1. Создаем поддельного робота-заглушку специально для тестов движка
    private static class MockRobotController implements IRobotController {
        int hp = 3;
        @Override public void setTarget(double x, double y) {}
        @Override public void update(double duration, IGameContext context) {}
        @Override public double getX() { return 100; }
        @Override public double getY() { return 100; }
        @Override public double getDirection() { return 0; }
        @Override public double getTargetX() { return 100; }
        @Override public double getTargetY() { return 100; }
        @Override public void takeDamage() { hp--; }
        @Override public void resetHp() { hp = 3; }
        @Override public int getHp() { return hp; }
        @Override public void dash() {}
        @Override public double getDashCooldownRemaining() { return 0; }
        @Override public double getDashCooldown() { return 2000; }
    }

    private static class MockRobotPlugin implements IRobotPlugin {
        IRobotController controller = new MockRobotController();
        @Override public String getName() { return "Mock"; }
        @Override public IRobotController getController() { return controller; }
        @Override public IRobotVisualizer getVisualizer() { return null; }
    }

    @Test
    public void testAppleRelocation() {
        Apple apple = new Apple(400, 400);
        int initialX = apple.getX();
        int initialY = apple.getY();

        apple.relocate(400, 400);

        boolean isMoved = (initialX != apple.getX()) || (initialY != apple.getY());
        Assert.assertTrue("Яблоко должно изменить координаты после relocate", isMoved);
    }

    @Test
    public void testBulletSpawning() throws Exception {
        GameModel model = new GameModel(400, 400);
        // Загружаем фейкового робота, чтобы пуле было в кого целиться
        model.setActiveRobot(new MockRobotPlugin());

        Assert.assertTrue("На старте игры пуль быть не должно", model.getBullets().isEmpty());

        Method spawnMethod = GameModel.class.getDeclaredMethod("spawnBullet");
        spawnMethod.setAccessible(true);
        spawnMethod.invoke(model);

        Assert.assertEquals("После спавна должна появиться ровно 1 пуля", 1, model.getBullets().size());
    }

    @Test
    public void testBulletOutOfBoundsAndNearMiss() throws Exception {
        GameModel model = new GameModel(400, 400);
        model.setActiveRobot(new MockRobotPlugin());

        java.lang.reflect.Field timeField = GameModel.class.getDeclaredField("timeUntilNextBullet");
        timeField.setAccessible(true);
        timeField.set(model, 10000);

        IRobotController robot = model.getActiveRobot().getController();

        // Пуля за границей поля
        model.getBullets().add(new Bullet(500, 100, 1 ,0));
        model.update();
        Assert.assertTrue("Пуля должна быть удалена", model.getBullets().isEmpty());

        // Пуля летит рядом (робот на 100:100, пуля на 121:100 -> дистанция 21)
        model.getBullets().add(new Bullet(121, 100, 0, 0));


        int initialHp = robot.getHp();

        model.update();

        Assert.assertFalse("Пуля, пролетевшая рядом, не должна исчезнуть", model.getBullets().isEmpty());
        Assert.assertEquals("ХП робота не должно измениться", initialHp, robot.getHp());
    }

    @Test
    public void testGameOverResetOnZeroHp() throws Exception {
        GameModel model = new GameModel(400, 400);
        model.setActiveRobot(new MockRobotPlugin());

        java.lang.reflect.Field timeField = GameModel.class.getDeclaredField("timeUntilNextBullet");
        timeField.setAccessible(true);
        timeField.set(model, 10000);

        java.lang.reflect.Field scoreField = GameModel.class.getDeclaredField("score");
        scoreField.setAccessible(true);
        scoreField.set(model, 50);

        IRobotController robot = model.getActiveRobot().getController();
        robot.takeDamage();
        robot.takeDamage();
        Assert.assertEquals("У робота должно быть 1 ХП", 1, robot.getHp());

        // Пуля летит точно в робота
        model.getBullets().add(new Bullet(100, 100, 0, 0));
        model.update();

        Assert.assertEquals("Счет должен сброситься до 0", 0, model.getScore());
        Assert.assertEquals("Жизни должны восстановиться до 3", 3, robot.getHp());
        Assert.assertTrue("Экран должен очиститься от пуль", model.getBullets().isEmpty());
    }

    @Test
    public void testAppleSpawnBounds() {
        Apple apple = new Apple(400, 400);
        for (int i = 0; i < 1000; i++) {
            apple.relocate(400, 400);
            Assert.assertTrue("Яблоко внутри поля по X", apple.getX() >= 20 && apple.getX() <= 380);
            Assert.assertTrue("Яблоко внутри поля по Y", apple.getY() >= 20 && apple.getY() <= 380);
        }
    }
}
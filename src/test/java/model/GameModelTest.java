package model;

import api.IBullet;
import api.ICollectible;
import api.IGameContext;
import api.IRobotController;
import api.IRobotPlugin;
import api.IRobotVisualizer;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.List;

public class GameModelTest {

    // 1. Обновленный робот-заглушка с поддержкой новых контрактов
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
        @Override public void heal() { if (hp < 3) hp++; }
        @Override public void resetHp() { hp = 3; }
        @Override public int getHp() { return hp; }

        @Override public void dash() {}
        @Override public double getDashCooldownRemaining() { return 0; }
        @Override public double getDashCooldown() { return 2000; }

        @Override public double getEnergy() { return 0; }
        @Override public double getMaxEnergy() { return 100; }
        @Override public void addEnergy(double amount) {}
        @Override public boolean consumeEnergy(double amount) { return false; }

        @Override public boolean isShieldActive() { return false; }
        @Override public void toggleShield() {}
    }

    private static class MockRobotPlugin implements IRobotPlugin {
        IRobotController controller = new MockRobotController();
        @Override public String getName() { return "Mock"; }
        @Override public IRobotController getController() { return controller; }
        @Override public IRobotVisualizer getVisualizer() { return null; }
    }

    @Test
    public void testCollectibleRelocationA() {

        CollectibleItem item = new CollectibleItem(ICollectible.Type.APPLE, 400, 400);
        double initialX = item.getX();
        double initialY = item.getY();

        item.relocate(400, 400);

        boolean isMoved = (initialX != item.getX()) || (initialY != item.getY());
        Assert.assertTrue("Предмет должен изменить координаты после relocate", isMoved);
    }

    @Test
    public void testCollectibleRelocationB() {

        CollectibleItem item = new CollectibleItem(ICollectible.Type.BATTERY, 400, 400);
        double initialX = item.getX();
        double initialY = item.getY();

        item.relocate(400, 400);

        boolean isMoved = (initialX != item.getX()) || (initialY != item.getY());
        Assert.assertTrue("Предмет должен изменить координаты после relocate", isMoved);
    }

    @Test
    public void testCollectibleRelocationM() {

        CollectibleItem item = new CollectibleItem(ICollectible.Type.MEDKIT, 400, 400);
        double initialX = item.getX();
        double initialY = item.getY();

        item.relocate(400, 400);

        boolean isMoved = (initialX != item.getX()) || (initialY != item.getY());
        Assert.assertTrue("Предмет должен изменить координаты после relocate", isMoved);
    }

    @Test
    public void testBulletSpawning() throws Exception {
        GameModel model = new GameModel(400, 400);
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

        model.getBullets().add(new Bullet(500, 100, 1 ,0));
        model.update();
        Assert.assertTrue("Пуля должна быть удалена", model.getBullets().isEmpty());

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

        model.getBullets().add(new Bullet(100, 100, 0, 0));
        model.update();

        Assert.assertEquals("Счет должен сброситься до 0", 0, model.getScore());
        Assert.assertEquals("Жизни должны восстановиться до 3", 3, robot.getHp());
        Assert.assertTrue("Экран должен очиститься от пуль", model.getBullets().isEmpty());
    }

    @Test
    public void testCollectibleSpawnBoundsB() {
        CollectibleItem item = new CollectibleItem(ICollectible.Type.BATTERY, 400, 400);
        for (int i = 0; i < 1000; i++) {
            item.relocate(400, 400);
            Assert.assertTrue("Предмет внутри поля по X", item.getX() >= 20 && item.getX() <= 380);
            Assert.assertTrue("Предмет внутри поля по Y", item.getY() >= 20 && item.getY() <= 380);
        }
    }

    @Test
    public void testCollectibleSpawnBoundsA() {
        CollectibleItem item = new CollectibleItem(ICollectible.Type.APPLE, 400, 400);
        for (int i = 0; i < 1000; i++) {
            item.relocate(400, 400);
            Assert.assertTrue("Предмет внутри поля по X", item.getX() >= 20 && item.getX() <= 380);
            Assert.assertTrue("Предмет внутри поля по Y", item.getY() >= 20 && item.getY() <= 380);
        }
    }

    @Test
    public void testCollectibleSpawnBoundsM() {
        CollectibleItem item = new CollectibleItem(ICollectible.Type.MEDKIT, 400, 400);
        for (int i = 0; i < 1000; i++) {
            item.relocate(400, 400);
            Assert.assertTrue("Предмет внутри поля по X", item.getX() >= 20 && item.getX() <= 380);
            Assert.assertTrue("Предмет внутри поля по Y", item.getY() >= 20 && item.getY() <= 380);
        }
    }

    @Test
    public void testContextPreventsBulletTampering() throws Exception {
        GameModel model = new GameModel(400, 400);

        java.lang.reflect.Field timeField = GameModel.class.getDeclaredField("timeUntilNextBullet");
        timeField.setAccessible(true);
        timeField.set(model, 10000);

        model.getBullets().add(new Bullet(10, 10, 0, 0));

        IRobotController cheaterController = new MockRobotController() {
            @Override
            public void update(double duration, IGameContext context) {
                try {
                    context.getBullets().clear();
                } catch (Exception e) {

                }
            }
        };

        model.setActiveRobot(new IRobotPlugin() {
            @Override public String getName() { return "Cheater"; }
            @Override public IRobotController getController() { return cheaterController; }
            @Override public IRobotVisualizer getVisualizer() { return null; }
        });

        model.update();

        Assert.assertEquals("Пуля не должна быть удаленна",
                1, model.getBullets().size());
    }

    @Test
    public void testContextProvidesAccurateData() {
        GameModel model = new GameModel(800, 600);

        final IGameContext[] capturedContext = new IGameContext[1];

        IRobotController Controller = new MockRobotController() {
            @Override
            public void update(double duration, IGameContext context) {
                capturedContext[0] = context;
            }
        };

        model.setActiveRobot(new IRobotPlugin() {
            @Override public String getName() { return "robot"; }
            @Override public IRobotController getController() { return Controller; }
            @Override public IRobotVisualizer getVisualizer() { return null; }
        });

        model.update();

        IGameContext ctx = capturedContext[0];
        Assert.assertNotNull("Контекст должен быть передан роботу", ctx);

        Assert.assertEquals("Контекст должен передавать точную ширину", 800, ctx.getFieldWidth());
        Assert.assertEquals("Контекст должен передавать точную высоту", 600, ctx.getFieldHeight());

        Assert.assertEquals("Контекст должен содержать то же количество предметов",
                model.getCollectibles().size(), ctx.getCollectibles().size());

        Assert.assertEquals("Контекст должен точно передавать X первого предмета",
                model.getCollectibles().get(0).getX(), ctx.getCollectibles().get(0).getX(), 0.01);
    }
}
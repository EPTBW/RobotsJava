package model;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;

public class GameModelTest {

    @Test
    public void testAppleRelocation() {
        Apple apple = new Apple(400, 400);
        int initialX = apple.getX();
        int initialY = apple.getY();

        apple.relocate(400, 400);

        boolean isMoved = (initialX != apple.getX()) || (initialY != apple.getY());
        Assert.assertTrue("Яблоко должно изменить координаты после relocate", isMoved);

        Assert.assertTrue("Яблоко должно быть внутри поля по X", apple.getX() >= 20 && apple.getX() <= 380);
        Assert.assertTrue("Яблоко должно быть внутри поля по Y", apple.getY() >= 20 && apple.getY() <= 380);
    }

    @Test
    public void testBulletSpawning() throws Exception {
        GameModel model = new GameModel(400, 400);

        Assert.assertTrue("На старте игры пуль быть не должно", model.getBullets().isEmpty());

        Method spawnMethod = GameModel.class.getDeclaredMethod("spawnBullet");
        spawnMethod.setAccessible(true);

        spawnMethod.invoke(model);

        Assert.assertEquals("После спавна должна появиться ровно 1 пуля", 1, model.getBullets().size());
    }
    @Test
    public void testBulletOutOfBoundsAndNearMiss() throws Exception{
        GameModel model = new GameModel(400, 400);

        java.lang.reflect.Field timeField = GameModel.class.getDeclaredField("timeUntilNextBullet");
        timeField.setAccessible(true);
        timeField.set(model, 10000);

        Robot robot = model.getRobot();

        // за границей
        model.getBullets().add(new Bullet(500, 100, 1 ,0));
        model.update();
        Assert.assertTrue("пуля должна быть удалена", model.getBullets().isEmpty());

        // рядом
        model.getBullets().add(new Bullet(121, 100, 0, 0));
        int initialHp = (int) robot.getLives();

        model.update();

        Assert.assertFalse("Пуля, пролетевшая рядом, не должна исчезнуть", model.getBullets().isEmpty());
        Assert.assertEquals("ХП робота не должно измениться", initialHp, robot.getLives(), 0.001);
    }

    @Test
    public void testGameOverResetOnZeroHp() throws Exception{
        GameModel model = new GameModel(400, 400);

        java.lang.reflect.Field timeField = GameModel.class.getDeclaredField("timeUntilNextBullet");
        timeField.setAccessible(true);
        timeField.set(model, 10000);

        Robot robot = model.getRobot();

        java.lang.reflect.Field scoreField = GameModel.class.getDeclaredField("score");
        scoreField.setAccessible(true);
        scoreField.set(model, 50);

        robot.takeDamage();
        robot.takeDamage();
        Assert.assertEquals("у робота должно быть 1 ХП", 1, robot.getLives(), 0.001);

        model.getBullets().add(new Bullet(100, 100, 0, 0));

        model.update();

        Assert.assertEquals("Счет должен сброситься до 0", 0, model.getScore());
        Assert.assertEquals("Жизни должны восстановиться до 3", 3, robot.getLives(), 0.001);
        Assert.assertTrue("Экран должен очиститься от пуль", model.getBullets().isEmpty());
    }

    @Test
    public void testAppleSpawnBounds() {
        Apple apple = new Apple(400, 400);

        for (int i = 0; i < 1000; i++) {
            apple.relocate(400, 400);
            int x = apple.getX();
            int y = apple.getY();

            Assert.assertTrue("Яблоко вылезло за левый край: x=" + x, x >= 20);
            Assert.assertTrue("Яблоко вылезло за правый край: x=" + x, x <= 380);
            Assert.assertTrue("Яблоко вылезло за верхний край: y=" + y, y >= 20);
            Assert.assertTrue("Яблоко вылезло за нижний край: y=" + y, y <= 380);
        }
    }
}
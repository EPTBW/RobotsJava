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
}
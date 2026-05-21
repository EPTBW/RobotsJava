package model;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Method;

public class RobotTest {
    @Test
    public void testTakeDamage() {
        Robot robot = new Robot(100, 100);
        int initialLives = (int) robot.getLives();

        robot.takeDamage();
        Assert.assertEquals("после получения урона ХП уменьшается на 1", initialLives - 1, robot.getLives(), 0.001);

        robot.resetLives();
        Assert.assertEquals("после сброса ХП должно быть равно максимуму", initialLives, robot.getLives(), 0.001);

    }

    @Test
    public void testDashMechanic() {
        Robot robot = new Robot(100, 100);

        Assert.assertEquals("Изначально откат рывка равен 0", 0.0, robot.getDashCooldownRemaining(), 0.001);

        robot.dash();

        Assert.assertTrue("После рывка должен включиться кулдаун", robot.getDashCooldownRemaining() > 0);
        Assert.assertEquals("Кулдаун должен быть равен константе DASH_COOLDOWN", robot.getDashCooldown(), robot.getDashCooldownRemaining(), 0.001);
    }

    @Test
    public void testAngleNormalization() throws Exception {
        Robot robot = new Robot(0, 0);

        Method method = Robot.class.getDeclaredMethod("asNormalizedRadians", double.class);
        method.setAccessible(true);

        double negativeAngle = -Math.PI / 2;
        double expectedPositive = 3 * Math.PI / 2;
        double result1 = (double) method.invoke(robot, negativeAngle);
        Assert.assertEquals("Отрицательный угол должен правильно нормализоваться", expectedPositive, result1, 0.001);

        double overflowAngle = 3 * Math.PI;
        double expectedNormal = Math.PI;
        double result2 = (double) method.invoke(robot, overflowAngle);
        Assert.assertEquals("Угол больше 360 градусов должен правильно обрезаться", expectedNormal, result2, 0.001);
    }

    private double invokeApplyLimits(Robot robot, double value, double min, double max) throws Exception {
        Method method = Robot.class.getDeclaredMethod("applyLimits", double.class, double.class, double.class);
        method.setAccessible(true);
        return (double) method.invoke(robot, value, min, max);
    }

    @Test
    public void testApplyLimits_InsideBounds() throws Exception {
        Robot robot = new Robot(0, 0);
        double result = invokeApplyLimits(robot, 50, 0.0, 100.0);
        Assert.assertEquals("значение должно остаться 50", 50.0, result, 0.001);
    }

    @Test
    public void testApplyLimits_BelowMin() throws Exception {
        Robot robot = new Robot(0, 0);
        double result = invokeApplyLimits(robot, -50, 0.0, 100.0);
        Assert.assertEquals("значение должно стать минимумом: 0.0", 0.0, result, 0.001);
    }

    @Test
    public void testApplyLimits_AboveMax() throws Exception {
        Robot robot = new Robot(0, 0);
        double result = invokeApplyLimits(robot, 150, 0.0, 100.0);
        Assert.assertEquals("значение должно остаться 50", 100.0, result, 0.001);
    }

    @Test
    public void testDashSpeedAndCooldownLifecycle() {
        Robot robot = new Robot(100, 100);
        robot.setTarget(200, 100);

        robot.update(10, 400, 400);
        double normalDistance = robot.getX() - 100;

        robot.dash();
        double xBeforeDash = robot.getX();
        robot.update(10, 400, 400);
        double dashDistance = robot.getX() - xBeforeDash;

        Assert.assertTrue("Расстояние за тик при рывке должно быть больше обычного", dashDistance > normalDistance);
        Assert.assertTrue("Рывок должен уйти в кулдаун", robot.getDashCooldownRemaining() > 0);

        robot.update(150, 400, 400);

        double xAfterDashDuration = robot.getX();
        robot.update(10, 400, 400);
        double postDashDistance = robot.getX() - xAfterDashDuration;

        Assert.assertEquals("Скорость должна вернуться к нормальной после окончания рывка",
                normalDistance, postDashDistance, 0.001);
    }
}

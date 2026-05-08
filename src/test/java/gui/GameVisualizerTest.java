package gui;

import org.junit.Assert;
import org.junit.Test;

public class GameVisualizerTest {

    @Test
    public void testApplyLimits_InsideBounds() {
        // Проверяем, что значение внутри границ (50) не изменяется
        double result = GameVisualizer.applyLimits(50.0, 0.0, 100.0);
        Assert.assertEquals("Значение внутри границ должно остаться 50.0", 50.0, result, 0.001);
    }

    @Test
    public void testApplyLimits_BelowMin() {
        // Проверяем, что значение меньше минимума (-10) приравнивается к нижней границе (0)
        double result = GameVisualizer.applyLimits(-10.0, 0.0, 100.0);
        Assert.assertEquals("Значение должно быть обрезано по нижней границе", 0.0, result, 0.001);
    }

    @Test
    public void testApplyLimits_AboveMax() {
        // Проверяем, что значение больше максимума (150) приравнивается к верхней границе (100)
        double result = GameVisualizer.applyLimits(150.0, 0.0, 100.0);
        Assert.assertEquals("Значение должно быть обрезано по верхней границе", 100.0, result, 0.001);
    }
}
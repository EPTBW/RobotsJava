package log;

import org.junit.Assert;
import org.junit.Test;
import java.lang.reflect.Field;
import java.util.List;

public class LogWindowSourceTest {

    @Test // Тест на ограничение длины очереди
    public void testQueueLengthLimit() {
        LogWindowSource source = new LogWindowSource(3);

        source.append(LogLevel.Debug, "MSG 1");
        source.append(LogLevel.Debug, "MSG 2");
        source.append(LogLevel.Debug, "MSG 3");
        source.append(LogLevel.Debug, "MSG 4");
        source.append(LogLevel.Debug, "MSG 5");

        Assert.assertEquals("Размер очереди должен строго равняться лимиту", 3, source.size());

        LogEntry firstEntry = source.all().iterator().next();
        Assert.assertEquals("Самым старым сообщением должно остаться MSG 3", "MSG 3", firstEntry.getMessage());
    }

    @Test
    public void testWeakReferenceMemoryLeak() throws Exception {
        LogWindowSource source = new LogWindowSource(10);

        // Создаем тестового слушателя
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {}
        };
        source.registerListener(listener);

        listener = null;

        System.gc();
        Thread.sleep(100);

        // Отправляем новое сообщение.
        source.append(LogLevel.Info, "Trigger update");

        Field listenersField = LogWindowSource.class.getDeclaredField("m_listeners");
        listenersField.setAccessible(true);
        List<?> internalList = (List<?>) listenersField.get(source);

        // Проверяем, что список пуст
        Assert.assertTrue("Список слушателей должен очиститься от сборщика мусора", internalList.isEmpty());
    }
}
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

        // 1. Создаем тестового слушателя
        LogChangeListener listener = new LogChangeListener() {
            @Override
            public void onLogChanged() {}
        };
        source.registerListener(listener);

        // убираем ссылку
        listener = null;

        // Принудительно вызываем Сборщик мусора (Garbage Collector)
        System.gc();
        Thread.sleep(100); // Даем GC время на очистку

        // Отправляем новое сообщение. В этот момент LogWindowSource
        // должен попытаться уведомить слушателей, обнаружить "мертвую" ссылку и удалить её.
        source.append(LogLevel.Info, "Trigger update");

        // Через Reflection API заглядываем в скрытые приватные поля класса
        Field listenersField = LogWindowSource.class.getDeclaredField("m_listeners");
        listenersField.setAccessible(true);
        List<?> internalList = (List<?>) listenersField.get(source);

        // Проверяем, что список пуст
        Assert.assertTrue("Список слушателей должен очиститься от сборщика мусора", internalList.isEmpty());
    }
}
package log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Что починить:
 * 1. Этот класс порождает утечку ресурсов (связанные слушатели оказываются
 * удерживаемыми в памяти)
 * 2. Этот класс хранит активные сообщения лога, но в такой реализации он 
 * их лишь накапливает. Надо же, чтобы количество сообщений в логе было ограничено 
 * величиной m_iQueueLength (т.е. реально нужна очередь сообщений 
 * ограниченного размера) 
 */
public class LogWindowSource
{
    private int m_iQueueLength;
    private final LinkedList<LogEntry> m_messages;
    private final List<WeakReference<LogChangeListener>> m_listeners;
    
    public LogWindowSource(int iQueueLength) 
    {
        m_iQueueLength = iQueueLength;
        m_messages = new LinkedList<>();
        m_listeners = new ArrayList<>();
    }
    
    public void registerListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.add(new WeakReference<>(listener));
        }
    }
    
    public void unregisterListener(LogChangeListener listener)
    {
        synchronized(m_listeners)
        {
            m_listeners.removeIf(ref -> ref.get() == listener || ref.get() == null);
        }
    }
    
    public void append(LogLevel logLevel, String strMessage)
    {
        LogEntry entry = new LogEntry(logLevel, strMessage);

        synchronized (m_messages)
        {
            if (m_messages.size() >= m_iQueueLength)
            {
                m_messages.removeFirst();
            }
            m_messages.add(entry);
        }
        notifyListeners();
    }

    private void notifyListeners()
    {
        List<LogChangeListener> activeListeners = new ArrayList<>();
        synchronized (m_listeners)
        {
            m_listeners.removeIf(ref -> ref.get() == null);
            for (WeakReference<LogChangeListener> ref : m_listeners)
            {
                LogChangeListener listener = ref.get();
                if (listener != null)
                {
                    activeListeners.add(listener);
                }
            }
        }
        for (LogChangeListener listener : activeListeners)
        {
            listener.onLogChanged();
        }
    }

    public int size()
    {
        synchronized (m_messages) {
            return m_messages.size();
        }
    }

    public Iterable<LogEntry> range(int startFrom, int count)
    {
        synchronized (m_messages) {
            if (startFrom < 0 || startFrom >= m_messages.size()) {
                return Collections.emptyList();
            }
            int indexTo = Math.min(startFrom + count, m_messages.size());
            return new ArrayList<>(m_messages.subList(startFrom, indexTo));
        }
    }

    public Iterable<LogEntry> all()
    {
        synchronized (m_messages) {
            return m_messages;
        }
    }
}

package gui;

import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.beans.PropertyVetoException;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import log.Logger;

/**
 * Что требуется сделать:
 * 1. Метод создания меню перегружен функционалом и трудно читается. 
 * Следует разделить его на серию более простых методов (или вообще выделить отдельный класс).
 *
 */
public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private LogWindow logWindow;
    private GameWindow gameWindow;
    private final File profileFile = new File(System.getProperty("user.home"), "robots_profile.conf");

    public MainApplicationFrame() {
        //Make the big window be indented 50 pixels from each edge
        //of the screen.
        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);

        this.logWindow = createLogWindow();
        addWindow(logWindow);

        this.gameWindow = new GameWindow();
        gameWindow.setSize(400, 400);
        addWindow(gameWindow);

        if (profileFile.exists()) {
            int n = JOptionPane.showConfirmDialog(this,
                    "Обнаружен сохраненный профиль. Восстановить?",
                    "Восстановление", JOptionPane.YES_NO_OPTION);
            if (n == JOptionPane.YES_OPTION) {
                loadProfile();
            }
        }

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void confirmExit() {
        Object[] options = {"Да", "Нет"};
        int n = JOptionPane.showOptionDialog(this,
                "Вы действительно хотите выйти?",
                "Подтверждение выхода",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (n == JOptionPane.YES_NO_OPTION) {
            saveProfile();
            System.exit(0);
        }
    }

    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

private JMenu createApplicationMenu() {
    JMenu menu = new JMenu("Приложение");
    menu.setMnemonic(KeyEvent.VK_A);

    {
        JMenuItem exitItem = new JMenuItem("Выход", KeyEvent.VK_X);
        exitItem.addActionListener((event) -> {
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(    // Пункт Выйти имитирует крестик
                    new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        menu.add(exitItem);
    }

    return menu;
}

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createApplicationMenu());
        menuBar.add(createLookAndFeelMenu());
        menuBar.add(createTestMenu());
        return menuBar;
    }

    private JMenu createLookAndFeelMenu() {

        JMenu lookAndFeelMenu = new JMenu("Режим отображения");
        lookAndFeelMenu.setMnemonic(KeyEvent.VK_V);
        lookAndFeelMenu.getAccessibleContext().setAccessibleDescription(
                "Управление режимом отображения приложения");

        JMenuItem systemLookAndFeel = new JMenuItem("Системная схема", KeyEvent.VK_S);
        systemLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLookAndFeel);

        JMenuItem crossplatformLookAndFeel = new JMenuItem("Универсальная схема", KeyEvent.VK_S);
        crossplatformLookAndFeel.addActionListener((event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(crossplatformLookAndFeel);

        return lookAndFeelMenu;
    }

    private JMenu createTestMenu() {
        JMenu testMenu = new JMenu("Тесты");
        testMenu.setMnemonic(KeyEvent.VK_T);
        testMenu.getAccessibleContext().setAccessibleDescription("Тестовые команды");

        JMenuItem addLogMessageItem = new JMenuItem("Сообщение в лог", KeyEvent.VK_S);
        addLogMessageItem.addActionListener((event) -> {
            Logger.debug("Новая строка");
        });
        testMenu.add(addLogMessageItem);

        return testMenu;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        }
        catch (ClassNotFoundException | InstantiationException
            | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private void saveProfile() {
        Properties props = new Properties();

        // Состояние главного окна
        props.setProperty("main.x", String.valueOf(getX()));
        props.setProperty("main.y", String.valueOf(getY()));
        props.setProperty("main.w", String.valueOf(getWidth()));
        props.setProperty("main.h", String.valueOf(getHeight()));
        props.setProperty("main.state", String.valueOf(getExtendedState()));

        // Состояние внутреннего окна лога
        saveInternalFrameState(props, "log", logWindow);
        // Состояние игрового окна
        saveInternalFrameState(props, "game", gameWindow);

        try (OutputStream out = new FileOutputStream(profileFile)) {
            props.store(out, "Robots Application Profile");
        } catch (IOException e) {
            Logger.error("Ошибка при сохранении профиля: " + e.getMessage());
        }
    }

    private void saveInternalFrameState(Properties props, String prefix, JInternalFrame frame) {
        if (frame != null) {
            props.setProperty(prefix + ".visible", String.valueOf(frame.isVisible()));
            props.setProperty(prefix + ".icon", String.valueOf(frame.isIcon()));
            props.setProperty(prefix + ".x", String.valueOf(frame.getX()));
            props.setProperty(prefix + ".y", String.valueOf(frame.getY()));
            props.setProperty(prefix + ".w", String.valueOf(frame.getWidth()));
            props.setProperty(prefix + ".h", String.valueOf(frame.getHeight()));
        }
    }

    private void loadProfile() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(profileFile)) {
            props.load(in);

            // Восстановление главного окна
            setBounds(
                    Integer.parseInt(props.getProperty("main.x")),
                    Integer.parseInt(props.getProperty("main.y")),
                    Integer.parseInt(props.getProperty("main.w")),
                    Integer.parseInt(props.getProperty("main.h"))
            );
            setExtendedState(Integer.parseInt(props.getProperty("main.state")));

            // Восстановление внутренних окон
            restoreInternalFrameState(props, "log", logWindow);
            restoreInternalFrameState(props, "game", gameWindow);

        } catch (IOException | PropertyVetoException e) {
            Logger.error("Ошибка при загрузке профиля: " + e.getMessage());
        }
    }

    private void restoreInternalFrameState(Properties props, String prefix, JInternalFrame frame) throws PropertyVetoException {
        if (frame != null && props.containsKey(prefix + ".x")) {
            frame.setBounds(
                    Integer.parseInt(props.getProperty(prefix + ".x")),
                    Integer.parseInt(props.getProperty(prefix + ".y")),
                    Integer.parseInt(props.getProperty(prefix + ".w")),
                    Integer.parseInt(props.getProperty(prefix + ".h"))
            );
            frame.setVisible(Boolean.parseBoolean(props.getProperty(prefix + ".visible")));
            frame.setIcon(Boolean.parseBoolean(props.getProperty(prefix + ".icon")));
        }
    }
}

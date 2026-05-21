package gui;

import java.awt.*;
import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;
import java.beans.PropertyVetoException;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;

import javax.swing.*;

import api.IRobotPlugin;
import log.Logger;
import utils.PluginLoader;

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
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(
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
        menuBar.add(createPluginMenu());
        return menuBar;
    }

    private JMenu createPluginMenu() {
        JMenu pluginMenu = new JMenu("Плагины");
        JMenuItem loadPluginItem = new JMenuItem("Загрузить робота");

        loadPluginItem.addActionListener((event) ->{

            String projectRobot = System.getProperty("user.dir");
            File pluginsDir = new File(projectRobot, "plugins");

            if (!pluginsDir.exists()) {
                pluginsDir.mkdirs();
            }

            JFileChooser fileChooser = new JFileChooser(pluginsDir);
            fileChooser.setDialogTitle("Выберите робота");

            fileChooser.setFileFilter(new FileNameExtensionFilter("JAR files", "jar"));
            int userSelection = fileChooser.showOpenDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File jarFile = fileChooser.getSelectedFile();
                try {
                    IRobotPlugin newPlugin = PluginLoader.loadPlugin(jarFile);
                    gameWindow.setPlugin(newPlugin);

                    JOptionPane.showMessageDialog(this,
                            "Плагин успешно загружен: " + newPlugin.getName(),
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Ошибка загрузки плагина:\n" + e.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        pluginMenu.add(loadPluginItem);
        return pluginMenu;
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

        }
    }

    private void saveProfile() {
        Properties props = new Properties();

        // главное окно
        props.setProperty("main.x", String.valueOf(getX()));
        props.setProperty("main.y", String.valueOf(getY()));
        props.setProperty("main.w", String.valueOf(getWidth()));
        props.setProperty("main.h", String.valueOf(getHeight()));
        props.setProperty("main.state", String.valueOf(getExtendedState()));

        // внутреннее окно лога
        saveInternalFrameState(props, "log", logWindow);
        // игровое окно
        saveInternalFrameState(props, "game", gameWindow);

        try (OutputStream out = new FileOutputStream(profileFile)) {
            props.store(out, "Robots Application Profile");
        } catch (IOException e) {
            Logger.error("Ошибка при сохранении профиля: " + e.getMessage());
        }
    }

    private void saveInternalFrameState(Properties props, String prefix, JInternalFrame frame) {
        if (frame != null) {
            props.setProperty(prefix + ".max", String.valueOf(frame.isMaximum()));
            props.setProperty(prefix + ".visible", String.valueOf(frame.isVisible()));
            props.setProperty(prefix + ".icon", String.valueOf(frame.isIcon()));

            Rectangle bounds;
            if (frame instanceof BaseInternalFrame){
                bounds = ((BaseInternalFrame) frame).getNormalBounds();
            } else {
                bounds = frame.getBounds();
            }
            props.setProperty(prefix + ".x", String.valueOf(bounds.x));
            props.setProperty(prefix + ".y", String.valueOf(bounds.y));
            props.setProperty(prefix + ".w", String.valueOf(bounds.width));
            props.setProperty(prefix + ".h", String.valueOf(bounds.height));
        }
    }

    private void loadProfile() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(profileFile)) {
            props.load(in);

            // главное окно
            setBounds(
                    Integer.parseInt(props.getProperty("main.x")),
                    Integer.parseInt(props.getProperty("main.y")),
                    Integer.parseInt(props.getProperty("main.w")),
                    Integer.parseInt(props.getProperty("main.h"))
            );
            setExtendedState(Integer.parseInt(props.getProperty("main.state")));

            // внутреннее окно лога
            restoreInternalFrameState(props, "log", logWindow);
            //игровое окно
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

            boolean isMaximum = Boolean.parseBoolean(props.getProperty(prefix + ".max", "false"));

            if (isMaximum) {
                frame.setMaximum(true);
            } else {
                frame.setIcon(Boolean.parseBoolean(props.getProperty(prefix + ".icon")));
            }
        }
    }
}

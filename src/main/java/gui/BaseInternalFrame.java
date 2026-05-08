package gui;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public abstract class BaseInternalFrame extends JInternalFrame {

    private Rectangle normalBounds;

    public BaseInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                confirmClose();
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateNormalBounds();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                updateNormalBounds();
            }
        });
    }

    private void updateNormalBounds() {
        if (!isMaximum() && !isIcon()) {
            normalBounds = getBounds();
        }
    }

    public Rectangle getNormalBounds() {
        return normalBounds != null ? normalBounds : getBounds();
    }

    private void confirmClose() {
        Object[] options = {"Да", "Нет"};
        int n = JOptionPane.showOptionDialog(this,
                "Вы действительно хотите закрыть окно '" + getTitle() + "'?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);
        if (n == JOptionPane.YES_OPTION) {
            dispose();
        }
    }
}
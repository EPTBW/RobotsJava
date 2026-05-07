package gui;
import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameAdapter;

public abstract class BaseInternalFrame extends JInternalFrame {
    public BaseInternalFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                confirmClose();
            }
        });
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
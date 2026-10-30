package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.MainWindow;

/** Starts the application. */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // use the default look if the system one is not available
        }
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}

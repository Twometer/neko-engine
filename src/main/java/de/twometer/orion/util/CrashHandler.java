package de.twometer.orion.util;

import de.twometer.orion.core.OrionApp;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.PrintWriter;
import java.io.StringWriter;

public class CrashHandler {

    public static void register() {
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> fatal(e));

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            fatal(e);
        }
    }

    public static void fatal(Throwable t) {
        Log.e("The game has crashed!", t);
        t.printStackTrace();
        if (t.getCause() != null) {
            System.err.println("Caused by:");
            t.getCause().printStackTrace();
        }

        try {
            OrionApp.get().getWindow().destroy();
        } catch (Exception e) {
            Log.e("Subsequent error during crash handling: Failed to destroy window");
            e.printStackTrace();
        }

        var writer = new StringWriter();
        var pwriter = new PrintWriter(writer);
        t.printStackTrace(pwriter);

        JOptionPane.showMessageDialog(null,
                "<html><body><h3>Unfortunately, the game has crashed.</h3><p style='font-family:monospaced;'><pre>" + writer.toString().replace("\r", "").replace("\n", "<br>") + "</pre></p></body></html>",
                "The game has crashed",
                JOptionPane.ERROR_MESSAGE);

        System.exit(1);
    }

}

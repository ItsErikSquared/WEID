package dev.itserik.weid;

import dev.itserik.weid.jna.User32;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class WEID {

    private static final IdleDetector idleDetector = new IdleDetector();
    private static final File propFile = new File("./WEID.properties");
    private static final Properties props = new Properties();
    private static Robot robot;
    private static boolean windowsMinimized = false;
    private static boolean paused = false;
    private static IdleDetector.State oldState;

    /**
     * Loads props, makes tray, starts idleDetector.
     * Handles checking each state.
     */
    public static void main(String[] args) {
        loadProperties(false);
        makeTray();
        idleDetector.start();

        try {
            robot = new Robot();
        } catch (AWTException e) {
            showAlert("Couldn't make a Robot: " + e.getMessage());
            e.printStackTrace();
        }

        while (true) {
            if (paused) {
                System.out.println("Paused: true");
                return;
            } else {
                System.out.println("Old State: " + oldState + " - New State: " + idleDetector.getCurrentState() + " - minimized: " + windowsMinimized);
                if ((idleDetector.getCurrentState() == IdleDetector.State.ONLINE && windowsMinimized) ||
                        (!idleDetector.getCurrentState().equals(IdleDetector.State.ONLINE) && !windowsMinimized)) {
                    toggleWindows();
                }
                if (oldState == IdleDetector.State.AWAY && idleDetector.getCurrentState() == IdleDetector.State.ONLINE) {
                    try {
                        Runtime.getRuntime().exec(System.getenv("windir") + File.separator + "System32" + File.separator + "rundll32.exe user32.dll,LockWorkStation");
                    } catch (IOException ex) {
                        showAlert("Cannot LockWorkstation: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                oldState = idleDetector.getCurrentState();
            }

            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                showAlert("InterruptedException: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Toggles the windows via the Java Robot (WIN + D), as well as hides the taskbar if they so choose.
     */
    public static void toggleWindows() {
        long li = idleDetector.lastInput;
        windowsMinimized = !windowsMinimized;
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
        robot.keyRelease(KeyEvent.VK_D);

        if (idleDetector.hideTaskbar)
            User32.INSTANCE.ShowWindow(User32.INSTANCE.FindWindow("Shell_TrayWnd", ""), windowsMinimized ? 0 : 1);
        idleDetector.lastInput = li;
    }

    /**
     * Makes a snazzy popup with the message provided.
     */
    public static void showAlert(String message) {
        final JFrame parent = new JFrame();
        JOptionPane.showConfirmDialog(parent, message, "WEID Notification", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Creates and loads the properties file along with defaults.
     * Also has some limitations to make sure they don't lock them selves out of their computer.
     */
    public static void loadProperties(boolean reload) {
        try {
            if (!propFile.exists()) {
                if (!propFile.createNewFile()) {
                    showAlert("Failed to load properties: createNewFile returned false");
                }
                showAlert("Welcome to WEID!\n\n" +
                        "It seems this is your first time running this app.\n" +
                        "Let's get you setup!\n\n" +
                        "A new file will be generated called WEID.properties, right next to wherever you ran this file from.\n" +
                        "Modify it to your liking, then reload it by double-clicking the icon in the System Tray (near your where you change your WiFi Network).\n" +
                        "When it reloads, a popup will appear letting you know it is done!");
            }

            props.setProperty("Paused", String.valueOf(false));
            props.setProperty("IdleTime", String.valueOf(300));
            props.setProperty("AwayTime", String.valueOf(300));
            props.setProperty("HideTaskbar", String.valueOf(true));
            props.load(new FileInputStream(propFile));
            props.store(new FileOutputStream(propFile), "IdleTime - Time to hide without locking on next move\nAwayTime - Time to hide with locking on next move\nHideTaskbar - Should it minimize the taskbar on minimize");

            long idle = Long.parseLong(props.getProperty("IdleTime")) * 1000;
            long away = Long.parseLong(props.getProperty("AwayTime")) * 1000;

            if (idle < 15) {
                showAlert("Oops!\n\nYour IdleTime cannot be less than 15 seconds!\nIt has been reset to 300s (5m)...");
                props.setProperty("IdleTime", String.valueOf(300));
                idle = 300;
            }

            if (away < 60) {
                showAlert("Oops!\n\nYour AwayTime cannot be less than 60 seconds!\nIt has been reset to 300s (5m)...");
                props.setProperty("AwayTime", String.valueOf(300));
                away = 300;
            }

            idleDetector.idle = idle;
            idleDetector.away = idle + away;
            idleDetector.hideTaskbar = Boolean.parseBoolean(props.getProperty("HideTaskbar"));
            paused = Boolean.parseBoolean(props.getProperty("Paused"));

            if (reload) {
                showAlert("WEID has been reloaded.");
            }

            System.out.println("IdleTime: " + idleDetector.idle);
            System.out.println("AwayTime: " + idleDetector.away);
            System.out.println("HideTaskbar: " + idleDetector.hideTaskbar);
        } catch (IOException ex) {
            showAlert("Failed to load properties: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Make's a tray icon. Double Clicking will reload the config, right clicking allows for more actions.
     * Preview - Shows what will happen when everything toggles.
     * Reload Config - Reload's the config
     * Stop App - Stops the app... pretty simple.
     */
    public static void makeTray() {
        try {
            PopupMenu popupMenu = new PopupMenu();
            TrayIcon trayIcon = new TrayIcon(
                    ImageIO.read(WEID.class.getResource("/res/WEID.png")),
                    "WEID - Wallpaper Engine Extension");
            trayIcon.setImageAutoSize(true);


            CheckboxMenuItem miPause = new CheckboxMenuItem("Pause App", Boolean.parseBoolean(props.getProperty("Paused")));
            MenuItem miReload = new MenuItem("Reload Config");
            MenuItem miPreview = new MenuItem("Preview");
            MenuItem miStop = new MenuItem("Stop Application");

            miPause.addItemListener(e -> {
                boolean paused = e.getStateChange() == ItemEvent.SELECTED;
                props.setProperty("Paused", String.valueOf(paused));
                WEID.paused = paused;
                if (!paused) {
                    idleDetector.lastInput = System.currentTimeMillis();
                }
            });
            miReload.addActionListener(e -> loadProperties(true));
            miPreview.addActionListener(e -> idleDetector.lastInput = System.currentTimeMillis() - idleDetector.idle);
            miStop.addActionListener(e -> System.exit(0));

            popupMenu.add(miPause);
            popupMenu.add(miReload);
            popupMenu.add(miPreview);
            popupMenu.addSeparator();
            popupMenu.add(miStop);
            trayIcon.addActionListener(e -> loadProperties(true));
            trayIcon.setPopupMenu(popupMenu);

            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException | IOException e) {
            showAlert("Error while building SystemTray: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Creates a new {@code JFrame} in the bottom right corner of the screen.
 * {@code SimpleFrame} also gives the option to add a {@code JOptionPane} option
 * message that asks to leave the program.
 * 
 * <p>
 * <b>No Known Issues</b>
 * 
 * @author Nathin Wascher
 * @version v1.1.6 - November 20, 2020
 * 
 * @see JFrame
 * @see JOptionPane
 * @see java.awt.event.WindowListener#windowClosing
 */
@SuppressWarnings("serial")
public class SimpleFrame extends JFrame {

    private static final int DEFAULT_WIDTH = 770;
    private static final int DEFAULT_HEIGHT = 500;

    private static final int MAX_WIDTH = 1540;
    private static final int MAX_HEIGHT = 840;

    private ImageIcon titleImage;
    private String javaFileName;
    private String frameTitle;
    private boolean fullscreen;
    private boolean addWarningWindow;

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title in the center of the
     * screen. The dimensions are set to 720 x 500.
     * 
     * @param javaFileName The name of the main class file (without the '.java').
     * @param frameTitle   The title at the top of the {@code JFrame}.
     * @see JFrame
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        newFrame();
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and dimensions in the
     * center of the screen.
     * 
     * @param javaFileName The name of the main class file (without the '.java').
     * @param frameTitle   The title at the top of the {@code JFrame}.
     * @param width        The initial width of the {@code JFrame}. This value
     *                     should be less than 1540.
     * @param height       The initial height of the {@code JFrame}. This value
     *                     should be less than 840.
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, int width, int height) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        newFrame(width, height);
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and dimensions in the
     * center of the screen. It also gives the option to allow a {@code JOptionPane}
     * that is created when clicking the {@code JFrame} exit. The
     * {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param width            The initial width of the {@code JFrame}. This value
     *                         should be less than 1540.
     * @param height           The initial height of the {@code JFrame}. This value
     *                         should be less than 840.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, int width, int height, boolean addWarningWindow) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.addWarningWindow = addWarningWindow;
        newFrame(width, height);
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and dimensions in the
     * center of the screen. It also gives the option to allow a {@code JOptionPane}
     * that is created when clicking the {@code JFrame} exit. The
     * {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param width            The initial width of the {@code JFrame}. This value
     *                         should be less than 1540.
     * @param height           The initial height of the {@code JFrame}. This value
     *                         should be less than 840.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @param titleImage       The image that displays in the titlebar
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, int width, int height, boolean addWarningWindow,
            ImageIcon titleImage) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.addWarningWindow = addWarningWindow;
        this.titleImage = titleImage;
        newFrame(width, height);
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and the option to start out
     * maxamized, otherwise the dimensions are automatically set to 720 x 500 and
     * put in the center of the screen. When the {@code JFrame} is unmaxamized, it
     * goes to the before mentioned size. It also gives the option to allow
     * {@code JOptionPane} that is created when clicking the {@code JFrame} exit.
     * The {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param fullscreen       If the {@code JFrame} should start maxamized.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, boolean fullscreen, boolean addWarningWindow) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.fullscreen = fullscreen;
        this.addWarningWindow = addWarningWindow;
        newFrame();
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and the title image and the
     * option to start out maxamized, otherwise the dimensions are automatically set
     * to 720 x 500 and put in the center of the screen. When the {@code JFrame} is
     * unmaxamized, it goes to the before mentioned size. It also gives the option
     * to allow {@code JOptionPane} that is created when clicking the {@code JFrame}
     * exit. The {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param fullscreen       If the {@code JFrame} should start maxamized.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @param titleImage       The image that displays in the titlebar
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, boolean fullscreen, boolean addWarningWindow,
            ImageIcon titleImage) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.fullscreen = fullscreen;
        this.addWarningWindow = addWarningWindow;
        this.titleImage = titleImage;
        newFrame();
    }

    /**
     * Overloaded Method
     * <p>
     * Creates a {@code JFrame} with the specified title and the option to start out
     * maxamized. When the {@code JFrame} is unmaxamized, it goes to the specified
     * dimensions tn the center of the screen. It also gives the option to allow a
     * {@code JOptionPane} that is created when clicking the {@code JFrame} exit.
     * The {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param width            The initial width of the {@code JFrame}. This value
     *                         should be less than 1540.
     * @param height           The initial height of the {@code JFrame}. This value
     *                         should be less than 840.
     * @param fullscreen       If the {@code JFrame} should start maxamized.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @see JFrame
     * @see JOptionPane
     * @see #SimpleFrame(String, String, int, int, boolean, boolean, ImageIcon)
     */
    public SimpleFrame(String javaFileName, String frameTitle, int width, int height, boolean fullscreen,
            boolean addWarningWindow) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.fullscreen = fullscreen;
        this.addWarningWindow = addWarningWindow;
        newFrame(width, height);
    }

    /**
     * Creates a {@code JFrame} with the specified title and the option to start out
     * maxamized. When the {@code JFrame} is unmaxamized, it goes to the specified
     * dimensions tn the center of the screen. It also gives the option to allow a
     * {@code JOptionPane} that is created when clicking the {@code JFrame} exit.
     * The {@code JOptionPane} is a warning message asking to exit the program
     * completely or to keep the window open.
     * 
     * @param javaFileName     The name of the main class file (without the
     *                         '.java').
     * @param frameTitle       The title at the top of the {@code JFrame}.
     * @param width            The initial width of the {@code JFrame}. This value
     *                         should be less than 1540.
     * @param height           The initial height of the {@code JFrame}. This value
     *                         should be less than 840.
     * @param fullscreen       If the {@code JFrame} should start maxamized.
     * @param addWarningWindow When a user attempts to exit the {@code JFrame}, if a
     *                         {@code JOptionPane} should appear and ask to exit the
     *                         window or keep the {@code JFrame} open.
     * @param titleImage       The image that displays in the titlebar
     * @see JFrame
     * @see JOptionPane
     */
    public SimpleFrame(String javaFileName, String frameTitle, int width, int height, boolean fullscreen,
            boolean addWarningWindow, ImageIcon titleImage) {
        this.javaFileName = javaFileName;
        this.frameTitle = frameTitle;
        this.fullscreen = fullscreen;
        this.addWarningWindow = addWarningWindow;
        this.titleImage = titleImage;
        newFrame(width, height);
    }

    // Overloaded method
    private void newFrame() {
        newFrame(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    // Creates a new JFrame with the variables from the constructor
    private void newFrame(int width, int height) {
        setTitle(frameTitle);
        setBounds((MAX_WIDTH - width) / 2, (MAX_HEIGHT - height) / 2, width, height);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        if (fullscreen) {
            this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
        if (titleImage != null) {
            setIconImage(titleImage.getImage());
        }
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                systemExit();
            }
        });
    }

    // Controls the JOptionPane and system exit
    private void systemExit() {
        String warning = "Do you really want to exit?\nAll progress will be lost!";

        if (addWarningWindow) {
            int output = JOptionPane.showConfirmDialog(this, warning, "WARNING", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (output == JOptionPane.YES_OPTION) {
                System.out.println("...Terminating Program (" + javaFileName + ")");
                this.dispose();
            }
        } else {
            System.out.println("...Terminating Program (" + javaFileName + ")");
            this.dispose();
        }
    }
}

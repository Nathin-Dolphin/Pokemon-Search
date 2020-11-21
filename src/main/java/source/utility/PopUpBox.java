
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility;

import java.awt.GridLayout;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * <p>
 * <b>No Known Issues.</b>
 * 
 * @author Nathin Wascher
 * @version v1.0.1 - November 20, 2020
 * 
 * @see JFrame
 */
@SuppressWarnings("serial")
public final class PopUpBox extends JPanel {

    private static final int WIDTH_MULTIPLIER = 10;
    private static final int HEIGHT_MULTIPLIER = 50;

    private static final int MAX_WIDTH = 1540;
    private static final int MAX_HEIGHT = 840;

    private ArrayList<JLabel> messageList;
    private ArrayList<String> contents;
    private String title;
    private int width;
    private int height;

    /**
     * 
     * @param title
     * @param contents
     */
    public PopUpBox(String title, ArrayList<String> contents) {
        messageList = new ArrayList<>();
        height = 0;
        this.title = title;
        this.contents = contents;
    }

    /**
     * 
     */
    public void createBox() {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(0, 1));

        width = 0;
        for (int h = 0; h < contents.size(); h++) {
            contents.get(h).replace("\t", "     ");
            messageList.add(new JLabel("     " + contents.get(h)));
            this.add(messageList.get(h));

            height = h + 1;
            if (width < contents.get(h).length()) {
                width = contents.get(h).length();
            }
        }

        height = height * HEIGHT_MULTIPLIER;
        width = width * WIDTH_MULTIPLIER;

        frame.setBounds((MAX_WIDTH - width) / 2, (MAX_HEIGHT - height) / 2, width, height);

        frame.add(this);
        frame.setVisible(true);
    }
}

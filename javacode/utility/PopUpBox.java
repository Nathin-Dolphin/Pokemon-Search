
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package utility;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.ArrayList;

/**
 * 
 * <p>
 * <b>No Known Issues</b>
 * 
 * @author Nathin Wascher
 * @version v1.0 - November 19, 2020
 * 
 * @see JFrame
 */
@SuppressWarnings("serial")
public class PopUpBox extends JPanel {
    private ArrayList<JLabel> messageList;
    private ArrayList<String> contents;
    private String title;
    private int width, height;

    public PopUpBox(String title, ArrayList<String> contents) {
        messageList = new ArrayList<>();
        height = 0;
        this.title = title;
        this.contents = contents;
    }

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
            if (width < contents.get(h).length())
                width = contents.get(h).length();
        }

        height = height * 50;
        width = width * 10;

        frame.setBounds((1540 - width) / 2, (840 - height) / 2, width, height);

        frame.add(this);
        frame.setVisible(true);
    }
}
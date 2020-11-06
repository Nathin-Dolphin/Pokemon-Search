
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.SimpleFrame;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// TODO: Add continue option after saving and exiting

/**
 * @author Nathin Wascher
 * @version PokedexWriter v2.1
 * @since November 6, 2020
 */
@SuppressWarnings("serial")
public class PokedexWriter extends JPanel implements ActionListener {
    private SimpleFrame frame;
    private GridBagConstraints gbc;

    private JButton[] buttonList;
    private JLabel[] labelList;
    private JTextField paramTextField;

    public static void main(String[] args) {
        System.out.println("Excecuting Program (PokedexWriter)...");
        new PokedexWriter();
    }

    private PokedexWriter() {
        frame = new SimpleFrame("PokedexWriter", "What pokedex are you writing today?", 500, 400, true);
        gbc = new GridBagConstraints();

        setLayout(new GridBagLayout());
        setBackground(new Color(0, 150, 0));

        setUp();
        showInfoBox();

        frame.add(this);
        frame.setVisible(true);
    }

    private void showInfoBox() {
        // TODO: Add icon
        String message = "Copyright (c) 2020 Nathin-Dolphin";
        message = message + "\nThis file is under the MIT License";
        message = message + "\n\nPokemon is a registered trademark of Nintendo";
        message = message + "\n\nWrite or modify pokedexes for Pokemon Search to use";
        JOptionPane.showMessageDialog(this, message, "Pokedex Writer v2.1", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setUp() {
        paramTextField = new JTextField(12);

        labelList = new JLabel[5];
        labelList[0] = new JLabel("Write region name here:");
        labelList[1] = new JLabel("Write a new pokedex.");
        labelList[2] = new JLabel("Write a new pokedex with assistance.");
        labelList[3] = new JLabel("Modify an already existing pokedex.");
        labelList[4] = new JLabel("Not Yet Implemented.");

        buttonList = new JButton[4];
        buttonList[0] = new JButton("Write New Pokedex");
        buttonList[1] = new JButton("Write New Pokedex (Assisted)");
        buttonList[2] = new JButton("Modify Pokedex");
        buttonList[3] = new JButton("Not Yet Implemented");

        for (JButton b : buttonList)
            b.addActionListener(this);

        setGBC(0, 0);
        add(labelList[0], gbc);
        setGBC(0, 1);
        add(paramTextField, gbc);
        for (int i = 0; i < buttonList.length; i++) {
            setGBC(0, (i * 2) + 2);
            add(labelList[i + 1], gbc);
            setGBC(0, (i * 2) + 3);
            add(buttonList[i], gbc);
        }
    }

    private void setGBC(int gridX, int gridY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String regionName = paramTextField.getText();
        String[] tempArray = { " ", "\t", "\n" };

        for (int j = 0; j < tempArray.length; j++)
            regionName = regionName.replace(tempArray[j], "");

        if (!regionName.equals(""))
            if (e.getSource() == buttonList[0]) { // 'Write New Pokedex' Button
                new PokedexWriter_Panel(regionName, false);

            } else if (e.getSource() == buttonList[1]) { // 'Write New Pokedex (Assisted)' Button
                new PokedexWriter_Panel(regionName, true);

            } else if (e.getSource() == buttonList[2]) { // 'Modify Pokedex' Button
                new PokedexWriter_Panel(regionName);

            } else { // 'Not Yet Implemented' Button
                JOptionPane.showMessageDialog(this, "ERROR: NOT YET IMPLEMENTED", "ERROR: NOT YET IMPLEMENTED",
                        JOptionPane.WARNING_MESSAGE);

            }
        else
            JOptionPane.showMessageDialog(this, "ERROR: NO REGION NAME INPUTTED", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
}
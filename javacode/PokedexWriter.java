
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.Misc;
import utility.SimpleFrame;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

// TODO: Add continue option after saving and exiting

/**
 * @author Nathin Wascher
 * @version v2.2 - November 19, 2020
 */
@SuppressWarnings("serial")
public class PokedexWriter extends JPanel implements ActionListener {
    private SimpleFrame frame;
    private GridBagConstraints gbc;

    private ImageIcon image;
    private JButton[] buttonList;
    private JTextField paramTextField;

    public static void main(String[] args) {
        System.out.println("Excecuting Program (PokedexWriter)...");
        new PokedexWriter();
    }

    private PokedexWriter() {
        findImageIcon();
        frame = new SimpleFrame("PokedexWriter", "What pokedex are you writing today?", 500, 400, true, image);
        gbc = new GridBagConstraints();

        setLayout(new GridBagLayout());
        setBackground(new Color(0, 200, 0));

        setUp();
        showInfoBox();

        frame.add(this);
        frame.setVisible(true);
    }

    private void showInfoBox() {
        String message = "Copyright (c) 2020 Nathin-Dolphin";
        message = message + "\nThis file is under the MIT License";
        message = message + "\nPokemon is a registered trademark of Nintendo";
        message = message + "\n\nWrite or modify pokedexes for Pokemon Search to use.";
        message = message + "\n\n[!] Known Issues:";
        message = message + "\n- The list fails to automatically show the bottom most line.";
        message = message + "\n- If the window gets too small, the lists and text fields become miniscule.";
        message = message + "\n- Closing the terminal is the only way to properly terminate the program.";
        JOptionPane.showMessageDialog(this, message, "Pokedex Writer v2.1", JOptionPane.INFORMATION_MESSAGE);
    }

    private void findImageIcon() {
        java.net.URL imgURL = PokemonSearch_Panel.class.getResource("Pokeball.png");

        if (imgURL != null)
            image = new ImageIcon(imgURL, "Pokeball");
        else
            System.out.println("Couldn't find file: Pokeball.png");
    }

    private void setUp() {
        JLabel[] labelList;
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

        add(labelList[0], Misc.setGBC(gbc, 0, 0));
        add(paramTextField, Misc.setGBC(gbc, 0, 1));

        for (int i = 0; i < buttonList.length; i++) {
            add(labelList[i + 1], Misc.setGBC(gbc, 0, (i * 2) + 2));
            add(buttonList[i], Misc.setGBC(gbc, 0, (i * 2) + 3));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String regionName = paramTextField.getText();
        String[] tempArray = { " ", "\t", "\n" };

        for (int j = 0; j < tempArray.length; j++)
            regionName = regionName.replace(tempArray[j], "");

        if (!regionName.equals(""))
            if (e.getSource() == buttonList[0]) { // 'Write New Pokedex' Button
                new PokedexWriter_Panel(regionName, 1);

            } else if (e.getSource() == buttonList[1]) { // 'Write New Pokedex (Assisted)' Button
                new PokedexWriter_Panel(regionName, 2);

            } else if (e.getSource() == buttonList[2]) { // 'Modify Pokedex' Button
                new PokedexWriter_Panel(regionName, 3);

            } else { // 'Not Yet Implemented' Button
                JOptionPane.showMessageDialog(this, "ERROR: NOT YET IMPLEMENTED", "ERROR: NOT YET IMPLEMENTED",
                        JOptionPane.WARNING_MESSAGE);
            }
        else
            JOptionPane.showMessageDialog(this, "ERROR: NO REGION NAME INPUTTED", "ERROR", JOptionPane.WARNING_MESSAGE);
    }
}
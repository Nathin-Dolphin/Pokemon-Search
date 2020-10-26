
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.json.JSONReader;
import utility.json.JSONWriter;
import utility.json.URLReader;

import utility.SimpleFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.List;

import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 */
public class PokedexWriter_Panel extends PokedexWriter_Writer {
    private static final long serialVersionUID = 2628539169557674903L;
    private final String pokeInfoURL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";

    private SimpleFrame frame;
    private GridBagConstraints gbc;
    private JSONReader pwpJsonReader;
    private URLReader urlReader;

    // TODO: Reorganize JPanels
    private JPanel controlPanel;
    private JPanel topPanel, middlePanel, bottomPanel, evoNumPanel, nameBarPanel;

    private JLabel regionHighJL, regionLowJL, nameJL, evolutionJL, evoNumJL, type1JL, type2JL;

    private ArrayList<String> tempArray;
    // private int paneValue;

    public PokedexWriter_Panel() {
    }

    public PokedexWriter_Panel(String regionName) {
        setUp(regionName);

        // Temporarly initialize 'min' and 'max' to a value so the frame is created
        // properly
        min = 0;
        max = 1;

        setLayout(new GridBagLayout());
        setUpControlPanel();

        setGBC(0, 0);
        add(outputList, gbc);
        setGBC(1, 0);
        add(controlPanel, gbc);

        frame.add(this);
        frame.setVisible(true);

        modifyFile();
    }

    public PokedexWriter_Panel(String regionName, boolean useURL) {
        setUp(regionName);

        if (!useURL)
            initializeRange();
        setLayout(new GridBagLayout());
        setUpControlPanel();

        setGBC(0, 0);
        add(outputList, gbc);
        setGBC(1, 0);
        add(controlPanel, gbc);

        frame.add(this);
        frame.setVisible(true);

        if (useURL)
            openURL();
    }

    private void setUp(String regionName) {
        // check other classes to remove redundant code that changes the letter casing
        // for 'regionName'
        regionName = regionName.toLowerCase();
        String tempString = regionName;
        if (regionName.endsWith(".json")) {
            regionName = regionName.replace(".json", "");
        }
        tempString = regionName + ".json";

        frame = new SimpleFrame("PokedexWriter", "Creating file: \"" + tempString + "\"", 900, 650, true);
        gbc = new GridBagConstraints();
        pwpJsonReader = new JSONReader();
        pwwJsonWriter = new JSONWriter(this, tempString);
        this.regionName = regionName;

        // Attempt to read 'pokeInfo.json', or if
        // it doesn't exist, download neccessary files
        try {
            pwpJsonReader.readJSON("pokeInfo");
            typeList = pwpJsonReader.get("types");
            regionList = pwpJsonReader.get("regions");
        } catch (FileNotFoundException e) {
            try {
                downloadPokeInfo();
            } catch (Exception x) {
                System.out.println("ERROR: UNKNOWN ERROR");
            }
        }
    }

    private void downloadPokeInfo() {
        System.out.println("Downloading file: pokeInfo.json");
        urlReader = new URLReader();
        tempArray = urlReader.readURL(pokeInfoURL);

        try {
            // if urlReader.readURL returns nothing
            if (tempArray == null)
                throw new Exception();

            FileWriter fw = new FileWriter("pokeInfo.json");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < tempArray.size() - 1; i++) {
                pw.println(tempArray.get(i));
            }
            pw.print(tempArray.size() - 1);
            pw.close();

        } catch (Exception e) {
            System.out.println("ERROR: FAILED TO DOWNLOAD " + regionName + ".json");
            System.exit(0);
        }
    }

    private void initializeRange() {
        // Object[] minMaxPaneJTF = new Object[] { new JButton("OK"), new JTextField(5),
        // new JButton("CANCEL") };
        // JOptionPane optionPane = new JOptionPane("test", JOptionPane.PLAIN_MESSAGE,
        // JOptionPane.OK_CANCEL_OPTION, null,
        // minMaxPaneJTF, null);

        min = 0;
        String j = "";
        do {
            do {
                j = JOptionPane.showInputDialog(this, "Input min: ", "title", JOptionPane.INFORMATION_MESSAGE);
                try {
                    min = Integer.parseInt(j);
                } catch (Exception e) {
                    j = "";
                    System.out.println("ERROR: NOT A NUMBER");
                }
            } while (j.equals(""));

            if (min <= 0)
                System.out.println("ERROR: MIN CAN NOT BE LOWER THAN 1");
        } while (min <= 0);

        pokeNum = min;
        j = "";
        do {
            do {
                j = JOptionPane.showInputDialog(this, "Input max: ", "title", JOptionPane.INFORMATION_MESSAGE);
                try {
                    max = Integer.parseInt(j);
                } catch (Exception e) {
                    j = "";
                    System.out.println("ERROR: NOT A NUMBER" + e);
                }
            } while (j.equals(""));

            if (max <= min)
                System.out.println("ERROR: MAX CAN NOT BE LOWER THAN MIN");
        } while (max <= min);
    }

    private void setUpControlPanel() {
        controlPanel = new JPanel(new GridBagLayout());

        setUpTopPanel();
        setUpMiddlePanel();
        setUpBottomPanel();

        setGBC(0, 0);
        controlPanel.add(topPanel, gbc);
        setGBC(0, 1);
        controlPanel.add(middlePanel, gbc);
        setGBC(0, 2);
        controlPanel.add(bottomPanel, gbc);
    }

    private void setUpTopPanel() {
        topPanel = new JPanel(new GridBagLayout());

        regionLowJL = new JLabel("Min Region Range: ");
        minJTF = new JTextField(String.valueOf(min), 3);

        setGBC(0, 0);
        topPanel.add(regionLowJL, gbc);
        setGBC(1, 0);
        topPanel.add(minJTF, gbc);

        regionHighJL = new JLabel("Max Region Range: ");
        maxJTF = new JTextField(String.valueOf(max), 3);

        setGBC(0, 1);
        topPanel.add(regionHighJL, gbc);
        setGBC(1, 1);
        topPanel.add(maxJTF, gbc);
    }

    private void setUpMiddlePanel() {
        middlePanel = new JPanel(new GridBagLayout());
        nameBarPanel = new JPanel(new GridBagLayout());

        nameJL = new JLabel("Pokemon Name: ");
        nameJTF = new JTextField(10);
        nameJTF.addActionListener(this);

        setGBC(0, 0);
        nameBarPanel.add(nameJL, gbc);
        setGBC(1, 0);
        nameBarPanel.add(nameJTF, gbc);

        gbc.gridwidth = 2;
        setGBC(0, 0);
        middlePanel.add(nameBarPanel, gbc);
        gbc.gridwidth = 1;

        type1JL = new JLabel("Primary Type");
        type2JL = new JLabel("Secondary Type");

        type1CL = new List(15);
        type2CL = new List(15);
        type2CL.add("none");
        for (String s : typeList) {
            type1CL.add(s);
            type2CL.add(s);
        }
        type1CL.select(0);
        type2CL.select(0);

        setGBC(0, 1);
        middlePanel.add(type1JL, gbc);
        setGBC(0, 2);
        middlePanel.add(type1CL, gbc);
        setGBC(1, 1);
        middlePanel.add(type2JL, gbc);
        setGBC(1, 2);
        middlePanel.add(type2CL, gbc);
    }

    private void setUpBottomPanel() {
        bottomPanel = new JPanel(new GridBagLayout());
        evoNumPanel = new JPanel(new GridBagLayout());

        nextEvoNumJB = new JButton("Next EvoNum");
        nextEvoNumJB.addActionListener(this);
        evoNumJL = new JLabel(" EvoNumber: ");
        evoNumJTF = new JTextField("000", 3);

        setGBC(0, 0);
        evoNumPanel.add(nextEvoNumJB, gbc);
        setGBC(0, 1);
        evoNumPanel.add(evoNumJL, gbc);
        setGBC(0, 2);
        evoNumPanel.add(evoNumJTF, gbc);

        evolutionJL = new JLabel("Evolution State");
        evolutionCL = new List(5);
        for (String s : evolutionStates)
            evolutionCL.add(s);
        evolutionCL.select(1);

        setGBC(0, 0);
        bottomPanel.add(evolutionJL, gbc);
        setGBC(0, 1);
        bottomPanel.add(evolutionCL, gbc);
        setGBC(1, 1);
        bottomPanel.add(evoNumPanel, gbc);

        enterJB = new JButton("Next Pokemon");
        enterJB.addActionListener(this);

        gbc.gridwidth = 2;
        setGBC(0, 2);
        bottomPanel.add(enterJB, gbc);
        gbc.gridwidth = 1;
    }

    private void setGBC(int gridX, int gridY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
    }
}
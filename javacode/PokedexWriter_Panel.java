
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.ListModelObject;
import utility.Misc;
import utility.SimpleFrame;
import utility.json.JSONReader;
import utility.json.JSONWriter;
import utility.json.URLReader;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 */
@SuppressWarnings("serial")
public class PokedexWriter_Panel extends PokedexWriter_Writer {
    private final String pokeInfoURL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";

    private DefaultListModel<ListModelObject> tempLMO;
    private SimpleFrame frame;
    private GridBagConstraints gbc;
    private JSONReader pwpJsonReader;
    private URLReader urlReader;

    // TODO: Reorganize JPanels
    private JPanel controlPanel;
    private JPanel topPanel, middlePanel, bottomPanel, evoNumPanel, nameBarPanel;

    private ImageIcon image;
    private JLabel regionHighJLabel, regionLowJLabel, nameJLabel, evolutionJLabel, evoNumJLabel, type1JLabel,
            type2JLabel;

    private ArrayList<String> tempArray;

    /**
     * 
     * @param regionName The name of the json (and the pokemon region)
     * @param mode       '1' = Write a new Pokedex; '2' = Write a new Pokedex with a
     *                   URL; '3' = Modify a pokedex
     */
    public PokedexWriter_Panel(String regionName, int mode) {
        setUp(regionName);

        if (mode == 1) {
            initializeRange();
        } else if (mode == 3) {
            // Temporarly initialize 'min' and 'max' to a
            // value so the frame is created properly
            min = 0;
            max = 1;
        }

        setLayout(new GridBagLayout());
        setUpControlPanel();

        add(outputListPane, Misc.setGBC(gbc, 0, 0));
        add(controlPanel, Misc.setGBC(gbc, 1, 0));

        frame.add(this);
        frame.setVisible(true);

        if (mode == 2)
            modifyFile();
        else if (mode == 3)
            openURL();
    }

    private void createImageIcon() {
        java.net.URL imgURL = PokemonSearch_Panel.class.getResource("Pokeball.png");

        if (imgURL != null)
            image = new ImageIcon(imgURL, "Pokeball");
        else
            System.out.println("Couldn't find file: Pokeball.png");
    }

    private void setUp(String regionName) {
        createImageIcon();

        regionName = regionName.toLowerCase();
        String tempString = regionName;
        if (regionName.endsWith(".json")) {
            regionName = regionName.replace(".json", "");
        }
        tempString = regionName + ".json";

        frame = new SimpleFrame("PokedexWriter", "Creating file: \"" + tempString + "\"", 600, 500, true);
        frame.setIconImage(image.getImage());
        gbc = new GridBagConstraints();
        pwpJsonReader = new JSONReader();
        pwwJsonWriter = new JSONWriter(this, tempString);
        setRegionName(regionName);

        // Attempt to read 'pokeInfo.json', or if
        // it doesn't exist, download neccessary files
        try {
            pwpJsonReader.readJSON("pokeInfo");
            typeList = pwpJsonReader.get("types");
            regionList = pwpJsonReader.get("regions");
        } catch (FileNotFoundException e) {
            try {
                downloadPokeInfo(regionName);
            } catch (Exception x) {
                System.out.println("ERROR: UNKNOWN ERROR");
            }
        }
    }

    private void downloadPokeInfo(String regionName) {
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

        controlPanel.add(topPanel, Misc.setGBC(gbc, 0, 0));
        controlPanel.add(middlePanel, Misc.setGBC(gbc, 0, 1));
        controlPanel.add(bottomPanel, Misc.setGBC(gbc, 0, 2));
    }

    private void setUpTopPanel() {
        topPanel = new JPanel(new GridBagLayout());

        regionLowJLabel = new JLabel("Min Region Range: ");
        minJTF = new JTextField(String.valueOf(min), 3);

        topPanel.add(regionLowJLabel, Misc.setGBC(gbc, 0, 0));
        topPanel.add(minJTF, Misc.setGBC(gbc, 1, 0));

        regionHighJLabel = new JLabel("Max Region Range: ");
        maxJTF = new JTextField(String.valueOf(max), 3);

        topPanel.add(regionHighJLabel, Misc.setGBC(gbc, 0, 1));
        topPanel.add(maxJTF, Misc.setGBC(gbc, 1, 1));
    }

    private void setUpMiddlePanel() {
        middlePanel = new JPanel(new GridBagLayout());
        nameBarPanel = new JPanel(new GridBagLayout());

        nameJLabel = new JLabel("Pokemon Name: ");
        nameJTF = new JTextField(10);
        nameJTF.addActionListener(this);

        nameBarPanel.add(nameJLabel, Misc.setGBC(gbc, 0, 0));
        nameBarPanel.add(nameJTF, Misc.setGBC(gbc, 1, 0));

        gbc.gridwidth = 2;
        middlePanel.add(nameBarPanel, Misc.setGBC(gbc, 0, 0));
        gbc.gridwidth = 1;

        type1JLabel = new JLabel("Primary Type");
        type2JLabel = new JLabel("Secondary Type");
        {
            tempLMO = new DefaultListModel<>();
            for (String t : typeList)
                tempLMO.addElement(new ListModelObject(t));

            type1JList = new JList<>(tempLMO);
            type1JList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            type1JList.setVisibleRowCount(8);
            type1JList.setSelectedIndex(0);

            JScrollPane type1Pane = new JScrollPane(type1JList);

            middlePanel.add(type1JLabel, Misc.setGBC(gbc, 0, 1));
            middlePanel.add(type1Pane, Misc.setGBC(gbc, 0, 2));
        }
        { //
            tempLMO = new DefaultListModel<>();
            tempLMO.addElement(new ListModelObject("none"));
            for (String t : typeList)
                tempLMO.addElement(new ListModelObject(t));

            type2JList = new JList<>(tempLMO);
            type2JList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            type2JList.setVisibleRowCount(8);
            type2JList.setSelectedIndex(0);

            JScrollPane type2Pane = new JScrollPane(type2JList);

            middlePanel.add(type2JLabel, Misc.setGBC(gbc, 1, 1));
            middlePanel.add(type2Pane, Misc.setGBC(gbc, 1, 2));
        }
    }

    private void setUpBottomPanel() {
        bottomPanel = new JPanel(new GridBagLayout());
        evoNumPanel = new JPanel(new GridBagLayout());

        nextEvoNumJB = new JButton("Next EvoNum");
        nextEvoNumJB.addActionListener(this);
        evoNumJLabel = new JLabel(" EvoNumber: ");
        evoNumJTF = new JTextField("000", 3);

        evoNumPanel.add(nextEvoNumJB, Misc.setGBC(gbc, 0, 0));
        evoNumPanel.add(evoNumJLabel, Misc.setGBC(gbc, 0, 1));
        evoNumPanel.add(evoNumJTF, Misc.setGBC(gbc, 0, 2));

        evolutionJLabel = new JLabel("Evolution State");
        tempLMO = new DefaultListModel<>();
        for (String s : evolutionStates)
            tempLMO.addElement(new ListModelObject(s));
        evolutionJList = new JList<>(tempLMO);
        type1JList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evolutionJList.setSelectedIndex(1);

        bottomPanel.add(evolutionJLabel, Misc.setGBC(gbc, 0, 0));
        bottomPanel.add(evolutionJList, Misc.setGBC(gbc, 0, 1));
        bottomPanel.add(evoNumPanel, Misc.setGBC(gbc, 1, 1));

        enterJB = new JButton("Next Pokemon");
        enterJB.addActionListener(this);

        gbc.gridwidth = 2;
        gbc = Misc.setGBC(gbc, 0, 2);
        bottomPanel.add(enterJB, Misc.setGBC(gbc, 0, 2));
        gbc.gridwidth = 1;
    }
}
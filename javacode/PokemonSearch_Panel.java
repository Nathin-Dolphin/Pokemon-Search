
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.ListModelObject;
import utility.Misc;
import utility.SimpleFrame;
import utility.json.JSONReader;
import utility.json.URLReader;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Nathin Wascher
 */
@SuppressWarnings("serial")
public class PokemonSearch_Panel extends JPanel implements ListSelectionListener {
    private final String pokeInfoURL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";

    private SimpleFrame frame;
    private PokemonSearch_Searcher pokeSearch;
    private URLReader pspUrlReader;
    private JSONReader pspJsonReader;
    private GridBagConstraints gbc;

    private DefaultListModel<ListModelObject> tempDLM;
    private JList<ListModelObject> typeJL, regionJL, evolutionJL;
    private JTextField searchTF;
    private ImageIcon image;

    private ArrayList<String> typeInput, regionInput, evolutionInput, typeList, regionList;
    private String pokeInfoJSONVersion = null, pokeInfoURLVersion = null;
    private String input;
    private int failSafe = -1;

    // Initializes the frame and some other classes
    public PokemonSearch_Panel() {
        findImageIcon();
        frame = new SimpleFrame("PokemonSearch", "Guess That Pokemon!", 1000, 600, true, false, image);
        frame.setLayout(new GridLayout(1, 2));

        gbc = new GridBagConstraints();
        pspJsonReader = new JSONReader();
        pspUrlReader = new URLReader();
        pokeSearch = new PokemonSearch_Searcher(pspJsonReader);

        regionInput = new ArrayList<String>();
        typeInput = new ArrayList<String>();
        evolutionInput = new ArrayList<String>();
        input = "";

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 0, 0));

        readPokeInfo();
        setUpPanels();
        showInfoBox();

        frame.add(pokeSearch.outputListPane);
        frame.add(this);
        frame.setVisible(true);
    }

    private void showInfoBox() {
        String message = "Copyright (c) 2020 Nathin-Dolphin";
        message = message + "\nThis file is under the MIT License";
        message = message + "\nPokemon is a registered trademark of Nintendo";
        message = message + "\n\nDescription:\nSearch for Pokemon by name, number, type, and evolution.";
        message = message + "\n\n[!] Known Issues:";
        message = message + "\n- The screen fails to load all of the pokemon when the window appears.";
        JOptionPane.showMessageDialog(this, message, "Pokemon Search v2.0.5", JOptionPane.INFORMATION_MESSAGE);
    }

    private void findImageIcon() {
        java.net.URL imageURL = PokemonSearch_Panel.class.getResource("Pokeball.png");

        if (imageURL != null)
            image = new ImageIcon(imageURL, "Pokeball");
        else
            System.out.println("Couldn't find file: Pokeball.png");
    }

    // Reads from the locally stored file 'pokeInfo'
    private void readPokeInfo() {
        ListModelObject tempLMO;

        // To prevent an infinite loop
        failSafe++;
        if (failSafe >= 5) {
            System.out.println("ERROR: FAILED TO DOWNLOAD \'POKEINFO\' AFTER MULTIPLE ATTEMPTS");
            System.out.println("...Terminating Program (PokemonSearch)");
            System.exit(0);
        }

        // Read from 'pokeInfo' and get version, types, and regions
        try {
            pspJsonReader.readJSON("pokeInfo");
            pokeInfoJSONVersion = pspJsonReader.get("version").get(0);

            typeList = pspJsonReader.get("types");
            tempDLM = new DefaultListModel<>();
            for (int i = 0; i < typeList.size(); i++) {
                tempLMO = new ListModelObject(typeList.get(i));
                tempDLM.addElement(tempLMO);
            }
            typeJL = new JList<>(tempDLM);
            typeJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            typeJL.addListSelectionListener(this);

            regionList = pspJsonReader.get("regions");
            tempDLM = new DefaultListModel<>();
            for (int i = 0; i < regionList.size(); i = i + 2) {
                regionInput.add(regionList.get(i));
                tempLMO = new ListModelObject(regionList.get(i));
                tempDLM.addElement(tempLMO);
            }
            regionJL = new JList<>(tempDLM);
            regionJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            regionJL.addListSelectionListener(this);

            // Download 'pokeInfo' if the file is not found
            // or if the version number is unobtainable
        } catch (Exception e) {
            downloadPokeInfo(true);
            readPokeInfo();
        }

        // Initially just read 'pokeInfo' from the url
        if (failSafe <= 0)
            downloadPokeInfo(false);

        // Get the version number from 'pokeInfo' through the url
        try {
            pokeInfoURLVersion = pspUrlReader.get("version").get(0);
        } catch (NullPointerException n) {
            System.out.println("ERROR: UNABLE TO GET VERSION NUMBER FROM \'POKEINFO\' THROUGH URL");
            downloadPokeInfo(true);
            readPokeInfo();
        }

        checkVersions();
    }

    // Get the contents from 'pokeInfo' through the url
    private void downloadPokeInfo(boolean writeContentsToFile) {
        ArrayList<String> jsonContents = null;

        jsonContents = pspUrlReader.readURL(pokeInfoURL);
        pspUrlReader.parseJSON(jsonContents);

        if (jsonContents == null)
            System.out.println("ERROR: URL NOT FOUND. MAKE SURE YOU HAVE AN INTERNET CONNECTION.");
        else if (writeContentsToFile)
            writeToFile("pokeInfo", jsonContents);
    }

    // Compares the two 'pokeInfo' versions
    private void checkVersions() {
        ArrayList<String> jsonContents, tempArray;

        if (pokeInfoJSONVersion.equals("debug")) {
            frame.setTitle("PokemonSearch: DEBUG MODE");
            JOptionPane.showMessageDialog(this, "PokemonSearch is in debug mode\nand will NOT download any JSON files!",
                    "WARNING: DEBUG MODE ACTIVE", JOptionPane.INFORMATION_MESSAGE);

            // If the two version match, check the region Files
        } else if (pokeInfoJSONVersion.equals(pokeInfoURLVersion)) {
            System.out.println("POKEINFO VERSION " + pokeInfoURLVersion + " == " + pokeInfoJSONVersion);
            tempArray = pspUrlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {
                try {
                    Scanner fileScan = new Scanner(new File(tempArray.get(i) + ".json"));
                    if (fileScan.nextLine().equals("")) {
                        fileScan.close();
                        throw new Exception();
                    }
                    fileScan.close();

                    // Download the region files that are not found
                } catch (Exception e) {
                    jsonContents = pspUrlReader.readURL(tempArray.get(i + 1));
                    if (pspUrlReader.isValidURL())
                        writeToFile(tempArray.get(i), jsonContents);
                }
            }

        } else {
            System.out.println("POKEINFO VERSION " + pokeInfoURLVersion + " DOES NOT EQUAL " + pokeInfoJSONVersion);
            downloadPokeInfo(true);
            tempArray = pspUrlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {
                jsonContents = pspUrlReader.readURL(tempArray.get(i + 1));
                if (pspUrlReader.isValidURL())
                    writeToFile(tempArray.get(i), jsonContents);
            }
            readPokeInfo();
        }
    }

    // Writes the given array list to a JSON
    private void writeToFile(String fileName, ArrayList<String> jsonContents) {
        System.out.println("Writing to file: " + fileName + ".json");
        try {
            FileWriter fw = new FileWriter(fileName + ".json");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < jsonContents.size() - 1; i++) {
                pw.println(jsonContents.get(i));
            }
            pw.print(jsonContents.get(jsonContents.size() - 1));
            pw.close();

        } catch (Exception e) {
            System.out.println("ERROR: FAILED TO WRITE " + fileName + ".json.");
        }
    }

    // TODO: Implement size, weight, abilities, and weakness options
    // TODO: Implement 'clear' button to search bar
    // Sets up the panels, buttons, lists, and text fields
    private void setUpPanels() {
        ArrayList<JComponent> componentList = new ArrayList<>();
        String border = "|}>----------<{::}>----------<{::}>----------<{|";
        ListModelObject tempSLM;

        // Add the search bar to the screen
        JPanel searchBarPanel = new JPanel(new GridLayout(2, 1));
        searchBarPanel.setBackground(new Color(240, 0, 0));
        searchBarPanel.add(new JLabel("POKEMON SEARCH!"));
        searchTF = new JTextField(12);
        searchBarPanel.add(searchTF);
        componentList.add(searchBarPanel);
        searchTF.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent d) {
                searchBarUpdate();
                pokeSearch.findPokemon(regionInput, typeInput, evolutionInput, input);
            }

            @Override
            public void removeUpdate(DocumentEvent d) {
                searchBarUpdate();
                pokeSearch.findPokemon(regionInput, typeInput, evolutionInput, input);
            }

            @Override
            public void changedUpdate(DocumentEvent d) {
            }
        });

        componentList.add(new JLabel(border));

        componentList.add(new JLabel("TYPE(S)"));

        // TODO: Implement check boxes
        JCheckBox checkBox1 = new JCheckBox("(WIP)"); // Find pokemon that have both selected types (max 2 selections)
        checkBox1.setBackground(new Color(240, 0, 0));
        componentList.add(checkBox1);

        JCheckBox checkBox2 = new JCheckBox("(WIP)"); // Exclude pokemon that have the selected type(s)
        checkBox2.setBackground(new Color(240, 0, 0));
        componentList.add(checkBox2);

        // Add the type list to the screen
        JScrollPane typePane = new JScrollPane(typeJL);
        typePane.setMinimumSize(new Dimension(65, 145));
        componentList.add(typePane);

        componentList.add(new JLabel(border));

        componentList.add(new JLabel("REGION(S)"));

        // Add the region list to the screen
        JScrollPane regionPane = new JScrollPane(regionJL);
        regionPane.setMinimumSize(new Dimension(100, 95));
        componentList.add(regionPane);

        componentList.add(new JLabel(border));

        // Add the evolution list to the screen
        String[] tempString = { "Only", "First", "Second", "Final" };
        tempDLM = new DefaultListModel<>();
        for (int i = 0; i < tempString.length; i++) {
            tempSLM = new ListModelObject("The " + tempString[i] + " Evolution");
            tempDLM.addElement(tempSLM);
        }
        evolutionJL = new JList<>(tempDLM);
        evolutionJL.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        evolutionJL.addListSelectionListener(this);
        componentList.add(evolutionJL);

        componentList.add(new JLabel(border));

        componentList.add(new JLabel("[Do \'Ctrl-Click\'' to multi-select or deselect items.]"));
        componentList.add(new JLabel("[Do \'Shift-Click to select all between two selections.]"));

        for (int b = 0; b < componentList.size(); b++) {
            add(componentList.get(b), Misc.setGBC(gbc, 0, b));
        }
    }

    // Attempt to search by number .....
    private boolean searchBarUpdate() {
        input = searchTF.getText().replaceAll(" ", "");

        try {
            pokeSearch.searchByNumber(Integer.parseInt(input), regionList);
            return false;

        } catch (NumberFormatException n) {
            return true;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent l) {
        if (l.getValueIsAdjusting())
            if (searchBarUpdate()) {
                // Get the selected items from 'typeJL'
                if (l.getSource() == typeJL) {
                    typeInput = new ArrayList<String>();

                    for (int t : typeJL.getSelectedIndices())
                        typeInput.add(typeList.get(t));

                    // Get the selected items from 'regionJL'
                } else if (l.getSource() == regionJL) {
                    regionInput = new ArrayList<String>();

                    for (int r : regionJL.getSelectedIndices())
                        regionInput.add(regionList.get(r * 2));

                    if (regionInput.size() == 0)
                        for (int i = 0; i < regionList.size(); i = i + 2)
                            regionInput.add(regionList.get(i));

                    // Get the selected item from 'evolutionJL'
                    // and translate it into 'evolutionInput'
                } else if (l.getSource() == evolutionJL) {
                    evolutionInput = new ArrayList<String>();

                    for (int e : evolutionJL.getSelectedIndices())
                        evolutionInput.add("" + e);
                }
                pokeSearch.findPokemon(regionInput, typeInput, evolutionInput, input);
            }
    }
}
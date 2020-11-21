
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

package source;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;

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

import source.utility.ListModelObject;
import source.utility.Misc;
import source.utility.SimpleFrame;
import source.utility.json.JSONReader;
import source.utility.json.URLReader;

/**
 * @author Nathin Wascher
 */
@SuppressWarnings("serial")
public class PokemonSearch_Panel extends JPanel implements ListSelectionListener {
    private static final String POKE_URL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";
    private static final String DIR = "resources\\";

    private static final String BORDER = "|}>----------<{::}>----------<{::}>----------<{|";

    private static final int SEARCH_BAR_WIDTH = 12;

    private static final int TYPE_PANE_WIDTH = 65;
    private static final int TYPE_PANE_HEIGHT = 145;

    private static final int REGION_PANE_WIDTH = 100;
    private static final int REGION_PANE_HEIGHT = 95;

    private static final int INITIAL_WIDTH = 1000;
    private static final int INITIAL_HEIGHT = 600;

    private SimpleFrame frame;
    private PokemonSearch_Searcher pokeSearch;
    private URLReader pspUrlReader;
    private JSONReader pspJsonReader;
    private GridBagConstraints gbc;

    private DefaultListModel<ListModelObject> tempDLM;
    private JList<ListModelObject> typeJL;
    private JList<ListModelObject> regionJL;
    private JList<ListModelObject> evolutionJL;

    private JTextField searchTF;
    private ImageIcon image;

    private ArrayList<String> typeInput;
    private ArrayList<String> regionInput;
    private ArrayList<String> evolutionInput;
    private ArrayList<String> typeList;
    private ArrayList<String> regionList;

    private String pokeJSONVersion;
    private String pokeURLVersion;
    private String input;
    private int failSafe = -1;

    /**
     * 
     */
    public PokemonSearch_Panel() {
        findImageIcon();
        frame = new SimpleFrame("PokemonSearch", "Guess That Pokemon!", INITIAL_WIDTH, INITIAL_HEIGHT, true, false,
                image);
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
        setBackground(Color.RED);

        readPokeInfo();
        setUpPanels();
        showInfoBox();

        frame.add(pokeSearch.getOutputListPane());
        frame.add(this);
        frame.setVisible(true);
    }

    /**
     * 
     */
    private void showInfoBox() {
        String message = "Copyright (c) 2020 Nathin-Dolphin";
        message = message + "\nThis file is under the MIT License";
        message = message + "\nPokemon is a registered trademark of Nintendo";
        message = message + "\n\nDescription:\nSearch for Pokemon by name, number, type, and evolution.";
        message = message + "\n\n[!] Known Issues:";
        message = message + "\n- The screen fails to load all of the pokemon when the window appears.";
        JOptionPane.showMessageDialog(this, message, "Pokemon Search v2.1", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 
     */
    private void findImageIcon() {
        java.net.URL imageURL = PokemonSearch_Panel.class.getResource("Pokeball.png");

        if (imageURL != null) {
            image = new ImageIcon(imageURL, DIR + "Pokeball");
        } else {
            System.out.println("Couldn't find file: Pokeball.png");
        }
    }

    /**
     * Reads from the locally stored file 'pokeInfo'.
     */
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
            pspJsonReader.readJSON(DIR + "pokeInfo");
            pokeJSONVersion = pspJsonReader.get("version").get(0);

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

            // TODO: Move this line to a more appropriate loaction
            PokemonObject.setRegionList(regionList);

            // Download 'pokeInfo' if the file is not found
            // or if the version number is unobtainable
        } catch (FileNotFoundException e) {
            downloadPokeInfo(true);
            readPokeInfo();
        }

        // Initially just read 'pokeInfo' from the url
        if (failSafe <= 0) {
            downloadPokeInfo(false);
        }

        // Get the version number from 'pokeInfo' through the url
        try {
            pokeURLVersion = pspUrlReader.get("version").get(0);
        } catch (NullPointerException n) {
            System.out.println("ERROR: UNABLE TO GET VERSION NUMBER FROM \'POKEINFO\' THROUGH URL");
            downloadPokeInfo(true);
            readPokeInfo();
        }

        checkVersions();
    }

    /**
     * Get the contents from 'pokeInfo' through the url.
     * 
     * @param writeContentsToFile
     */
    private void downloadPokeInfo(boolean writeContentsToFile) {
        ArrayList<String> jsonContents = null;

        jsonContents = pspUrlReader.readURL(POKE_URL);
        pspUrlReader.parseJSON(jsonContents);

        if (jsonContents == null) {
            System.out.println("ERROR: URL NOT FOUND. MAKE SURE YOU HAVE AN INTERNET CONNECTION.");
        } else if (writeContentsToFile) {
            writeToFile("pokeInfo", jsonContents);
        }
    }

    /**
     * Compares the two 'pokeInfo' versions.
     */
    private void checkVersions() {
        ArrayList<String> jsonContents;
        ArrayList<String> tempArray;

        if ("debug".equals(pokeJSONVersion)) {
            frame.setTitle("PokemonSearch: DEBUG MODE");
            JOptionPane.showMessageDialog(this, "PokemonSearch is in debug mode\nand will NOT download any JSON files!",
                    "WARNING: DEBUG MODE ACTIVE", JOptionPane.INFORMATION_MESSAGE);

            // If the two version match, check the region Files
        } else if (pokeJSONVersion.equals(pokeURLVersion)) {
            System.out.println("POKEINFO VERSION " + pokeURLVersion + " == " + pokeJSONVersion);
            tempArray = pspUrlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {
                try {
                    Scanner fileScan = new Scanner(new File(DIR + tempArray.get(i) + ".json"));
                    if (fileScan.nextLine().equals("")) {
                        fileScan.close();
                        throw new FileNotFoundException();
                    }
                    fileScan.close();

                    // Download the region files that are not found
                } catch (FileNotFoundException e) {
                    jsonContents = pspUrlReader.readURL(tempArray.get(i + 1));
                    if (pspUrlReader.isValidURL()) {
                        writeToFile(tempArray.get(i), jsonContents);
                    }
                }
            }

        } else {
            System.out.println("POKEINFO VERSION " + pokeURLVersion + " DOES NOT EQUAL " + pokeJSONVersion);
            downloadPokeInfo(true);
            tempArray = pspUrlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {
                jsonContents = pspUrlReader.readURL(tempArray.get(i + 1));
                if (pspUrlReader.isValidURL()) {
                    writeToFile(tempArray.get(i), jsonContents);
                }
            }
            readPokeInfo();
        }
    }

    /**
     * Writes the given array list to a JSON.
     * 
     * @param fileName
     * @param jsonContents
     */
    private void writeToFile(String fileName, ArrayList<String> jsonContents) {
        System.out.println("Writing to file: " + fileName + ".json");
        try {
            FileWriter fw = new FileWriter(DIR + fileName + ".json");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < jsonContents.size() - 1; i++) {
                pw.println(jsonContents.get(i));
            }
            pw.print(jsonContents.get(jsonContents.size() - 1));
            pw.close();

        } catch (IOException e) {
            System.out.println("ERROR: FAILED TO WRITE " + fileName + ".json.");
        }
    }

    // TODO: Implement size, weight, abilities, and weakness options
    // TODO: Implement 'clear' button to search bar
    /**
     * Sets up the panels, buttons, lists, and text fields.
     */
    private void setUpPanels() {
        ArrayList<JComponent> componentList = new ArrayList<>();
        ListModelObject tempSLM;

        // Add the search bar to the screen
        JPanel searchBarPanel = new JPanel(new GridLayout(2, 1));
        searchBarPanel.setBackground(Color.RED);
        searchBarPanel.add(new JLabel("POKEMON SEARCH!"));
        searchTF = new JTextField(SEARCH_BAR_WIDTH);
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

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("TYPE(S)"));

        // TODO: Implement check boxes
        JCheckBox checkBox1 = new JCheckBox("(WIP)"); // Find pokemon that have both selected types (max 2 selections)
        checkBox1.setBackground(Color.RED);
        componentList.add(checkBox1);

        JCheckBox checkBox2 = new JCheckBox("(WIP)"); // Exclude pokemon that have the selected type(s)
        checkBox2.setBackground(Color.RED);
        componentList.add(checkBox2);

        // Add the type list to the screen
        JScrollPane typePane = new JScrollPane(typeJL);
        typePane.setMinimumSize(new Dimension(TYPE_PANE_WIDTH, TYPE_PANE_HEIGHT));
        componentList.add(typePane);

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("REGION(S)"));

        // Add the region list to the screen
        JScrollPane regionPane = new JScrollPane(regionJL);
        regionPane.setMinimumSize(new Dimension(REGION_PANE_WIDTH, REGION_PANE_HEIGHT));
        componentList.add(regionPane);

        componentList.add(new JLabel(BORDER));

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

        componentList.add(new JLabel(BORDER));

        componentList.add(new JLabel("[Do \'Ctrl-Click\'' to multi-select or deselect items.]"));
        componentList.add(new JLabel("[Do \'Shift-Click to select all between two selections.]"));

        for (int b = 0; b < componentList.size(); b++) {
            add(componentList.get(b), Misc.setGBC(gbc, 0, b));
        }
    }

    /**
     * Attempt to search by number.
     * 
     * @return
     */
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
        if (l.getValueIsAdjusting()) {
            if (searchBarUpdate()) {
                // Get the selected items from 'typeJL'
                if (l.getSource() == typeJL) {
                    typeInput = new ArrayList<String>();

                    for (int t : typeJL.getSelectedIndices()) {
                        typeInput.add(typeList.get(t));
                    }

                    // Get the selected items from 'regionJL'
                } else if (l.getSource() == regionJL) {
                    regionInput = new ArrayList<String>();

                    for (int r : regionJL.getSelectedIndices()) {
                        regionInput.add(regionList.get(r * 2));
                    }

                    if (regionInput.size() == 0) {
                        for (int i = 0; i < regionList.size(); i = i + 2) {
                            regionInput.add(regionList.get(i));
                        }
                    }

                    // Get the selected item from 'evolutionJL'
                    // and translate it into 'evolutionInput'
                } else if (l.getSource() == evolutionJL) {
                    evolutionInput = new ArrayList<String>();

                    for (int e : evolutionJL.getSelectedIndices()) {
                        evolutionInput.add("" + e);
                    }
                }
                pokeSearch.findPokemon(regionInput, typeInput, evolutionInput, input);
            }
        }
    }
}


/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.json.URLReader;
import utility.json.JSONReader;

import utility.SimpleFrame;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.List;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.File;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Nathin Wascher
 */
public class PokemonSearch_Panel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 6168657140878114472L;
    private final String pokeInfoURL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";

    private SimpleFrame frame;
    private PokemonSearch_Searcher pokeSearch;
    private URLReader pspUrlReader;
    private JSONReader pspJsonReader;
    private GridBagConstraints gbc;

    private JPanel searchBarPanel;
    private List typeCL, regionCL, evolutionCL; // CL = Check List
    private JTextField searchTF;
    private JLabel searchBarLabel, regionLabel, typeLabel;
    private JButton enterB;

    private ArrayList<String> typeInput, regionInput, evolutionInput, typeList, regionList;
    private String pokeInfoJSONVersion = null, pokeInfoURLVersion = null;
    private String input;
    private int failSafe = -1;

    // Initializes the frame and some other classes
    public PokemonSearch_Panel() {
        frame = new SimpleFrame("PokemonSearch", "Guess That Pokemon!", 800, 600, false);
        frame.setLayout(new GridLayout(1, 2));

        gbc = new GridBagConstraints();
        pspJsonReader = new JSONReader();
        pspUrlReader = new URLReader();
        pokeSearch = new PokemonSearch_Searcher(pspJsonReader);

        setLayout(new GridBagLayout());
        setBackground(new Color(240, 0, 0));

        readPokeInfo();
        setUpPanels();
        showInfoBox();

        frame.add(pokeSearch.outputList);
        frame.add(this);
        frame.setVisible(true);
    }

    private void showInfoBox() {
        String message = "PokemonSearch and PokedexWriter in the OOP-Repository will no longer be updated.";
        message = message + "\nTo access the up-to-date version, download the Pokemon-Search Repository from";
        message = message + "\n\'https://github.com/Nathin-Dolphin/Pokemon-Search.git\''";
        JOptionPane.showMessageDialog(this, message, "Pokemon Search v1.4.3", JOptionPane.INFORMATION_MESSAGE);
    }

    // Reads from the locally stored file 'pokeInfo'
    private void readPokeInfo() {

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
            typeCL = new List(9, true);
            for (int i = 0; i < typeList.size(); i++)
                typeCL.add(typeList.get(i));

            regionList = pspJsonReader.get("regions");
            regionCL = new List(9, true);
            for (int i = 0; i < regionList.size(); i = i + 2)
                regionCL.add(regionList.get(i));

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
        searchBarPanel = new JPanel(new GridLayout(2, 1));

        evolutionCL = new List(4, true);
        evolutionCL.add("The Only Evolution");
        evolutionCL.add("The First Evolution");
        evolutionCL.add("The Middle Evolution");
        evolutionCL.add("The Final Evolution");

        searchBarLabel = new JLabel("   Pokemon Search!");
        regionLabel = new JLabel("Region(s)");
        typeLabel = new JLabel("Type(s)");

        searchTF = new JTextField(12);
        enterB = new JButton("Enter");

        searchTF.addActionListener(this);
        enterB.addActionListener(this);

        searchBarPanel.add(searchBarLabel);
        searchBarPanel.add(searchTF);
        searchBarPanel.setBackground(new Color(240, 0, 0));

        setGBC(0, 0);
        add(searchBarPanel, gbc);
        setGBC(0, 1);
        add(typeLabel, gbc);
        setGBC(0, 2);
        add(typeCL, gbc);
        setGBC(0, 3);
        add(regionLabel, gbc);
        setGBC(0, 4);
        add(regionCL, gbc);
        setGBC(0, 5);
        add(evolutionCL, gbc);
        setGBC(0, 6);
        add(enterB, gbc);
    }

    // Does this really need explaining
    private void setGBC(int gridX, int gridY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
    }

    public void actionPerformed(ActionEvent a) {
        regionInput = new ArrayList<String>();
        typeInput = new ArrayList<String>();
        evolutionInput = new ArrayList<String>();
        input = searchTF.getText();
        input = input.replaceAll(" ", "");

        try {
            // Attempt to search by number, if unable too, search by other means
            pokeSearch.searchByNumber(Integer.parseInt(input), regionList);

        } catch (NumberFormatException n) {
            // Get the selected items from 'regionCL'
            for (String r : regionCL.getSelectedItems())
                regionInput.add(r);
            if (regionInput.size() == 0)
                for (int i = 0; i < regionList.size(); i = i + 2)
                    regionInput.add(regionList.get(i));

            // Get the selected items from 'typeCL'
            for (String t : typeCL.getSelectedItems())
                typeInput.add(t);
            if (typeList.equals(typeInput))
                typeInput = new ArrayList<String>();

            // Get the selected item from 'evolutionCL'
            // and translate it into 'evolutionInput'
            for (String e : evolutionCL.getSelectedItems()) {
                if (e.contains("Only")) {
                    evolutionInput.add("0");

                } else if (e.contains("First")) {
                    if (!evolutionInput.contains("0"))
                        evolutionInput.add("0");
                    evolutionInput.add("1");

                } else if (e.contains("Middle")) {
                    evolutionInput.add("2");

                } else if (e.contains("Final")) {
                    evolutionInput.add("3");
                }
            }
            pokeSearch.findPokemon(regionInput, typeInput, evolutionInput, input);
        }
    }
}
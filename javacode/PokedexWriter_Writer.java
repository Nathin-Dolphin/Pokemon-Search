
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.ListModelObject;
import utility.Misc;
import utility.json.JSONReader;
import utility.json.JSONWriter;

import java.awt.Font;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.util.ArrayList;

import java.net.URL;

// TODO: Implement save button
// TODO: Properly add a method to change min and max smoothly
// TODO: Find a solution to the 'freezing button' problem

/**
 * @author Nathin Wascher
 */
@SuppressWarnings("serial")
public class PokedexWriter_Writer extends JPanel implements ActionListener {
    private final String POKEDEX_URL = "https://pokemondb.net/pokedex/national";
    private final ListModelObject ADD_POKEMON_LMO = new ListModelObject("Add New Pokemon");

    // Have these get automatically updated by 'pokeInfo'
    private final int NAME = 0, TYPE = 4, EVOLUTION = 6, OBJECT_LENGTH = 8;
    // private final int NUMBER = 2;

    private ArrayList<ArrayList<String>> pokedexEntries;
    private ArrayList<String> tempPokedexEntry, urlContents, urlRegionList, jsonContents;
    private String previousEvoSet, createFileString, regionName;
    private int currentEvoNum, urlContentsIndex, evolutionPos, jsonIndex, outputListIndex;
    private boolean modifyPokedex = false, customEvoNum = false;

    private JSONReader pwwJsonReader;
    private DefaultListModel<ListModelObject> outputListModel;
    private JList<ListModelObject> outputJList;
    private JScrollBar outputScrollBar;

    public JScrollPane outputListPane;
    public JSONWriter pwwJsonWriter;

    public JTextField maxJTF, minJTF, nameJTF, evoNumJTF;
    public JButton enterJB, nextEvoNumJB;

    public JList<ListModelObject> type1JList, type2JList, evolutionJList;
    public ArrayList<String> regionList, typeList, evolutionStates;
    public int min, max, pokeNum;

    // Initialize a bunch of stuff
    public PokedexWriter_Writer() {
        pwwJsonReader = new JSONReader();
        pokedexEntries = new ArrayList<ArrayList<String>>();
        evolutionStates = new ArrayList<String>();
        urlContents = new ArrayList<String>();
        jsonContents = new ArrayList<String>();
        outputListModel = new DefaultListModel<>();

        outputListModel.addElement(ADD_POKEMON_LMO);
        outputJList = new JList<>(outputListModel);
        outputJList.setFont(new Font("Monospaced", Font.BOLD, 10));
        outputJList.setFixedCellWidth(300);
        outputJList.setVisibleRowCount(25);
        outputJList.setSelectedIndex(0);

        // TODO: Make pane automatically scroll down to the bottom (mostly works)
        outputListPane = new JScrollPane(outputJList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outputScrollBar = outputListPane.getVerticalScrollBar();

        evolutionStates.add("Only");
        evolutionStates.add("First");
        evolutionStates.add("Middle");
        evolutionStates.add("Final");

        outputListIndex = -1;
        urlContentsIndex = -1;
        evolutionPos = 1;
        currentEvoNum = 0;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
        createFileString = "Create \'" + regionName + ".json\' File";
    }

    // Read the JSON that the user wants to modify
    public void modifyFile() {
        jsonIndex = 0;

        modifyPokedex = true;
        getMinMax();

        try {
            pwwJsonReader.readJSON(regionName);
            jsonContents = pwwJsonReader.get(regionName);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: FILE NOT FOUND");
            System.exit(0);
        }

        modifyPokemonInfo();
    }

    public void openURL() {
        urlRegionList = new ArrayList<String>();
        String tempString;
        boolean stopSearch = false;

        // TODO: This line needs to be updated when new pokemon are introduced
        for (int i = 0; i < 8; i++)
            urlRegionList.add("id=\"gen-" + (i + 1) + "\"");

        int h = getMinMax();

        if (h != -1) {
            try {
                System.out.println("\nREADING URL: " + POKEDEX_URL);
                URL openURL = new URL(POKEDEX_URL);
                BufferedReader br = new BufferedReader(new InputStreamReader(openURL.openStream()));

                while ((tempString = br.readLine()) != null & !stopSearch) {
                    if (tempString.contains(urlRegionList.get(h))) {
                        tempString = br.readLine();

                        while (!tempString.equals("</div>")) {
                            urlContents.add(tempString);
                            tempString = br.readLine();
                        }
                        stopSearch = true;
                    }
                }
                urlContentsIndex = 1;
                getPokemonInfoFromURL();
                br.close();
            } catch (Exception e) {
                System.out.println("ERROR: FAILED TO LOAD URL " + POKEDEX_URL);
            }
        }
    }

    // Finds the min and max for the region
    private int getMinMax() {
        String[] tempArray;
        int h = -1;

        for (int d = 0; d < regionList.size(); d = d + 2) {
            if (regionList.get(d).equalsIgnoreCase(regionName)) {
                tempArray = regionList.get(d - 1).split("-");

                if (d == 0) {
                    min = 1;

                } else {
                    min = Integer.parseInt(tempArray[0]) + 1;
                    currentEvoNum = Integer.parseInt(tempArray[1]) + 1;
                    evoNumJTF.setText(addZeros(currentEvoNum));
                }

                pokeNum = min;
                max = Integer.parseInt(regionList.get(d + 1).split("-")[0]);
                maxJTF.setText("" + max);
                minJTF.setText("" + min);

                h = d / 2;
                d = regionList.size();
            }
        }
        return h;
    }

    private void getEvolutionSet() {
        int tempInt;

        for (int i = 0; i < evolutionStates.size(); i++)
            if (evolutionJList.isSelectedIndex(i))
                evolutionPos = i;

        try {
            tempInt = Integer.parseInt(evoNumJTF.getText());

            if (tempInt != currentEvoNum & !customEvoNum)
                --currentEvoNum;
            customEvoNum = true;

        } catch (NumberFormatException n) {
            evoNumJTF.setText(addZeros(currentEvoNum));
            Misc.warningBox(this, "THE EVOLUTION NUMBER TEXT FIELD MUST CONTAIN AN INTEGER");
        }
    }

    private void setEvolutionState() {
        if (evolutionPos == 1)
            evolutionJList.setSelectedIndex(2);
        else if (evolutionPos == 2)
            evolutionJList.setSelectedIndex(3);
        else {
            evoNumJTF.setText(addZeros(++currentEvoNum));
            evolutionJList.setSelectedIndex(1);
            customEvoNum = false;
        }
    }

    // Takes the user inputs and turns it into a single string
    private String setPokemon(int tempPokeNum) {
        tempPokedexEntry = new ArrayList<String>();
        String tempString;
        int tempInt;

        // Set the name for the pokemon
        tempPokedexEntry.add("name");
        tempString = Misc.capitalize(nameJTF.getText().replaceAll(" ", ""));
        tempPokedexEntry.add(tempString);

        // Set the number for the pokemon
        tempPokedexEntry.add("number");
        if (tempPokeNum < 10)
            tempPokedexEntry.add("000" + tempPokeNum);
        else if (tempPokeNum < 100)
            tempPokedexEntry.add("00" + tempPokeNum);
        else if (tempPokeNum < 1000)
            tempPokedexEntry.add("0" + tempPokeNum);
        else
            tempPokedexEntry.add("" + tempPokeNum);

        // Set the type(s) for the pokemon
        tempPokedexEntry.add("type");

        tempInt = type1JList.getSelectedIndex();
        tempString = Misc.capitalize(typeList.get(tempInt));

        tempInt = type2JList.getSelectedIndex();
        if (tempInt > 0)
            tempString = tempString + "-" + Misc.capitalize(typeList.get(tempInt - 1));
        tempPokedexEntry.add(tempString);

        // Set the evolution string for the pokemon
        tempPokedexEntry.add("evolution");
        getEvolutionSet();
        tempString = evoNumJTF.getText();
        tempPokedexEntry.add(addZeros(tempString) + "-" + evolutionPos);
        setEvolutionState();

        // Combine the previous information into one string
        tempString = "Evo:" + tempPokedexEntry.get(7); // Adds the evolution set
        tempString = tempString + "   #" + tempPokedexEntry.get(3); // Adds the pokemon number
        tempString = tempString + "   " + tempPokedexEntry.get(1); // Adds the name
        tempString = tempString + "     " + tempPokedexEntry.get(5); // Adds the type(s)
        return tempString;
    }

    // Gather the name and type(s) for a pokemon from the URL's contents
    private void getPokemonInfoFromURL() {
        String[] tempArray = urlContents.get(urlContentsIndex++).split("\"");
        boolean type1Set = false;

        for (String s : tempArray) {
            // Find the name of the pokemon
            if (s.contains("/pokedex/")) {
                s = s.replace("/pokedex/", "");
                nameJTF.setText(s);

                // Find the type(s) of the pokemon
            } else if (s.contains("/type/")) {
                s = s.replace("/type/", "");
                for (int k = 0; k < typeList.size(); k++) {
                    if (s.equals(typeList.get(k)) & !type1Set) {
                        type1JList.setSelectedIndex(k);
                        type1Set = true;
                        type2JList.setSelectedIndex(0);

                    } else if (s.equals(typeList.get(k)))
                        type2JList.setSelectedIndex(k + 1);
                }
            }
        }
    }

    private void modifyPokemonInfo() {
        String[] tempArray;
        int tempInt = (OBJECT_LENGTH * jsonIndex++ + 1);

        // Set the pokemon's name to the screen
        nameJTF.setText(jsonContents.get(NAME + tempInt));

        // TODO: Add pokemon's number to the screen from the json???

        // Set the pokemon's type(s) to the screen
        type2JList.setSelectedIndex(0);
        tempArray = jsonContents.get(TYPE + tempInt).split("-");
        for (int g = 0; g < tempArray.length; g++)
            for (int type = 0; type < typeList.size(); type++)
                if (typeList.get(type).equalsIgnoreCase(tempArray[g]))
                    if (g == 0)
                        type1JList.setSelectedIndex(type);
                    else
                        type2JList.setSelectedIndex(type + 1);

        // Set the Pokemon's evolution set to the screen
        tempArray = jsonContents.get(EVOLUTION + tempInt).split("-");
        evoNumJTF.setText(addZeros(Integer.parseInt(tempArray[0])));
        evolutionJList.setSelectedIndex(Integer.parseInt(tempArray[1]));
    }

    // Add the newly created pokemon to the screen and the pokedex array
    private void addNewPokemon() {
        String tempString = setPokemon(pokeNum++);

        outputListModel.remove(outputListModel.size() - 1);
        outputListModel.addElement(new ListModelObject(tempString));
        outputListModel.addElement(ADD_POKEMON_LMO);
        outputJList.setModel(outputListModel);
        outputJList.setSelectedIndex(outputListModel.size() - 1);
        pokedexEntries.add(tempPokedexEntry);

        if (urlContentsIndex >= 0 & urlContentsIndex < urlContents.size())
            getPokemonInfoFromURL();

        // Second condition is to prevent IndexOutOfBounds Error
        if (modifyPokedex & (jsonIndex * OBJECT_LENGTH + 1) < jsonContents.size())
            modifyPokemonInfo();

        if (pokeNum == max + 1) {
            outputListModel.remove(outputListModel.size() - 1);
            System.out.println(createFileString);
            outputListModel.addElement(new ListModelObject(createFileString));
            outputJList.setModel(outputListModel);
        }
    }

    // Change the pokemon to the new stats
    private void changePokemon() {
        if (outputJList.getSelectedIndex() == outputListIndex) {
            String tempString = setPokemon(outputListIndex + min - 1);

            outputListModel.remove(outputListIndex);
            outputListModel.add(outputListIndex, new ListModelObject(tempString));
            outputJList.setModel(outputListModel);
            outputJList.setSelectedIndex(outputListModel.size() - 1);

            pokedexEntries.remove(outputListIndex - 1);
            pokedexEntries.add(outputListIndex - 1, tempPokedexEntry);

            tempString = previousEvoSet.split("-")[0];
            evoNumJTF.setText(addZeros(tempString));
            tempString = previousEvoSet.split("-")[1];
            evolutionJList.setSelectedIndex(Integer.parseInt(tempString));

            if (modifyPokedex & (jsonIndex * OBJECT_LENGTH + 1) < jsonContents.size()) {
                jsonIndex--;
                modifyPokemonInfo();
            }

            if (urlContentsIndex >= 0 & urlContentsIndex < urlContents.size()) {
                urlContentsIndex--;
                getPokemonInfoFromURL();
            }

            outputListIndex = -1;

        } else {
            for (int i = 0; i < evolutionStates.size(); i++)
                if (evolutionJList.isSelectedIndex(i))
                    previousEvoSet = evoNumJTF.getText() + "-" + i;

            outputListIndex = outputJList.getSelectedIndex();
            try {
                tempPokedexEntry = pokedexEntries.get(outputListIndex - 1);
            } catch (IndexOutOfBoundsException i) {
                System.out.println("ERROR: ATTEMPTING TO CHANGE AN INVALID SELECTION");
            }

            getPokemonInfo();
        }
    }

    private void getPokemonInfo() {
        String[] tempArray;

        nameJTF.setText(tempPokedexEntry.get(1));

        tempArray = tempPokedexEntry.get(5).split("-");
        for (int t = 0; t < typeList.size(); t++)
            if (tempArray[0].equalsIgnoreCase(typeList.get(t)))
                type1JList.setSelectedIndex(t);

        type2JList.setSelectedIndex(0);
        if (tempArray.length > 1)
            for (int t = 0; t < typeList.size(); t++)
                if (tempArray[1].equalsIgnoreCase(typeList.get(t)))
                    type2JList.setSelectedIndex(t + 1);

        tempArray = tempPokedexEntry.get(7).split("-");
        evoNumJTF.setText(addZeros(tempArray[0]));
        evolutionJList.setSelectedIndex(Integer.parseInt(tempArray[1]));
    }

    // Create a JSON and write the contents of the pokedex array to it
    private void createFile() {
        System.out.println(" [!] Creating File: \"" + regionName + ".json\"");

        pwwJsonWriter.newArray(regionName);
        for (ArrayList<String> s : pokedexEntries) {
            pwwJsonWriter.newObject(s);
        }
        pwwJsonWriter.endArray();
        pwwJsonWriter.closeFile();

        JOptionPane.showMessageDialog(this, regionName + ".json has been succesfully created.", "Pokedex Complete!",
                JOptionPane.INFORMATION_MESSAGE);
        System.out.println("File Successfully Created!");
    }

    private String addZeros(String n) {
        return addZeros(Integer.parseInt(n));
    }

    // Add zeros to 'evoNum'
    private String addZeros(int n) {
        String tempString;

        if (n < 10)
            tempString = "00" + n;
        else if (n < 100)
            tempString = "0" + n;
        else
            tempString = "" + n;

        return tempString;
    }

    public void actionPerformed(ActionEvent e) {
        String tempString;
        outputScrollBar.setValue(outputScrollBar.getMaximum());

        if (outputJList.getSelectedIndex() != -1) {
            if (e.getSource() == enterJB || e.getSource() == nameJTF) {
                ListModelObject temp = outputListModel.get(outputJList.getSelectedIndex());

                if (temp.toString().equals(createFileString)) {
                    tempString = "Successfully created \'" + regionName + ".json\'";
                    outputListModel.remove(outputListModel.size() - 1);
                    outputListModel.addElement(new ListModelObject(tempString));
                    outputJList.setModel(outputListModel);

                    pokeNum++;
                    createFile();

                } else if (pokeNum == max + 2) {
                    Misc.warningBox(this, "File Already Created!");

                } else if (nameJTF.getText().replaceAll(" ", "").equals("")) {
                    Misc.warningBox(this, "MISSING NAME");

                } else if (type1JList.toString().equals(type2JList.toString())) {
                    Misc.warningBox(this, "PRIMARY TYPE CAN NOT EQUAL SECONDARY TYPE");

                    // If 'Add New Pokemon' is selected
                } else if (temp.toString().equals(ADD_POKEMON_LMO.toString())) {
                    addNewPokemon();

                    // If an already existing pokemon is selected, then modify
                    // the pokemon on the screen and in the pokedex array
                } else {
                    changePokemon();
                }
                // If the user presses nextEvoNum Button
            } else if (e.getSource() == nextEvoNumJB) {
                evoNumJTF.setText(addZeros(++currentEvoNum));
            }
        } else
            Misc.warningBox(this, "NO ITEM SELECTED");
    }
}
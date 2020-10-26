
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.json.JSONWriter;
import utility.json.JSONReader;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.List;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.util.ArrayList;

// TODO: Implement save button
// TODO: Properly add a method to change min and max smoothly
// TODO: Find a solution to the 'freezing button' problem

/**
 * @author Nathin Wascher
 */
public class PokedexWriter_Writer extends JPanel implements ActionListener {
    private static final long serialVersionUID = 2911032048461996161L;
    private final String pokedexSourceURL = "https://pokemondb.net/pokedex/national";

    // Have these get automatically updated by 'pokeInfo'
    private final int NAME = 0, TYPE = 4, EVOLUTION = 6, OBJECT_LENGTH = 8;
    // private final int NUMBER = 2;

    private ArrayList<ArrayList<String>> pokedexEntries;
    private ArrayList<String> tempPokedexEntry, urlContents, urlRegionList, jsonContents;
    private String previousEvoSet;
    private int currentEvoNum, urlContentsIndex, evolutionPos, jsonIndex, outputListIndex;
    private boolean modifyPokedex = false, customEvoNum = false;

    private JSONReader pwwJsonReader;
    public JSONWriter pwwJsonWriter;

    public JTextField maxJTF, minJTF, nameJTF, evoNumJTF;
    public List type1CL, type2CL, outputList, evolutionCL; // CL = Check List
    public JButton enterJB, nextEvoNumJB;

    public ArrayList<String> regionList, typeList, evolutionStates;
    public String regionName;
    public int min, max, pokeNum;

    // Initialize a bunch of stuff
    public PokedexWriter_Writer() {
        pwwJsonReader = new JSONReader();
        pokedexEntries = new ArrayList<ArrayList<String>>();
        evolutionStates = new ArrayList<String>();
        urlContents = new ArrayList<String>();
        jsonContents = new ArrayList<String>();
        outputList = new List(40);

        outputList.add("!<<<<<>>>>>!!<<<<<>>>>>!!<<<<<>>>>>!!<<<<<>>>>>!");
        outputList.add("Add New Pokemon!");
        outputList.addActionListener(this);
        outputList.select(1);

        evolutionStates.add("Only");
        evolutionStates.add("First");
        evolutionStates.add("Middle");
        evolutionStates.add("Final");

        outputListIndex = -1;
        urlContentsIndex = -1;
        evolutionPos = 1;
        currentEvoNum = 0;
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

        // This line needs to be updated when new pokemon are introduced
        for (int i = 0; i < 8; i++)
            urlRegionList.add("id=\"gen-" + (i + 1) + "\"");

        int h = getMinMax();

        if (h != -1) {
            try {
                System.out.println("\nREADING URL: " + pokedexSourceURL);
                URL openURL = new URL(pokedexSourceURL);
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
                System.out.println("ERROR: FAILED TO LOAD URL " + pokedexSourceURL);
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

                    // Update this???
                    if (currentEvoNum > 99)
                        evoNumJTF.setText("" + currentEvoNum);
                    else
                        evoNumJTF.setText("0" + currentEvoNum);
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
            if (evolutionCL.isIndexSelected(i))
                evolutionPos = i;

        try {
            tempInt = Integer.parseInt(evoNumJTF.getText());

            if (tempInt != currentEvoNum & !customEvoNum)
                --currentEvoNum;
            customEvoNum = true;

        } catch (NumberFormatException n) {
            evoNumJTF.setText(addZeros(currentEvoNum));
            warning("THE EVOLUTION NUMBER TEXT FIELD MUST CONTAIN AN INTEGER");
        }
    }

    private void setEvolutionState() {
        if (evolutionPos == 1)
            evolutionCL.select(2);
        else if (evolutionPos == 2)
            evolutionCL.select(3);
        else {
            evoNumJTF.setText(addZeros(++currentEvoNum));
            evolutionCL.select(1);
            customEvoNum = false;
        }
    }

    // TODO: Make 'outputList' easier to read
    // Takes the user inputs and turns it into a single string
    private String setPokemon(int tempPokeNum) {
        tempPokedexEntry = new ArrayList<String>();
        String tempString;

        // Set the name for the pokemon
        tempPokedexEntry.add("name");
        tempString = capitalize(nameJTF.getText().replaceAll(" ", ""));
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
        tempString = capitalize(type1CL.getSelectedItem());
        if (!type2CL.getSelectedItem().equals("none"))
            tempString = tempString + "-" + capitalize(type2CL.getSelectedItem());
        tempPokedexEntry.add(tempString);

        // Set the evolution string for the pokemon
        tempPokedexEntry.add("evolution");
        getEvolutionSet();
        tempString = evoNumJTF.getText();
        tempPokedexEntry.add(addZeros(tempString) + "-" + evolutionPos);
        setEvolutionState();

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
                        type1CL.select(k);
                        type1Set = true;
                        type2CL.select(0);

                    } else if (s.equals(typeList.get(k)))
                        type2CL.select(k + 1);
                }
            }
        }
    }

    private void modifyPokemonInfo() {
        String[] tempArray;
        int tempInt = (OBJECT_LENGTH * jsonIndex++ + 1);

        // Set the pokemon's name to the screen
        nameJTF.setText(jsonContents.get(NAME + tempInt));

        // TODO: Add pokemon's number to the screen???

        // Set the pokemon's type(s) to the screen
        type2CL.select(0);
        tempArray = jsonContents.get(TYPE + tempInt).split("-");
        for (int g = 0; g < tempArray.length; g++)
            for (int type = 0; type < typeList.size(); type++)
                if (typeList.get(type).equalsIgnoreCase(tempArray[g]))
                    if (g == 0)
                        type1CL.select(type);
                    else
                        type2CL.select(type + 1);

        // Set the Pokemon's evolution set to the screen
        // TODO: Set the evolution set entirely from 'jsonContents'
        // tempArray = jsonContents.get(EVOLUTION + tempInt).split("-");
        // evoNumJTF.setText(addZeros(Integer.parseInt(tempArray[0])));
        // evolutionCL.select(Integer.parseInt(tempArray[1]));
    }

    // Add the newly created pokemon to the screen and the pokedex array
    private void addNewPokemon() {
        String tempString = setPokemon(pokeNum++);
        outputList.add(tempString, outputList.getItemCount() - 1);
        outputList.select(outputList.getItemCount() - 1);
        pokedexEntries.add(tempPokedexEntry);

        if (urlContentsIndex >= 0 & urlContentsIndex < urlContents.size())
            getPokemonInfoFromURL();

        // Second condition is to prevent IndexOutOfBounds Error
        if (modifyPokedex & (jsonIndex * OBJECT_LENGTH + 1) < jsonContents.size())
            modifyPokemonInfo();

        // TODO: Make it more noticable that a file is created
        if (pokeNum == max + 1) {
            // String tempString = "Create " + regionName + ".json File";
            // outputList.add(tempString, outputList.getItemCount() - 1);
            createFile();
        }
    }

    // Change the pokemon to the new stats
    private void changePokemon() {
        if (outputList.getSelectedIndex() == outputListIndex) {
            String tempString = setPokemon(outputListIndex + min - 1);

            outputList.remove(outputListIndex);
            outputList.add(tempString, outputListIndex);
            outputList.select(outputList.getItemCount() - 1);

            pokedexEntries.remove(outputListIndex - 1);
            pokedexEntries.add(outputListIndex - 1, tempPokedexEntry);

            tempString = previousEvoSet.split("-")[0];
            evoNumJTF.setText(addZeros(tempString));
            tempString = previousEvoSet.split("-")[1];
            evolutionCL.select(Integer.parseInt(tempString));

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
                if (evolutionCL.isIndexSelected(i))
                    previousEvoSet = evoNumJTF.getText() + "-" + i;

            outputListIndex = outputList.getSelectedIndex();
            tempPokedexEntry = pokedexEntries.get(outputListIndex - 1);
            getPokemonInfo();
        }
    }

    private void getPokemonInfo() {
        String[] tempArray;

        nameJTF.setText(tempPokedexEntry.get(1));

        tempArray = tempPokedexEntry.get(5).split("-");
        for (int t = 0; t < typeList.size(); t++)
            if (tempArray[0].equalsIgnoreCase(typeList.get(t)))
                type1CL.select(t);

        type2CL.select(0);
        if (tempArray.length > 1)
            for (int t = 0; t < typeList.size(); t++)
                if (tempArray[1].equalsIgnoreCase(typeList.get(t)))
                    type2CL.select(t + 1);

        tempArray = tempPokedexEntry.get(7).split("-");
        evoNumJTF.setText(addZeros(tempArray[0]));
        evolutionCL.select(Integer.parseInt(tempArray[1]));
    }

    // Create a JSON and write the contents of the pokedex array to it
    private void createFile() {
        outputList.remove(outputList.getItemCount() - 1);

        // TODO: Have this do a pop-up box too
        System.out.println("Creating File: \"" + regionName + ".json\"");

        pwwJsonWriter.newArray(regionName);
        for (ArrayList<String> s : pokedexEntries) {
            pwwJsonWriter.newObject(s);
        }
        pwwJsonWriter.endArray();
        pwwJsonWriter.closeFile();

        System.out.println("File Successfully Created!");
    }

    // TODO: Rework this to be a more general purpose pop-up box
    private void warning(String s) {
        JOptionPane.showConfirmDialog(this, "WARNING: " + s, "WARNING MESSAGE! (WIP)", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
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

    // Capitalize the string
    private String capitalize(String input) {
        String tempString;
        try {
            tempString = input.substring(0, 1).toUpperCase();
            tempString = tempString.concat(input.substring(1, input.length()));
            return tempString;
        } catch (StringIndexOutOfBoundsException e) {
            return "!<<<<<NULL>>>>>!";
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterJB || e.getSource() == nameJTF) {
            if (pokeNum == max + 1) {
                warning("MAX NUMBER REACHED");

                // If the top line or no line is selected
            } else if (outputList.getSelectedIndex() <= 0) {
                outputList.select(outputList.getItemCount() - 1);
                warning("MissingNo has Appeared!");

                // Is the name blank
            } else if (nameJTF.getText().replaceAll(" ", "").equals("")) {
                warning("MISSING NAME");

                // Are both of the selected items in the type Lists the same
            } else if (type1CL.getSelectedItem().equals(type2CL.getSelectedItem())) {
                warning("TYPE 1 CAN NOT EQUAL TYPE 2");

                // Is user creating a new pokemon
            } else if (outputList.getSelectedItem().equals("Add New Pokemon!")) {
                addNewPokemon();

                // If an already existing pokemon is selected, then modify
                // the pokemon on the screen and in the pokedex array
            } else {
                changePokemon();
            }
            // When user presses nextEvoNum Button
        } else if (e.getSource() == nextEvoNumJB) {
            evoNumJTF.setText(addZeros(++currentEvoNum));
        }
    }
}
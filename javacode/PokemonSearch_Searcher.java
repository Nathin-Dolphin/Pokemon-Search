
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.json.JSONReader;

// import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JScrollPane;
import javax.swing.JList;

import java.io.FileNotFoundException;

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 */
public class PokemonSearch_Searcher {
    private final int NAME = 0, NUMBER = 2, TYPE = 4, EVOLUTION = 6, OBJECT_LENGTH = 8;

    private JSONReader pssJsonReader;
    private ArrayList<String> pokedex, tempPokedex;

    private DefaultListModel<PokemonSearch_Pokemon> listModel;
    private JList<PokemonSearch_Pokemon> outputJList;

    public JScrollPane outputListPane;

    /**
     * 
     * @param jsonReader
     */
    public PokemonSearch_Searcher(JSONReader jsonReader) {
        this.pssJsonReader = jsonReader;
        listModel = new DefaultListModel<>();
        outputJList = new JList<>(listModel);
        outputListPane = new JScrollPane(outputJList);
    }

    /**
     * 
     * @param input
     * @param regionList
     */
    public void searchByNumber(int input, ArrayList<String> regionList) {
        searchByNumber(input, regionList, false);
    }

    /**
     * 
     * @param input
     * @param regionList
     * @param findEvolutionSetNum
     */
    private void searchByNumber(int input, ArrayList<String> regionList, boolean findEvolutionSetNum) {
        PokemonSearch_Pokemon tempPSP;
        String[] tempArray;
        String tempString;
        int tempInt, arrayPos = 1;
        pokedex = new ArrayList<String>();

        if (!findEvolutionSetNum) {
            outputJList.removeAll();
            arrayPos = 0;
        }

        for (int i = 1; i < regionList.size(); i = i + 2) {
            tempArray = regionList.get(i).split("-");
            tempInt = Integer.parseInt(tempArray[arrayPos]);

            if (input <= tempInt) {
                tempString = regionList.get(i - 1);

                try {
                    pssJsonReader.readJSON(tempString + ".json");
                    pokedex.addAll(pssJsonReader.get(tempString));
                } catch (FileNotFoundException e) {
                    System.out.println("ERROR: UNABLE TO FIND \"" + tempString + ".json\".");
                }
                if (!findEvolutionSetNum)
                    i = regionList.size();
            }
        }

        // TODO: Implement binary sorting
        tempPokedex = new ArrayList<String>();
        if (!findEvolutionSetNum) {
            listModel.removeAllElements();

            // Add pokemon by number to the model list
            for (int g = NUMBER; g < pokedex.size(); g = g + OBJECT_LENGTH) {
                tempInt = Integer.parseInt(pokedex.get(g + 1));

                if (input == tempInt) {
                    tempPSP = new PokemonSearch_Pokemon();
                    tempPSP.processInfo(pokedex, g - NUMBER);
                    listModel.addElement(tempPSP);
                    g = pokedex.size();
                }
            }
            searchByNumber(input, regionList, true);
            outputJList.setModel(listModel);

        } else {
            // Add pokemon by evolution to the model list
            for (int g = EVOLUTION; g < pokedex.size(); g = g + OBJECT_LENGTH) {
                tempInt = Integer.parseInt(pokedex.get(g + 1).split("-")[0]);

                if (input == tempInt) {
                    tempPSP = new PokemonSearch_Pokemon();
                    tempPSP.processInfo(pokedex, g - EVOLUTION);
                    listModel.addElement(tempPSP);
                }
            }
        }
    }

    /**
     * 
     * @param regionInput
     * @param typeInput
     * @param evolutionInput
     * @param input
     */
    public void findPokemon(ArrayList<String> regionInput, ArrayList<String> typeInput,
            ArrayList<String> evolutionInput, String input) {
        PokemonSearch_Pokemon tempPSP;
        String tempString;
        outputJList.removeAll();
        listModel.removeAllElements();

        long startTime = System.nanoTime();

        searchByRegion(regionInput);

        if (evolutionInput.size() != 0)
            searchByEvolution(evolutionInput);

        if (typeInput.size() != 0)
            searchByType(typeInput);

        if (!input.equals(""))
            searchByName(input);

        // Add pokemon by name to the model list
        for (int f = NAME; f < pokedex.size(); f = f + OBJECT_LENGTH) {
            tempString = pokedex.get(f);

            if (tempString.equals("name")) {
                tempPSP = new PokemonSearch_Pokemon();
                tempPSP.processInfo(pokedex, f + NAME);
                listModel.addElement(tempPSP);
            }
        }
        outputJList.setModel(listModel);
        System.out
                .println("\nElapsed Time: " + (double) ((System.nanoTime() - startTime) / 1000000) / 1000 + " Seconds");
    }

    /**
     * 
     * @param regionInput
     */
    private void searchByRegion(ArrayList<String> regionInput) {
        pokedex = new ArrayList<String>();

        for (String s : regionInput)
            try {
                pssJsonReader.readJSON(s + ".json");
                pokedex.addAll(pssJsonReader.get(s));
            } catch (Exception e) {
                System.out.println("ERROR: FILE \"" + s + ".json\" WAS NOT FOUND OR IS EMPTY");
            }
    }

    /**
     * 
     * @param evolutionInput
     */
    private void searchByEvolution(ArrayList<String> evolutionInput) {
        String[] tempArray;
        tempPokedex = pokedex;
        pokedex = new ArrayList<String>();

        for (int g = EVOLUTION; g < tempPokedex.size(); g = g + OBJECT_LENGTH) {
            tempArray = tempPokedex.get(g + 1).split("-");

            for (String s : evolutionInput)
                if (s.equals(tempArray[1]))
                    for (int h = 0; h < 8; h++)
                        pokedex.add(tempPokedex.get(h + g - EVOLUTION));
        }
    }

    /**
     * 
     * @param typeInput
     */
    private void searchByType(ArrayList<String> typeInput) {
        String[] tempArray;
        tempPokedex = pokedex;
        pokedex = new ArrayList<String>();

        for (int g = TYPE; g < tempPokedex.size(); g = g + OBJECT_LENGTH) {
            tempArray = tempPokedex.get(g + 1).split("-");

            for (int n = 0; n < tempArray.length; n++)
                for (int d = 0; d < typeInput.size(); d++)
                    if (tempArray[n].equalsIgnoreCase(typeInput.get(d)))
                        for (int h = 0; h < 8; h++) {
                            pokedex.add(tempPokedex.get(g + h - TYPE));
                            n = 3;
                            d = typeInput.size();
                        }
        }
    }

    /**
     * 
     * @param input
     */
    private void searchByName(String input) {
        String tempString;
        tempPokedex = pokedex;
        pokedex = new ArrayList<String>();

        for (int g = NAME; g < tempPokedex.size(); g = g + OBJECT_LENGTH) {
            tempString = tempPokedex.get(g + 1);

            for (int n = 0; n + input.length() <= tempString.length(); n++)
                if (tempString.substring(n, n + input.length()).equalsIgnoreCase(input)) {
                    n = tempString.length();

                    for (int h = 0; h < 8; h++)
                        pokedex.add(tempPokedex.get(g + h - NAME));
                }
        }
    }

    // private void pokemonInformationPane() {}
}
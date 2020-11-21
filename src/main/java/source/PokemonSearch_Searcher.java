
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

package source;

import java.awt.Font;

import java.io.FileNotFoundException;

import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import source.utility.json.JSONReader;

/**
 * @author Nathin Wascher
 */
public class PokemonSearch_Searcher implements ListSelectionListener {
    private static final String DIR = "resources\\";

    // TODO: Have these values get calaculated from pokeInfo.json
    private static final int NAME = 0;
    private static final int NUMBER = 2;
    private static final int TYPE = 4;
    private static final int EVOLUTION = 6;
    private static final int OBJECT_LENGTH = 8;

    private JSONReader pssJsonReader;
    private DefaultListModel<PokemonObject> listModel;
    private JList<PokemonObject> outputJList;

    private JScrollPane outputListPane;

    private ArrayList<String> pokedex;
    private ArrayList<String> tempPokedex;

    /**
     * 
     * @param jsonReader
     */
    public PokemonSearch_Searcher(JSONReader jsonReader) {
        this.pssJsonReader = jsonReader;
        listModel = new DefaultListModel<>();
        outputJList = new JList<>(listModel);
        outputJList.addListSelectionListener(this);
        outputJList.setFont(new Font("Monospaced", Font.BOLD, 10));
        outputListPane = new JScrollPane(outputJList);
    }

    public JScrollPane getOutputListPane() {
        return outputListPane;
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
        PokemonObject tempPSP;
        String[] tempArray;
        String tempString;
        int tempInt;
        int arrayPos = 1;
        pokedex = new ArrayList<String>();

        if (!findEvolutionSetNum) {
            outputJList.removeAll();
            arrayPos = 0;
        }

        breakpoint: for (int i = 1; i < regionList.size(); i = i + 2) {
            tempArray = regionList.get(i).split("-");
            tempInt = Integer.parseInt(tempArray[arrayPos]);

            if (input <= tempInt) {
                tempString = regionList.get(i - 1);

                try {
                    pssJsonReader.readJSON(DIR + tempString + ".json");
                    pokedex.addAll(pssJsonReader.get(tempString));
                } catch (FileNotFoundException e) {
                    System.out.println("ERROR: UNABLE TO FIND \"" + tempString + ".json\".");
                }
                if (!findEvolutionSetNum) {
                    break breakpoint;
                }
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
                    tempPSP = new PokemonObject();
                    tempPSP.processInfo(pokedex, g - NUMBER);
                    listModel.addElement(tempPSP);
                    break;
                }
            }
            searchByNumber(input, regionList, true);
            outputJList.setModel(listModel);

        } else {
            // Add pokemon by evolution to the model list
            for (int g = EVOLUTION; g < pokedex.size(); g = g + OBJECT_LENGTH) {
                tempInt = Integer.parseInt(pokedex.get(g + 1).split("-")[0]);

                if (input == tempInt) {
                    tempPSP = new PokemonObject();
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
        PokemonObject tempPSP;
        String tempString;
        outputJList.removeAll();
        listModel.removeAllElements();

        searchByRegion(regionInput);

        if (evolutionInput.size() != 0) {
            searchByEvolution(evolutionInput);
        }
        if (typeInput.size() != 0) {
            searchByType(typeInput);
        }
        if (!"".equals(input)) {
            searchByName(input);
        }

        // Add pokemon by name to the model list
        for (int f = NAME; f < pokedex.size(); f = f + OBJECT_LENGTH) {
            tempString = pokedex.get(f);

            if ("name".equals(tempString)) {
                tempPSP = new PokemonObject();
                tempPSP.processInfo(pokedex, f + NAME);
                listModel.addElement(tempPSP);
            }
        }
        outputJList.setModel(listModel);
    }

    /**
     * 
     * @param regionInput
     */
    private void searchByRegion(ArrayList<String> regionInput) {
        pokedex = new ArrayList<String>();

        for (String s : regionInput) {
            try {
                pssJsonReader.readJSON(DIR + s + ".json");
                pokedex.addAll(pssJsonReader.get(s));
            } catch (FileNotFoundException e) {
                System.out.println("ERROR: FILE \"" + s + ".json\" WAS NOT FOUND OR IS EMPTY");
            }
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

            for (String s : evolutionInput) {
                if (s.equals(tempArray[1])) {
                    for (int h = 0; h < OBJECT_LENGTH; h++) {
                        pokedex.add(tempPokedex.get(h + g - EVOLUTION));
                    }
                }
            }
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

            breakpoint: for (int n = 0; n < tempArray.length; n++) {
                for (int d = 0; d < typeInput.size(); d++) {
                    if (tempArray[n].equalsIgnoreCase(typeInput.get(d))) {
                        for (int h = 0; h < OBJECT_LENGTH; h++) {
                            pokedex.add(tempPokedex.get(g + h - TYPE));
                        }
                        break breakpoint;
                    }
                }
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

            for (int n = 0; n + input.length() <= tempString.length(); n++) {
                if (tempString.substring(n, n + input.length()).equalsIgnoreCase(input)) {
                    for (int h = 0; h < OBJECT_LENGTH; h++) {
                        pokedex.add(tempPokedex.get(g + h - NAME));
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent l) {
        if (l.getValueIsAdjusting()) {
            // TODO: Use a different method to run this code
            int tempInt = outputJList.getSelectedIndex();
            listModel.get(tempInt).showInfoBox();
        }
    }
}

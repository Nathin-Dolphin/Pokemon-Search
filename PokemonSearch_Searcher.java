
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.json.JSONReader;

// import javax.swing.JOptionPane;

import java.awt.List;

import java.io.FileNotFoundException;

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 */
public class PokemonSearch_Searcher {
    private final int NAME = 0, NUMBER = 2, TYPE = 4, EVOLUTION = 6, OBJECT_LENGTH = 8;

    private JSONReader pssJsonReader;
    private ArrayList<String> pokedex, tempPokedex;

    public List outputList;

    /**
     * 
     * @param jsonReader
     */
    public PokemonSearch_Searcher(JSONReader jsonReader) {
        this.pssJsonReader = jsonReader;
        outputList = new List(40);
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
        String[] tempArray;
        String tempString;
        int tempInt, arrayPos = 1;
        pokedex = new ArrayList<String>();

        if (!findEvolutionSetNum) {
            outputList.removeAll();
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
            for (int g = NUMBER; g < pokedex.size(); g = g + OBJECT_LENGTH) {
                tempInt = Integer.parseInt(pokedex.get(g + 1));

                if (input == tempInt) {
                    tempString = processInfo(g - NUMBER);
                    tempPokedex.add(tempString);
                    g = pokedex.size();
                }
            }
            printToScreen(tempPokedex);
            searchByNumber(input, regionList, true);

        } else {
            for (int g = EVOLUTION; g < pokedex.size(); g = g + OBJECT_LENGTH) {
                tempInt = Integer.parseInt(pokedex.get(g + 1).split("-")[0]);

                if (input == tempInt) {
                    tempString = processInfo(g - EVOLUTION);
                    tempPokedex.add(tempString);
                }
            }
            printToScreen(tempPokedex);
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
        String tempString;
        outputList.removeAll();

        long startTime = System.nanoTime();

        searchByRegion(regionInput);

        if (evolutionInput.size() != 0)
            searchByEvolution(evolutionInput);

        if (typeInput.size() != 0)
            searchByType(typeInput);

        if (!input.equals(""))
            searchByName(input);

        // Prints the pokemon to the screen
        tempPokedex = new ArrayList<String>();
        for (int f = NAME; f < pokedex.size(); f = f + OBJECT_LENGTH) {
            tempString = pokedex.get(f);

            if (tempString.equals("name")) {
                tempString = processInfo(f + NAME);
                tempPokedex.add(tempString);
            }
        }
        printToScreen(tempPokedex);
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

    /**
     * Prints a pokemon's information.
     * 
     * @param pokedexPos The position of the {@code JSON} name "<i>name</i>" in the
     *                   {@code pokedex ArrayList}.
     */
    private String processInfo(int pokedexPos) {
        pokedexPos++;
        String[] tempArray;
        String region = foundInRegion(pokedex.get(pokedexPos + NUMBER));
        String tempString = "";
        int pokeNum;

        // Adds the region and pokemon evolution
        tempString = region + "   Evo:" + pokedex.get(pokedexPos + EVOLUTION) + "   ";

        // Adds the pokemon number
        pokeNum = Integer.parseInt(pokedex.get(pokedexPos + NUMBER));
        if (pokeNum > 999)
            tempString = tempString + "#" + pokeNum;
        else if (pokeNum > 99)
            tempString = tempString + "#0" + pokeNum;
        else if (pokeNum > 9)
            tempString = tempString + "#00" + pokeNum;
        else
            tempString = tempString + "#000" + pokeNum;

        // Adds the pokemon's name
        tempString = tempString + "   " + pokedex.get(pokedexPos + NAME) + "  ";
        for (int i = pokedex.get(pokedexPos + NAME).length(); i < 16; i++)
            tempString = tempString + " ";

        // Adds the types
        tempArray = pokedex.get(pokedexPos + TYPE).split("-");
        tempString = tempString + tempArray[0];
        if (tempArray.length == 2)
            tempString = tempString + ", " + tempArray[1];

        return tempString;
    }

    private void printToScreen(ArrayList<String> pokemonList) {
        for (String s : pokemonList) {
            outputList.add(s);
        }
    }

    /**
     * 
     * @param pokeNumString
     * @return A {@code String} of the region with the appropriate spaces to keep
     *         spacing in {@code outputList} consistent
     */
    private String foundInRegion(String pokeNumString) {
        int pokeNum = Integer.parseInt(pokeNumString);

        if (pokeNum <= 151)
            return "Kanto  ";
        else if (pokeNum <= 251)
            return "Johto   ";
        else if (pokeNum <= 386)
            return "Hoenn ";
        else if (pokeNum <= 493)
            return "Sinnoh";
        else if (pokeNum <= 649)
            return "Unova ";
        else if (pokeNum <= 721)
            return "Kalos  ";
        else if (pokeNum <= 809)
            return "Alola    ";
        else if (pokeNum <= 890)
            return "Galar   ";
        else
            return "NONE???";
    }

    // private void pokemonInformationPane() {}
}
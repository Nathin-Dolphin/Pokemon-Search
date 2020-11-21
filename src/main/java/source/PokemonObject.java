
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

package source;

import java.util.ArrayList;

import source.utility.Misc;
import source.utility.PopUpBox;

/**
 * @author Nathin Wascher
 * @version v1.1.1 - November 20, 2020
 */
public class PokemonObject {
    // TODO: Have OBJECT_LENGTH get calculated from 'format' in pokeInfo.json
    private static final int OBJECT_LENGTH = 8;

    private static ArrayList<String> regionNameList;
    private static ArrayList<Integer> regionRangeList;

    private ArrayList<String> pokedexEntry;
    private String pokemon;

    public PokemonObject() {
    }

    /**
     * 
     * @param regionList
     */
    public static void setRegionList(ArrayList<String> regionList) {
        regionNameList = new ArrayList<String>();
        regionRangeList = new ArrayList<Integer>();
        String tempString;

        for (int d = 0; d < regionList.size(); d = d + 2) {
            tempString = regionList.get(d);
            if (tempString.length() == 5) {
                tempString = tempString.concat(" ");
            }
            regionNameList.add(tempString);

            tempString = regionList.get(d + 1).split("-")[0];
            regionRangeList.add(Integer.parseInt(tempString));
        }
    }

    /**
     * Prints a pokemon's information.
     * 
     * @param pokedex
     * @param pokedexPos The position of the {@code JSON} name "<i>name</i>" in the
     *                   {@code pokedex ArrayList}.
     */
    public void processInfo(ArrayList<String> pokedex, int pokedexPos) {
        String[] tempArray;
        int pokeNum;

        pokedexEntry = new ArrayList<String>();
        for (int g = 0; g < OBJECT_LENGTH; g++) {
            pokedexEntry.add(pokedex.get(pokedexPos + g));
        }

        String region = foundInRegion(pokedexEntry.get(3));

        // Adds the region and pokemon evolution
        pokemon = region + "  Evo:" + pokedexEntry.get(7);

        // Adds the pokemon number
        pokeNum = Integer.parseInt(pokedexEntry.get(3));
        if (pokeNum >= 10 * 10 * 10) {
            pokemon = pokemon + "  #" + pokeNum;
        } else if (pokeNum >= 10 * 10) {
            pokemon = pokemon + "  #0" + pokeNum;
        } else if (pokeNum >= 10) {
            pokemon = pokemon + "  #00" + pokeNum;
        } else {
            pokemon = pokemon + "  #000" + pokeNum;
        }

        // Adds the pokemon's name
        pokemon = pokemon + "  " + pokedexEntry.get(1) + "  ";
        for (int i = pokedexEntry.get(1).length(); i < 4 * 3; i++) {
            pokemon = pokemon + " ";
        }

        // Adds the types
        tempArray = pokedexEntry.get(5).split("-");
        pokemon = pokemon + tempArray[0];
        if (tempArray.length == 2) {
            pokemon = pokemon + ", " + tempArray[1];
        }
    }

    /**
     * 
     */
    public void showInfoBox() {
        ArrayList<String> parse = new ArrayList<>();

        for (int i = 0; i < pokedexEntry.size(); i++) {
            parse.add(Misc.capitalize(pokedexEntry.get(i)) + " : " + pokedexEntry.get(i + 1));
        }

        PopUpBox tempPUB = new PopUpBox(pokedexEntry.get(1), parse);
        tempPUB.createBox();
    }

    /**
     * 
     * @param pokeNumString
     * @return A {@code String} of the region with the appropriate spaces to keep
     *         spacing in {@code outputList} consistent
     */
    private String foundInRegion(String pokeNumString) {
        int pokeNum = Integer.parseInt(pokeNumString);

        for (int v = 0; v < regionNameList.size(); v++) {
            if (pokeNum <= regionRangeList.get(v)) {
                return regionNameList.get(v);
            }
        }
        return "NULL";
    }

    @Override
    public String toString() {
        return pokemon;
    }
}

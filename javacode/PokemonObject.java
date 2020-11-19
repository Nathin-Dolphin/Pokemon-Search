
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import utility.Misc;
import utility.PopUpBox;

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 * @version v1.1 - November 19, 2020
 */
public class PokemonObject {
    private ArrayList<String> pokedexEntry;
    private String pokemon;

    public PokemonObject() {
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
        for (int g = 0; g < 8; g++)
            pokedexEntry.add(pokedex.get(pokedexPos + g));

        String region = foundInRegion(pokedexEntry.get(3));

        // Adds the region and pokemon evolution
        pokemon = region + "  Evo:" + pokedexEntry.get(7);

        // Adds the pokemon number
        pokeNum = Integer.parseInt(pokedexEntry.get(3));
        if (pokeNum >= 1000)
            pokemon = pokemon + "  #" + pokeNum;
        else if (pokeNum >= 100)
            pokemon = pokemon + "  #0" + pokeNum;
        else if (pokeNum >= 10)
            pokemon = pokemon + "  #00" + pokeNum;
        else
            pokemon = pokemon + "  #000" + pokeNum;

        // Adds the pokemon's name
        pokemon = pokemon + "  " + pokedexEntry.get(1) + "  ";
        for (int i = pokedexEntry.get(1).length(); i < 12; i++)
            pokemon = pokemon + " ";

        // Adds the types
        tempArray = pokedexEntry.get(5).split("-");
        pokemon = pokemon + tempArray[0];
        if (tempArray.length == 2)
            pokemon = pokemon + ", " + tempArray[1];
    }

    public void showInfoBox() {
        ArrayList<String> parse = new ArrayList<>();

        for (int i = 0; i < pokedexEntry.size(); i++)
            parse.add(Misc.capitalize(pokedexEntry.get(i)) + " : " + pokedexEntry.get(++i));

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

        // TODO: Have this part be automatically calculated--
        // TODO: --using the contents of 'pokeInfo.json'
        if (pokeNum <= 151)
            return "Kanto ";
        else if (pokeNum <= 251)
            return "Johto ";
        else if (pokeNum <= 386)
            return "Hoenn ";
        else if (pokeNum <= 493)
            return "Sinnoh";
        else if (pokeNum <= 649)
            return "Unova ";
        else if (pokeNum <= 721)
            return "Kalos ";
        else if (pokeNum <= 809)
            return "Alola ";
        else if (pokeNum <= 898)
            return "Galar ";
        else
            return "NULL";
    }

    @Override
    public String toString() {
        return pokemon;
    }
}
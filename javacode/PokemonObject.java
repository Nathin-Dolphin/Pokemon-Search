
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

import java.util.ArrayList;

/**
 * @author Nathin Wascher
 */
public class PokemonObject {
    private String pokemon;

    public PokemonObject() {
    }

    /**
     * Prints a pokemon's information.
     * 
     * @param pokedexPos The position of the {@code JSON} name "<i>name</i>" in the
     *                   {@code pokedex ArrayList}.
     */
    public void processInfo(ArrayList<String> pokedex, int pokedexPos) {
        pokedexPos++;
        String[] tempArray;
        String region = foundInRegion(pokedex.get(pokedexPos + 2));
        int pokeNum;

        // Adds the region and pokemon evolution
        pokemon = region + "  Evo:" + pokedex.get(pokedexPos + 6);

        // Adds the pokemon number
        pokeNum = Integer.parseInt(pokedex.get(pokedexPos + 2));
        if (pokeNum > 999)
            pokemon = pokemon + "  #" + pokeNum;
        else if (pokeNum > 99)
            pokemon = pokemon + "  #0" + pokeNum;
        else if (pokeNum > 9)
            pokemon = pokemon + "  #00" + pokeNum;
        else
            pokemon = pokemon + "  #000" + pokeNum;

        // Adds the pokemon's name
        pokemon = pokemon + "  " + pokedex.get(pokedexPos + 0) + "  ";
        for (int i = pokedex.get(pokedexPos + 0).length(); i < 12; i++)
            pokemon = pokemon + " ";

        // Adds the types
        tempArray = pokedex.get(pokedexPos + 4).split("-");
        pokemon = pokemon + tempArray[0];
        if (tempArray.length == 2)
            pokemon = pokemon + ", " + tempArray[1];
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
            return "NONE???";
    }

    @Override
    public String toString() {
        return pokemon;
    }
}
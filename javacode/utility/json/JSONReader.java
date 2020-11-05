
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package utility.json;

import java.io.FileNotFoundException;
import java.io.File;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Locates, reads and stores the information from a {@code .json} file into an
 * {@code ArrayList<String>}. Also has a {@code get()} method to find specific
 * values.
 * 
 * <p>
 * <b>Planned Features:</b>
 * <p>
 * Have 2 {@code ArrayList<String>}, where one includes brackets and the other
 * does not.
 * 
 * <p>
 * <b>Known Issues:</b>
 * <p>
 * If {@code objectName} in method {@code get()} has the same name as a value,
 * it will add the next {@code String} to the {@code ArrayList<String>}.
 * 
 * @author Nathin Wascher
 * @version 1.6
 * @since October 22, 2020
 * 
 * @see JSONParser
 */

public class JSONReader extends JSONParser {
    private Scanner fileScan;
    private ArrayList<String> jsonContents, BracketedContents, BracketlessContents;
    private String nextLine;

    public void run(String fileName) throws FileNotFoundException {
        System.out.println("JSONReader Thread: " + fileName);
        readJSON(fileName);
    }

    public JSONReader() {
    }

    /**
     * Looks for a file with the same name as {@code fileName}, then reads and
     * stores the data into an {@code ArrayList<String>}.
     * <p>
     * <b>[!] WARNING:</b>
     * <p>
     * If {@code includeBrackets} is {@code false}, {@code get()} may not function
     * as expected.
     * 
     * @param fileName        The name of the {@code .json} to read from
     * @param includeBrackets If the returning {@code ArrayList<String>} should
     *                        include the brackets from the {@code .json} file
     * @return An {@code ArrayList<String>} of the parsed {@code .json} data
     * @throws FileNotFoundException If {@code fileName} is not a {@code .json} or
     *                               can not be found
     * @see JSONParser
     */
    public ArrayList<String> readJSON(String fileName, boolean includeBrackets) throws FileNotFoundException {
        if (fileName.endsWith(".json"))
            fileScan = new Scanner(new File(fileName));
        else
            fileScan = new Scanner(new File(fileName + ".json"));

        jsonContents = new ArrayList<String>();
        BracketedContents = new ArrayList<String>();
        while (fileScan.hasNextLine()) {
            nextLine = fileScan.nextLine();
            jsonContents.add(nextLine);
        }

        if (includeBrackets) {
            BracketedContents = parseJSON(jsonContents, true);
            return BracketedContents;
        }
        BracketlessContents = parseJSON(jsonContents, false);
        return BracketlessContents;
    }

    /**
     * Overloaded Method
     * <p>
     * Looks for a file with the same name as {@code fileName}, then reads and
     * stores the data into an {@code ArrayList<String>}. Includes the brackets
     * found in a {@code .json}.
     * 
     * @param fileName The name of the {@code .json} to read from
     * @return An {@code ArrayList<String>} of the parsed {@code .json} data
     * @throws FileNotFoundException If {@code fileName} is not a {@code .json} or
     *                               can not be found
     * @see #readJSON(String, boolean)
     */
    public ArrayList<String> readJSON(String fileName) throws FileNotFoundException {
        return readJSON(fileName, true);
    }
}
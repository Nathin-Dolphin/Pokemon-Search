
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility.json;

import java.util.ArrayList;

/**
 * Parses the information from an {@code .json} file into a
 * {@code ArrayList<String>}. For reading a {@code .json} file from an url,
 * check {@link URLReader}. For locating and reading directly from a
 * {@code .json} file, check {@link JSONReader}.
 * 
 * <p>
 * <b>Planned Features:</b>
 * <p>
 * Have get(String) take an ArrayList instead of a String
 * <p>
 * Allow option in get(String) to get only values from the JSON
 * <p>
 * Optimize run()
 * 
 * <p>
 * <b>WARNING:</b> An extremely messy {@code .json} file may not get parsed
 * correctly.
 * 
 * @author Nathin Wascher
 * @version v1.9.1 - November 20, 2020
 * 
 * @see JSONReader
 * @see URLReader
 */

public class JSONParser extends Thread {

    /**
     * Contains a list of brackets found in parsed {@code .json} data:
     * <p>
     * <code>"[{"</code>, <code>"["</code>, <code>"{"</code>, <code>"}{"</code>,
     * <code>"}"</code>, <code>"]"</code>, <code>"}]"</code>.
     */
    private static final String[] BRACKET_LIST = { "[{", "[", "{", "}{", "}", "]", "}]" };

    private ArrayList<String> bracketedContents;
    private ArrayList<String> bracketlessContents;
    private ArrayList<String> jsonContents;
    private ArrayList<String> tempArray;

    private String[] parsedLine;
    private String tempString;
    private String tempStr2;
    private int arrayIndex;

    public JSONParser() {
    }

    // TODO: Allow option 'includeBrackets' when doing threads
    /**
     * 
     * @param rawJsonData The raw data from a single {@code .json} file
     * @return
     */
    public ArrayList<String> setJSONContents(ArrayList<String> rawJsonData) {
        this.jsonContents = rawJsonData;
        run();
        return tempArray;
    }

    /**
     * 
     */
    public void run() {
        tempArray = parseJSON(jsonContents);
    }

    /**
     * Overloaded Method
     * <p>
     * Parses data from a {@code .json} file and puts it into a
     * {@code ArrayList<String>}.
     * 
     * @param rawJsonData The raw data from a single {@code .json} file
     * @return An {@code ArrayList<String>} consisting of the {@code .json} data
     */
    public ArrayList<String> parseJSON(ArrayList<String> rawJsonData) {
        return parseJSON(rawJsonData, true);
    }

    /**
     * Parses data from a {@code .json} file and puts it into a
     * {@code ArrayList<String>}. If {@code includeBrackets} is false, an
     * {@code ArrayList<String>} that contains brackets will still be created, but
     * that array will not be returned. To access that array list, use
     * {@code getBracketedContents}.
     * 
     * @param rawJsonData     The raw data from a single {@code .json} file
     * @param includeBrackets If {@code true}, the parsed {@code .json} data should
     *                        keep the brackets else if {@code false}, remove them
     * @return An {@code ArrayList<String>} consisting of the {@code .json} data
     * 
     * @see #getBracketedContents()
     */
    public ArrayList<String> parseJSON(ArrayList<String> rawJsonData, boolean includeBrackets) {
        this.jsonContents = rawJsonData;
        if (!includeBrackets) {
            bracketlessContents = new ArrayList<String>();
        }
        bracketedContents = new ArrayList<String>();
        tempStr2 = null;

        for (int h = 0; h < jsonContents.size(); h++) {
            parsedLine = jsonContents.get(h).split("\"");

            for (int i = 0; i < parsedLine.length; i++) {
                tempString = parsedLine[i];

                if (!"".equals(tempString)) {
                    tempString = tempString.replaceAll(" ", "").replaceAll("\t", "");
                    if ("".equals(tempString)) {
                        tempString = null;
                    }
                }
                if (i % 2 == 0) {
                    if (tempString != null) {
                        sortJSONComponents(i, includeBrackets);
                    }
                    if (tempString != null) {
                        bracketedContents.add(tempString);
                        if (tempStr2 != null) {
                            bracketedContents.add(tempStr2);
                            tempStr2 = null;
                        }
                    }
                } else {
                    bracketedContents.add(tempString);
                    if (!includeBrackets) {
                        bracketlessContents.add(tempString);
                    }
                }
            }
        }
        if (includeBrackets) {
            return bracketedContents;
        } else {
            return bracketlessContents;
        }
    }

    /**
     * Primarily used to aquire {@code jsonContents} (which includes brackets) after
     * recieving {@code BracketlessContents} from {@code parseJSON()}.
     * 
     * @return The parsed {@code .json} contents that includes the brackets
     */
    public ArrayList<String> getBracketedContents() {
        return bracketedContents;
    }

    /**
     * Finds the value(s) associated with {@code ObjectName}.
     * 
     * @param objectName The name of an object or array in the {@code .json} file
     * @return An {@code ArrayList<String>} of values with the specified
     *         {@code objectName}
     * @throws NullPointerException If {@code readJSON} is not called before this
     *                              method, {@code jsonContents} equals null or if
     *                              the {@code .json} file is empty
     * @see #parseJSON(String, boolean)
     */
    public ArrayList<String> get(String objectName) throws NullPointerException {
        if (bracketedContents == null) {
            throw new NullPointerException();

        } else if (bracketedContents.size() == 0) {
            throw new NullPointerException("JSON FILE IS EMPTY");

        } else {
            tempArray = new ArrayList<String>();
            for (arrayIndex = 0; arrayIndex < bracketedContents.size(); arrayIndex++) {
                tempString = bracketedContents.get(arrayIndex);

                if (tempString.equals(objectName)) {
                    tempString = bracketedContents.get(++arrayIndex);

                    if (!isNewArray()) {
                        tempArray.add(tempString);
                    }
                }
            }
        }
        return tempArray;
    }

    // Checks if a new json array or object is beginning
    private boolean isNewArray() {
        if ("[".equals(tempString)) {
            endArrayCheck("]");
            return true;

        } else if ("{".equals(tempString)) {
            endArrayCheck("}");
            return true;

        } else if ("[{".equals(tempString)) {
            endArrayCheck("}]");
            return true;
        }
        return false;
    }

    // Checks if the current json array or object is ending
    private void endArrayCheck(String endBracket) {
        tempString = bracketedContents.get(++arrayIndex);

        while (!tempString.equals(endBracket)) {
            if ("}{".equals(tempString)) {
                tempString = bracketedContents.get(++arrayIndex);

            } else {
                tempArray.add(tempString);
                tempString = bracketedContents.get(++arrayIndex);
                isNewArray();
            }
        }
        tempString = bracketedContents.get(++arrayIndex);
    }

    // Sorts the json into names, values, and brackets as well as erase the colons
    // and commas between them.
    private void sortJSONComponents(int i, boolean includeBrackets) {
        tempString.replaceAll(" ", "");

        // Removes the colons between the names and their values.
        // Also removes some commas not considered part of a name or value.
        if (tempString.startsWith(":")) {
            if (tempString.contains("[{")) {
                tempString = "[{";

            } else if (tempString.contains("{")) {
                tempString = "{";

            } else if (tempString.contains("[")) {
                tempString = "[";

            } else {
                // Checks if this string contains a numerical (or unquoted) value
                tempString = tempString.replaceAll(":", "").replaceAll(",", "");
                if (!"".equals(tempString)) {
                    if (tempString.contains("}]")) {
                        tempString.replaceAll("}]", "");
                        tempStr2 = "}]";

                    } else if (tempString.contains("}")) {
                        tempString.replaceAll("}", "");
                        tempStr2 = "}";

                    } else if (tempString.contains("]")) {
                        tempString.replaceAll("]", "");
                        tempStr2 = "]";

                    } else {
                        try {
                            Integer.parseInt(tempString);
                        } catch (NumberFormatException e) {
                            tempString = null;
                        }
                    }
                } else {
                    tempString = null;
                }
            }
        } else if (",".equals(tempString) || ", ".equals(tempString)) {
            tempString = null;
        }

        if (tempString != null) {
            // Simplifies strings containing brackets.
            if (tempString.contains("}")) {
                if (tempString.contains("},{")) {
                    tempString = "}{";

                } else if (tempString.contains("},")) {
                    tempString = "}";

                } else if (tempString.contains("}],")) {
                    tempString = "}]";
                }
            } else if (tempString.contains(",{")) {
                tempString = "{";

            } else if (tempString.startsWith("],")) {
                tempString = "]";
            }

            if (includeBrackets) {
                // combines brackets if possible.
                int maxIndex = bracketedContents.size() - 1;
                if (maxIndex > 1) {
                    if ("[".equals(bracketedContents.get(maxIndex)) & "{".equals(tempString)) {
                        bracketedContents.remove(maxIndex);
                        tempString = "[{";

                    } else if ("}".equals(bracketedContents.get(maxIndex)) & "]".equals(tempString)) {
                        bracketedContents.remove(maxIndex);
                        tempString = "}]";

                    } else if ("}".equals(bracketedContents.get(maxIndex)) & "{".equals(tempString)) {
                        bracketedContents.remove(maxIndex);
                        tempString = "}{";
                    }
                }
            } else {
                tempStr2 = null;
                for (String s : BRACKET_LIST) {
                    if (tempString.equals(s)) {
                        tempString = null;
                    }
                }
            }
        }
    }

    /**
     * Returns a {@code String} of the contents in the current {@code .json} file.
     * 
     * @return The contents of the {@code .json} or null if no {@code jsonContents}
     *         was inputted
     */
    public String toString() {
        return toString(false);
    }

    /**
     * Returns a {@code String} of the contents in the current {@code .json} file.
     * 
     * @param printParsedContents If the parsed {@code .json} contents should be
     *                            returned rather than the raw data
     * @return The contents of the {@code .json} or null if no {@code jsonContents}
     *         was inputted
     */
    public String toString(boolean printParsedContents) {
        tempString = "";

        if (jsonContents == null) {
            return null;

        } else if (printParsedContents) {
            for (int i = 0; i < bracketedContents.size(); i++) {
                tempString = "\n" + tempString + bracketedContents.get(i);
            }
        } else {
            for (int i = 0; i < jsonContents.size(); i++) {
                tempString = "\n" + tempString + jsonContents.get(i);
            }
        }
        return tempString;
    }
}

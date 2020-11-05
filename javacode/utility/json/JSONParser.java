
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package utility.json;

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
 * @version 1.9
 * @since October 22, 2020
 * 
 * @see JSONReader
 * @see URLReader
 */

public class JSONParser extends Thread {
    private ArrayList<String> BracketedContents, BracketlessContents, jsonContents, tempArray;
    private String[] parsedLine;
    private String tempString, tempStr2;
    private int arrayIndex;

    /**
     * Contains a list of brackets found in parsed {@code .json} data:
     * <p>
     * <code>[{</code>, <code>[</code>, <code>{</code>, <code>}{</code>,
     * <code>}</code>, <code>]</code>, <code>}]</code>
     */
    public String[] bracketList;

    // TODO: Allow option 'includeBrackets' when doing threads
    public ArrayList<String> setJSONContents(ArrayList<String> jsonContents) {
        this.jsonContents = jsonContents;
        run();
        return tempArray;
    }

    public void run() {
        tempArray = parseJSON(jsonContents);
    }

    public JSONParser() {
        String[] brackets = { "[{", "[", "{", "}{", "}", "]", "}]" };
        bracketList = brackets;
    }

    /**
     * Overloaded Method
     * <p>
     * Parses data from a {@code .json} file and puts it into a
     * {@code ArrayList<String>}.
     * 
     * @param jsonContents The raw data from a single {@code .json} file
     * @return An {@code ArrayList<String>} consisting of the {@code .json} data
     */
    public ArrayList<String> parseJSON(ArrayList<String> jsonContents) {
        return parseJSON(jsonContents, true);
    }

    /**
     * Parses data from a {@code .json} file and puts it into a
     * {@code ArrayList<String>}. If {@code includeBrackets} is false, an
     * {@code ArrayList<String>} that contains brackets will still be created, but
     * that array will not be returned. To access that array list, use
     * {@code getBracketedContents}.
     * 
     * @param jsonContents    The raw data from a single {@code .json} file
     * @param includeBrackets If {@code true}, the parsed {@code .json} data should
     *                        keep the brackets else if {@code false}, remove them
     * @return An {@code ArrayList<String>} consisting of the {@code .json} data
     * 
     * @see #getBracketedContents()
     */
    public ArrayList<String> parseJSON(ArrayList<String> jsonContents, boolean includeBrackets) {
        if (!includeBrackets)
            BracketlessContents = new ArrayList<String>();
        BracketedContents = new ArrayList<String>();
        tempStr2 = null;
        this.jsonContents = jsonContents;

        for (int h = 0; h < jsonContents.size(); h++) {
            parsedLine = jsonContents.get(h).split("\"");

            for (int i = 0; i < parsedLine.length; i++) {
                tempString = parsedLine[i];

                if (!tempString.equals("")) {
                    tempString = tempString.replaceAll(" ", "").replaceAll("\t", "");
                    if (tempString.equals(""))
                        tempString = null;
                }

                if (i % 2 == 0) {
                    if (tempString != null)
                        sortJSONComponents(i, includeBrackets);
                    if (tempString != null) {
                        BracketedContents.add(tempString);
                        if (tempStr2 != null) {
                            BracketedContents.add(tempStr2);
                            tempStr2 = null;
                        }
                    }
                } else {
                    BracketedContents.add(tempString);
                    if (!includeBrackets)
                        BracketlessContents.add(tempString);
                }
            }
        }
        if (includeBrackets)
            return BracketedContents;
        else
            return BracketlessContents;
    }

    /**
     * Primarily used to aquire {@code jsonContents} (which includes brackets) after
     * recieving {@code BracketlessContents} from {@code parseJSON()}
     * 
     * @return The parsed {@code .json} contents that includes the brackets
     */
    public ArrayList<String> getBracketedContents() {
        return BracketedContents;
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
        if (BracketedContents == null) {
            throw new NullPointerException();

        } else if (BracketedContents.size() == 0) {
            throw new NullPointerException("JSON FILE IS EMPTY");

        } else {
            tempArray = new ArrayList<String>();
            for (arrayIndex = 0; arrayIndex < BracketedContents.size(); arrayIndex++) {
                tempString = BracketedContents.get(arrayIndex);

                if (tempString.equals(objectName)) {
                    tempString = BracketedContents.get(++arrayIndex);

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
        if (tempString.equals("[")) {
            endArrayCheck("]");
            return true;

        } else if (tempString.equals("{")) {
            endArrayCheck("}");
            return true;

        } else if (tempString.equals("[{")) {
            endArrayCheck("}]");
            return true;
        }
        return false;
    }

    // Checks if the current json array or object is ending
    private void endArrayCheck(String endBracket) {
        tempString = BracketedContents.get(++arrayIndex);

        while (!tempString.equals(endBracket)) {
            if (tempString.equals("}{")) {
                tempString = BracketedContents.get(++arrayIndex);

            } else {
                tempArray.add(tempString);
                tempString = BracketedContents.get(++arrayIndex);
                isNewArray();
            }
        }
        tempString = BracketedContents.get(++arrayIndex);
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
                if (!tempString.equals("")) {
                    if (tempString.contains("}]")) {
                        tempString.replaceAll("}]", "");
                        tempStr2 = "}]";

                    } else if (tempString.contains("}")) {
                        tempString.replaceAll("}", "");
                        tempStr2 = "}";

                    } else if (tempString.contains("]")) {
                        tempString.replaceAll("]", "");
                        tempStr2 = "]";

                    } else
                        try {
                            Integer.parseInt(tempString);
                        } catch (NumberFormatException e) {
                            tempString = null;
                        }
                } else
                    tempString = null;
            }
        } else if (tempString.equals(",") || tempString.equals(", ")) {
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
                int maxIndex = BracketedContents.size() - 1;
                if (maxIndex > 1) {
                    if (BracketedContents.get(maxIndex).equals("[") & tempString.equals("{")) {
                        BracketedContents.remove(maxIndex);
                        tempString = "[{";

                    } else if (BracketedContents.get(maxIndex).equals("}") & tempString.equals("]")) {
                        BracketedContents.remove(maxIndex);
                        tempString = "}]";

                    } else if (BracketedContents.get(maxIndex).equals("}") & tempString.equals("{")) {
                        BracketedContents.remove(maxIndex);
                        tempString = "}{";
                    }
                }
            } else {
                tempStr2 = null;
                for (String s : bracketList) {
                    if (tempString.equals(s)) {
                        tempString = null;
                    }
                }
            }
        }
    }

    /**
     * Returns a {@code String} of the contents in the current {@code .json} file
     * 
     * @return The contents of the {@code .json} or null if no {@code jsonContents}
     *         was inputted
     */
    public String toString() {
        return toString(false);
    }

    /**
     * Returns a {@code String} of the contents in the current {@code .json} file
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
            for (int i = 0; i < BracketedContents.size(); i++)
                tempString = "\n" + tempString + BracketedContents.get(i);

        } else
            for (int i = 0; i < jsonContents.size(); i++)
                tempString = "\n" + tempString + jsonContents.get(i);

        return tempString;
    }
}

/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility.json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author Nathin Wascher
 * @version v1.1.6 - November 20, 2020
 */
public class JSONWriter {
    private ArrayList<String> jsonContents;
    private ArrayList<String> endBrackets;
    private String tabs;
    private String fileName;

    /**
     * 
     * @param fileName
     */
    public JSONWriter(JPanel panel, String fileName) {
        String warning = "The file \"" + fileName + "\" was found.\nDo you want to overwrite this file?";
        Scanner scan;
        jsonContents = new ArrayList<String>();
        endBrackets = new ArrayList<String>();
        tabs = "\t";

        if (!fileName.endsWith(".json")) {
            fileName.concat(".json");
        }
        this.fileName = fileName;
        jsonContents.add("{");

        try {
            scan = new Scanner(new File(fileName));
            int output = JOptionPane.showInternalConfirmDialog(panel, warning, "WARNING", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (output != JOptionPane.YES_OPTION) {
                System.out.println("\n...Terminating Program From (JSONWriter)");
                // TODO: Have it close the frame/panel its attached too
                System.exit(0);
                scan.close();
            }
        } catch (FileNotFoundException e) {
        }
    }

    /**
     * 
     * @param input
     */
    public void newObject(ArrayList<String> input) {
        String tempString = "";

        if (jsonContents.get(jsonContents.size() - 1).endsWith("}")) {
            jsonContents.remove(jsonContents.size() - 1);
            jsonContents.add(tabs + "},{");
        } else {
            jsonContents.add(tabs + "{");
        }

        for (int i = 0; i < input.size(); i = i + 2) {
            tempString = tabs + "\t\"" + input.get(i) + "\": \"" + input.get(i + 1) + "\"";
            if (i < input.size() - 1) {
                tempString = tempString + ",";
            }
            jsonContents.add(tempString);
        }
        jsonContents.add(tabs + "}");
    }

    /**
     * WARNING: NOT IMPLEMENTED.
     * 
     * @param input
     * @param name
     */
    public void newObject(ArrayList<String> input, String name) {
        // TODO: Implement newObject(ArrayList<String>, String)
    }

    /**
     * 
     * @param name
     */
    public void newArray(String name) {
        if (jsonContents.get(jsonContents.size() - 1).endsWith("}")) {
            jsonContents.remove(jsonContents.size() - 1);
            jsonContents.add(tabs + "},");

        } else if (jsonContents.get(jsonContents.size() - 1).endsWith("]")) {
            jsonContents.remove(jsonContents.size() - 1);
            jsonContents.add(tabs + "],");
        }

        jsonContents.add(tabs + "\"" + name + "\": [");
        tabs = tabs + "\t";
        endBrackets.add("]");
    }

    /**
     * 
     */
    public void endArray() {
        tabs = "";
        for (int i = 0; i < endBrackets.size(); i++) {
            tabs = tabs + "\t";
        }
        jsonContents.add(tabs + endBrackets.get(endBrackets.size() - 1));
        endBrackets.remove(endBrackets.size() - 1);
    }

    /** 
     * 
    */
    public void closeFile() {
        PrintWriter pw;
        jsonContents.add("}");

        try {
            FileWriter fw = new FileWriter(fileName);
            BufferedWriter bw = new BufferedWriter(fw);
            pw = new PrintWriter(bw);

        } catch (IOException e) {
            System.out.println("ERROR: FAILED TO CREATE \"" + fileName + "\"");
            return;
        }

        // TODO: Add loading bar here
        for (int f = 0; f < jsonContents.size() - 1; f++) {
            pw.println(jsonContents.get(f));
        }
        pw.print(jsonContents.get(jsonContents.size() - 1));
        pw.close();
    }
}

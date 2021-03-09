
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is under the MIT License.
 */

package source;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;

import source.utility.json.JSONReader;
import source.utility.json.URLReader;

/**
 * @author Nathin Wascher
 */
public class PS_DownloadPokeJSONs {
    private static final String POKE_URL = "https://jsontextfiles.azurewebsites.net/pokeInfo.json";
    private static final String DIR = "resources\\";

    private URLReader urlReader;
    private JSONReader jsonReader;

    private String pokeJSONVersion;
    private String pokeURLVersion;

    private int failSafe = -1;

    /**
     * 
     */
    public PS_DownloadPokeJSONs() {
        urlReader = new URLReader();
    }

    /**
     * 
     * @param jsonReader
     */
    public void setJsonReader(JSONReader jsonReader) {
        this.jsonReader = jsonReader;
    }

    /**
     * Reads from the locally stored file 'pokeInfo'.
     */
    public void readPokeInfo() {

        // To prevent an infinite loop
        failSafe++;
        if (failSafe >= 5) {
            System.out.println("ERROR: FAILED TO DOWNLOAD \'POKEINFO\' AFTER MULTIPLE ATTEMPTS");
            System.out.println("...Terminating Program (PokemonSearch)");
            System.exit(0);
        }

        // Read from 'pokeInfo' and get version, types, and regions
        try {
            jsonReader.readJSON(DIR + "pokeInfo");
            pokeJSONVersion = jsonReader.get("version").get(0);

            // Download 'pokeInfo' if the file is not found
            // or if the version number is unobtainable
        } catch (FileNotFoundException e) {
            downloadPokeInfo(true);
            readPokeInfo();
        }

        // Initially just read 'pokeInfo' from the url
        if (failSafe <= 0) {
            downloadPokeInfo(false);
        }

        // Get the version number from 'pokeInfo' through the url
        try {
            pokeURLVersion = urlReader.get("version").get(0);
        } catch (NullPointerException n) {
            System.out.println("ERROR: UNABLE TO GET VERSION NUMBER FROM \'POKEINFO\' THROUGH URL");
            downloadPokeInfo(true);
            readPokeInfo();
        }

        checkVersions();
    }

    // Compares the two 'pokeInfo' versions.
    private void checkVersions() {
        ArrayList<String> jsonContents;
        ArrayList<String> tempArray;

        if ("debug".equals(pokeJSONVersion)) {
            System.out.println("DEBUG");
            /**
             * TODO: Fix this
             * 
             * FRAME.setTitle("PokemonSearch: DEBUG MODE");
             * JOptionPane.showMessageDialog(MENU_PANEL, "PokemonSearch is in debug
             * mode\nand will NOT download any JSON files!", "WARNING: DEBUG MODE ACTIVE",
             * JOptionPane.INFORMATION_MESSAGE);
             */

            // If the two version match, check the region Files
        } else if (pokeJSONVersion.equals(pokeURLVersion)) {
            System.out.println("POKEINFO VERSION " + pokeURLVersion + " == " + pokeJSONVersion);
            tempArray = urlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {

                // Detect if the file is empty
                try {
                    Scanner fileScan = new Scanner(new File(DIR + tempArray.get(i) + ".json"));
                    if (fileScan.nextLine().equals("")) {
                        fileScan.close();
                        throw new FileNotFoundException();
                    }
                    fileScan.close();

                    // Download the region files that are not found
                } catch (FileNotFoundException e) {
                    jsonContents = urlReader.readURL(tempArray.get(i + 1));
                    if (urlReader.isValidURL()) {
                        writeToFile(tempArray.get(i), jsonContents);
                    }
                }
            }

        } else {
            System.out.println("POKEINFO VERSION " + pokeURLVersion + " DOES NOT EQUAL " + pokeJSONVersion);
            downloadPokeInfo(true);
            tempArray = urlReader.get("regionURLs");

            for (int i = 0; i < tempArray.size(); i = i + 2) {
                jsonContents = urlReader.readURL(tempArray.get(i + 1));
                if (urlReader.isValidURL()) {
                    writeToFile(tempArray.get(i), jsonContents);
                }
            }
            readPokeInfo();
        }
    }

    /**
     * Get the contents from 'pokeInfo' through the url.
     * 
     * @param writeContentsToFile
     */
    private void downloadPokeInfo(boolean writeContentsToFile) {
        ArrayList<String> jsonContents = null;

        jsonContents = urlReader.readURL(POKE_URL);
        urlReader.parseJSON(jsonContents);

        if (jsonContents == null) {
            System.out.println("ERROR: URL NOT FOUND. MAKE SURE YOU HAVE AN INTERNET CONNECTION.");
        } else if (writeContentsToFile) {
            writeToFile("pokeInfo", jsonContents);
        }
    }

    /**
     * Writes the given array list to a JSON.
     * 
     * @param fileName
     * @param jsonContents
     */
    private void writeToFile(String fileName, ArrayList<String> jsonContents) {
        System.out.println("Writing to file: " + fileName + ".json");

        try {
            FileWriter fw = new FileWriter(DIR + fileName + ".json");
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            for (int i = 0; i < jsonContents.size() - 1; i++) {
                pw.println(jsonContents.get(i));
            }
            pw.print(jsonContents.get(jsonContents.size() - 1));
            pw.close();

        } catch (IOException e) {
            System.out.println("ERROR: FAILED TO WRITE " + fileName + ".json.");
        }
    }
}

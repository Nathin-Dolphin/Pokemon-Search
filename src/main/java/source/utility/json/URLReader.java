
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Reads an {@code URL}, and if it leads to a {@code .json} file, stores the
 * information from the file into an {@code ArrayList<String>}.
 * 
 * <p>
 * <b>Planned Features:</b>
 * <p>
 * Overload the method {@code readURLIndex} to allow indexing multiple files
 * <p>
 * Add an option to exclude the brackets from a {@code .json}.
 * <p>
 * Implement threading.
 * 
 * <p>
 * <b>No Known Issues</b>
 * 
 * @author Nathin Wascher
 * @version v1.3.5 - November 20, 2020
 * 
 * @see JSONParser
 */
public class URLReader extends JSONParser {
    private BufferedReader br;
    private URL openURL;

    private ArrayList<String> urlContents;
    private ArrayList<String> urlIndexContents;
    private ArrayList<String> urlList;
    private boolean validURL;

    public URLReader() {
    }

    // WORK IN PROGRESS
    /**
     * @return
     */
    public boolean isValidURL() {
        return validURL;
    }

    /**
     * Tests if the input {@code URL} can be found and opened.
     * 
     * @param url The {@code URL} to be tested
     * @return {@code true} if the provided {@code URL} can be opened
     */
    public boolean isValidURL(String url) {
        try {
            openURL = new URL(url);
            validURL = true;
            return true;

        } catch (MalformedURLException e) {
            validURL = false;
            return false;
        }
    }

    /**
     * Reads and copies the information from the {@code .json} file referenced by
     * the {@code URL} and stores it into an {@code ArrayList<String>}.
     * 
     * @param url           The {@code URL} to read from
     * @param printContents If the contents of the {@code .json} should be printed
     *                      to the terminal
     * @return An {@code ArrayList<String>} of the {@code .json} data
     *         <p>
     *         Returns null if the {@code URL} is invalid
     */
    public ArrayList<String> readURL(String url, boolean printContents) {
        String tempString;
        System.out.println("\nREADING URL:  " + url);
        if (isValidURL(url)) {
            urlContents = new ArrayList<String>();

            try {
                br = new BufferedReader(new InputStreamReader(openURL.openStream()));
                while ((tempString = br.readLine()) != null) {
                    urlContents.add(tempString);
                    if (printContents) {
                        System.out.println(tempString);
                    }
                }
                br.close();

            } catch (IOException e) {
                System.out.println("ERROR: I/O EXCEPTION\n" + e);
            }
            return urlContents;
        } else {
            System.out.println("ERROR: INVALID URL");
            return null;
        }
    }

    /**
     * Overloaded Method
     * <p>
     * Reads and copies the information from the {@code .json} file referenced by
     * the {@code URL} and stores it into an {@code ArrayList<String>}.
     * 
     * @param url The {@code URL} to read from
     * @return An {@code ArrayList<String>} of the {@code .json} data
     *         <p>
     *         Returns null if the {@code URL} is invalid
     */
    public ArrayList<String> readURL(String url) {
        return readURL(url, false);
    }

    /**
     * Reads and finds valid {@code URLs} from the {@code .json} referenced by the
     * {@code URL} index, then reads and copies the information from those
     * {@code .json} files and stores them into an {@code ArrayList<String>}.
     * 
     * @param url           The {@code URL} to read from
     * @param printContents If the contents of the {@code .json} should be printed
     *                      to the terminal
     * @return An {@code ArrayList<String>} of the {@code .json} data
     */
    public ArrayList<String> readURLIndex(String url, boolean printContents) {
        readURL(url, printContents);
        getURLList();
        urlIndexContents = new ArrayList<String>();

        try {
            for (String s : urlList) {
                urlIndexContents.addAll(readURL(s, printContents));
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Warning: No Valid URL Links");
        }
        return urlIndexContents;
    }

    /**
     * Overloaded Method
     * <p>
     * Reads and finds valid {@code URLs} from the {@code .json} referenced by the
     * {@code URL} index, then reads and copies the information from those
     * {@code .json} files and stores them into an {@code ArrayList<String>}.
     * 
     * @param url The {@code URL} to read from
     * @return {@code ArrayList<String>}
     * @see #readURLindex(String, boolean)
     * @see #readURL(String, boolean)
     */
    public ArrayList<String> readURLIndex(String url) {
        return readURLIndex(url, false);
    }

    /**
     * Returns the a list of {@code URLs} contained within the {@code .json}.
     * 
     * @return An {@code ArrayList<String>} of valid {@code URLs}
     * @see URL
     */
    public ArrayList<String> getURLList() {
        urlList = new ArrayList<String>();
        urlContents = parseJSON(urlContents, false);

        for (String tempString : urlContents) {
            try {
                new URL(tempString);
                urlList.add(tempString);
            } catch (MalformedURLException e) {
            }
        }
        return urlList;
    }
}

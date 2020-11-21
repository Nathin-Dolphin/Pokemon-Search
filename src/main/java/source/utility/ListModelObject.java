
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility;

/**
 * This class is strictly used in conjunction with DefaultListModel and JList.
 * 
 * @author Nathin Wascher
 * @version v1.0.2 - November 20, 2020
 * 
 * @see javax.swing.DefaultListModel
 * @see javax.swing.JList
 */
public class ListModelObject {
    private String str;

    /**
     * 
     * @param str The String this class object will hold
     */
    public ListModelObject(String str) {
        this.str = str;
    }

    /**
     * Call this to change this class's string.
     * 
     * @param string The String this class object will hold
     */
    public void setString(String string) {
        this.str = string;
    }

    @Override
    public String toString() {
        return str;
    }
}

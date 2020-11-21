
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package source.utility;

import java.awt.Component;
import java.awt.GridBagConstraints;

import javax.swing.JOptionPane;

/**
 * <p>
 * <b>No Known Issues.</b>
 * 
 * @author Nathin Wascher
 * @version v1.0.1 - November 20, 2020
 */
public final class Misc {

    private Misc() {
    }

    /**
     * Capitalizes the inputted string.
     * 
     * @param input The string to capitalize
     * @return The capitalized string
     * @throws StringIndexOutOfBoundsException if the {@code beginIndex} is
     *                                         negative, or {@code endIndex} is
     *                                         larger than the length of this
     *                                         {@code String} object, or
     *                                         {@code beginIndex} is larger than
     *                                         {@code endIndex}.
     */
    public static String capitalize(String input) throws StringIndexOutOfBoundsException {
        String tempString;
        tempString = input.substring(0, 1).toUpperCase();
        tempString = tempString.concat(input.substring(1, input.length()));
        return tempString;
    }

    /**
     * Set the x and y position of the {@code GridBagConstraint}.
     * 
     * @param gbc   {@code GridBagConstraint}
     * @param gridX {@code GridBagConstraint.gridx} value
     * @param gridY {@code GridBagConstraint.gridy} value
     * @return The {@code GridBagConstraint} with the new grid position
     */
    public static GridBagConstraints setGBC(GridBagConstraints gbc, int gridX, int gridY) {
        gbc.gridx = gridX;
        gbc.gridy = gridY;
        return gbc;
    }

    /**
     * 
     * @param frame
     * @param warningMessage
     */
    public static void warningBox(Component frame, String warningMessage) {
        JOptionPane.showConfirmDialog(frame, "WARNING: " + warningMessage, "WARNING MESSAGE!",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
    }
}

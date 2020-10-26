
/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package utility.graphics;

import utility.RandomGen;

import java.awt.Graphics;
import java.awt.Color;

/**
 * @author Nathin Wascher
 * @version 1.1.2
 * @since March 6, 2020
 */
public class DrawArc extends RandomGen {
    private static final long serialVersionUID = 4954228904271827036L;

    public int xPos, yPos, height, width, startAngle, arcAngle;
    public boolean fillShape;
    public Color newColor;

    // default constructor
    public DrawArc() {
        xPos = intGen(0, 100);
        yPos = intGen(0, 100);
        height = intGen(10, 200);
        width = intGen(10, 200);
        fillShape = boolGen();
        newColor = colorGen();
        randomArc();
    }

    // String, boolean, Color, int, int, int, int, int, int
    public DrawArc(boolean fillShape, Color newColor, int xPos, int yPos, int width, int height, int startAngle,
            int arcAngle) {
        setAngles(startAngle, arcAngle);
        drawArc(fillShape, newColor, xPos, yPos, width, height);
    }

    // String, Color, int, int, int, int
    public DrawArc(Color newColor, int xPos, int yPos, int width, int height, int startAngle, int arcAngle) {
        setAngles(startAngle, arcAngle);
        drawArc(boolGen(), newColor, xPos, yPos, width, height);
    }

    // String, boolean, int, int, int, int
    public DrawArc(boolean fillShape, int xPos, int yPos, int width, int height, int startAngle, int arcAngle) {
        setAngles(startAngle, arcAngle);
        drawArc(fillShape, colorGen(), xPos, yPos, width, height);
    }

    // String, int, int, int, int
    public DrawArc(int xPos, int yPos, int width, int height, int startAngle, int arcAngle) {
        setAngles(startAngle, arcAngle);
        drawArc(boolGen(), colorGen(), xPos, yPos, width, height);
    }

    // boolean, Color, int, int, int, int
    public DrawArc(boolean fillShape, Color newColor, int xPos, int yPos, int width, int height) {
        randomArc();
        drawArc(fillShape, newColor, xPos, yPos, width, height);
    }

    // Color, int, int, int, int
    public DrawArc(Color newColor, int xPos, int yPos, int width, int height) {
        randomArc();
        drawArc(boolGen(), newColor, xPos, yPos, width, height);
    }

    // boolean, int, int, int, int
    public DrawArc(boolean fillShape, int xPos, int yPos, int width, int height) {
        randomArc();
        drawArc(fillShape, colorGen(), xPos, yPos, width, height);
    }

    // int, int, int, int
    public DrawArc(int xPos, int yPos, int width, int height) {
        randomArc();
        drawArc(boolGen(), colorGen(), xPos, yPos, width, height);
    }

    private void drawArc(boolean fillShape, Color newColor, int xPos, int yPos, int width, int height) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    private void setAngles(int startAngle, int arcAngle) {
        this.startAngle = startAngle;
        this.arcAngle = arcAngle;
    }

    private void randomArc() {
        startAngle = intGen(-360, 0);
        arcAngle = intGen(startAngle, startAngle + 360);
    }

    public void paint(Graphics g) {
        g.setColor(newColor);

        if (fillShape)
            g.fillArc(xPos, yPos, width, height, startAngle, arcAngle);
        else
            g.drawArc(xPos, yPos, width, height, startAngle, arcAngle);
    }
}
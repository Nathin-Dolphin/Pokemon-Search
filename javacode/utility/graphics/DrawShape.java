
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
 * @version 1.0.5
 * @since March 3, 2020
 * 
 * String shape = "oval" or "rect"
 */
public class DrawShape {
    public RandomGen gen = new RandomGen();
    
    public int xPos, yPos, height, width;
    public boolean fillShape;
    public Color newColor;
    public String shape;

    // default constructor
    public DrawShape() {
        xPos = gen.intGen(0, 100);
        yPos = gen.intGen(0, 100);
        height = gen.intGen(10, 200);
        width = gen.intGen(10, 200);
        fillShape = gen.boolGen();
        newColor = gen.colorGen();
        shape = randomShape();
    }

    // String, boolean, Color, int, int, int, int
    public DrawShape(String shape, boolean fillShape, Color newColor, int xPos, int yPos, int width, int height) {
        drawShape(shape, fillShape, newColor, xPos, yPos, width, height);
    }

    // String, Color, int, int, int, int
    public DrawShape(String shape, Color newColor, int xPos, int yPos, int width, int height) {
        drawShape(shape, gen.boolGen(), newColor, xPos, yPos, width, height);
    }

    // String, boolean, int, int, int, int
    public DrawShape(String shape, boolean fillShape, int xPos, int yPos, int width, int height) {
        drawShape(shape, fillShape, gen.colorGen(), xPos, yPos, width, height);
    }

    // String, int, int, int, int
    public DrawShape(String shape, int xPos, int yPos, int width, int height) {
        drawShape(shape, gen.boolGen(), gen.colorGen(), xPos, yPos, width, height);
    }

    // boolean, Color, int, int, int, int
    public DrawShape(boolean fillShape, Color newColor, int xPos, int yPos, int width, int height) {
        drawShape(randomShape(), fillShape, newColor, xPos, yPos, width, height);
    }

    // Color, int, int, int, int
    public DrawShape(Color newColor, int xPos, int yPos, int width, int height) {
        drawShape(randomShape(), gen.boolGen(), newColor, xPos, yPos, width, height);
    }

    // boolean, int, int, int, int
    public DrawShape(boolean fillShape, int xPos, int yPos, int width, int height) {
        drawShape(randomShape(), fillShape, gen.colorGen(), xPos, yPos, width, height);
    }

    // int, int, int, int
    public DrawShape(int xPos, int yPos, int width, int height) {
        drawShape(randomShape(), gen.boolGen(), gen.colorGen(), xPos, yPos, width, height);
    }

    private void drawShape(String shape, boolean fillShape, Color newColor, int xPos, int yPos, int width, int height) {
        this.shape = shape;
        this.fillShape = fillShape;
        this.newColor = newColor;
        this.xPos = xPos;
        this.yPos = yPos;
        this.width = width;
        this.height = height;
    }

    private String randomShape() {
        if (gen.boolGen())
            return shape = "oval";
        else
            return shape = "rect";
    }

    public Color getColor() {
        return newColor;
    }

    public void paint(Graphics g) {
        g.setColor(newColor);

        if (fillShape)
            switch (shape) {
                case "oval":
                    g.fillOval(xPos, yPos, width, height);
                    break;
                case "rect":
                    g.fillRect(xPos, yPos, width, height);
                    break;
            }
        else
            switch (shape) {
                case "oval":
                    g.drawOval(xPos, yPos, width, height);
                    break;
                case "rect":
                    g.drawRect(xPos, yPos, width, height);
                    break;
            }
    }
}

/**
 * Copyright (c) 2020 Nathin-Dolphin.
 * 
 * This file is part of the utility library and is under the MIT License.
 */

package utility.graphics;

import java.awt.Graphics;
import java.awt.Color;

// allow classes more control over the features of a face object
// have colors not blend in with background, face color, etc.

/**
 * @author Nathin Wascher
 * @version 1.2.6
 * @since October 26, 2020
 */
public class DrawFace extends DrawShape {
    private DrawShape leftEye, rightEye;
    private DrawArc mouth;

    private int tempXPos, tempYPos, tempHeight, tempWidth, startAngle, arcAngle;
    private boolean wideMouth, isSmile;
    private Color eyeColor;

    public DrawFace(int xPos, int yPos, int width, int height) {
        super("oval", xPos, yPos, width, height);
        drawFace(gen.boolGen(), gen.boolGen());
    }

    public DrawFace(boolean isSolid, int xPos, int yPos, int width, int height) {
        super("oval", isSolid, xPos, yPos, width, height);
        drawFace(gen.boolGen(), gen.boolGen());
    }

    public DrawFace(boolean isSolid, boolean isSmile, boolean wideMouth, int xPos, int yPos, int width, int height) {
        super("oval", isSolid, xPos, yPos, width, height);
        drawFace(isSmile, wideMouth);
    }

    public void repaintFace(int xPos, int yPos) {
        // WORK IN PROGRESS
    }

    private void drawFace(boolean isSmile, boolean wideMouth) {
        eyeColor = gen.analogousColor(getColor(), 50);
        this.isSmile = isSmile;
        this.wideMouth = wideMouth;
        facialFeatures(xPos, yPos, width, height);
    }

    private void facialFeatures(int xPos, int yPos, int width, int height) {
        tempWidth = width / 5;
        tempHeight = height / 5;
        tempYPos = height / 6 + yPos;

        tempXPos = (int) (width / 2 + (double) (width / 10) + xPos);
        rightEye = new DrawShape("oval", true, eyeColor, tempXPos, tempYPos, tempWidth, tempHeight);

        tempXPos = (int) (width / 2 - (double) (width / 10) + xPos - tempWidth);
        leftEye = new DrawShape("oval", true, eyeColor, tempXPos, tempYPos, tempWidth, tempHeight);

        drawMouth();
    }

    private void drawMouth() {
        if (isSmile) {
            tempYPos = yPos - height / 10;

            if (wideMouth) {
                startAngle = 0;
                arcAngle = -180;

            } else {
                startAngle = gen.intGen(-60, 0);
                arcAngle = -180 - (2 * startAngle);
            }
        } else {
            if (wideMouth) {
                tempYPos = yPos + height / 3;
                startAngle = 0;
                arcAngle = 180;

            } else {
                tempYPos = yPos + height / 2;
                startAngle = gen.intGen(20, 60);
                arcAngle = 180 - (2 * startAngle);
            }
        }

        tempWidth = 2 * width / 3;
        tempHeight = height;
        tempXPos = xPos + width / 6;
        mouth = new DrawArc(wideMouth, gen.complementColor(eyeColor), tempXPos, tempYPos, tempWidth, tempHeight,
                startAngle, arcAngle);
    }

    public void paint(Graphics g) {
        super.paint(g);
        leftEye.paint(g);
        rightEye.paint(g);
        mouth.paint(g);
    }
}
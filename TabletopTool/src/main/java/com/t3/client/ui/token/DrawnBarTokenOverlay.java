/*
 * Copyright (c) 2014 tabletoptool.com team.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     rptools.com team - initial implementation
 *     tabletoptool.com team - further development
 */
package com.t3.client.ui.token;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.t3.model.Token;
import com.t3.xstreamversioned.version.SerializationVersion;

/**
 * Draws a single color bar along one side of a token.
 * 
 * @author Jay
 */
@SerializationVersion(0)
public class DrawnBarTokenOverlay extends BarTokenOverlay {

    /**
     * The color of the bar.
     */
    private Color barColor;
    
    /**
     * The thickness of the bar in pixels
     */
    private int thickness;
    
    /**
     * Build the bar with all of the details
     * 
     * @param name Name of the overlay
     * @param aBarColor Color of the bar
     * @param aThickness Thickness of the bar
     */
    public DrawnBarTokenOverlay(String name, Color aBarColor, int aThickness) {
        super(name);
        barColor = aBarColor;
        thickness = aThickness;
    }
    
    /**
     * Default constructor for serialization.
     */
    public DrawnBarTokenOverlay() {
        this(AbstractTokenOverlay.DEFAULT_STATE_NAME, Color.RED, 5);
    }
    
    /**
     * @see com.t3.client.ui.token.BarTokenOverlay#paintOverlay(java.awt.Graphics2D, com.t3.model.Token, java.awt.Rectangle, double)
     */
    @Override
    public void safePaintOverlay(Graphics2D g, Token token, Rectangle bounds, float value) {
        int width = (getSide() == Side.TOP || getSide() == Side.BOTTOM) ? bounds.width : thickness;
        int height = (getSide() == Side.LEFT || getSide() == Side.RIGHT) ? bounds.height : thickness;
        int x = 0;
        int y = 0;
        switch (getSide()) {
        case RIGHT:
            x = bounds.width - width;
            break;
        case BOTTOM:
            y = bounds.height - height;
        } // endswitch
        
        if (getSide() == Side.TOP || getSide() == Side.BOTTOM) {
            width = calcBarSize(width, value);
        } else {
            height = calcBarSize(height, value);
            y += bounds.height - height;
        }
        Color tempColor = g.getColor();
        g.setColor(barColor);
        g.fillRect(x, y, width, height);
        g.setColor(tempColor);
    }

    /**
     * @see com.t3.client.ui.token.AbstractTokenOverlay#clone()
     */
    @Override
    public Object clone() {
        BarTokenOverlay overlay = new DrawnBarTokenOverlay(getName(), barColor, thickness);
        overlay.setOrder(getOrder());
        overlay.setGroup(getGroup());
        overlay.setMouseover(isMouseover());
        overlay.setOpacity(getOpacity());
        overlay.setIncrements(getIncrements());
        overlay.setSide(getSide());
        overlay.setShowGM(isShowGM());
        overlay.setShowOwner(isShowOwner());
        overlay.setShowOthers(isShowOthers());
        return overlay;
    }

    /** @return Getter for barColor */
    public Color getBarColor() {
        return barColor;
    }

    /** @param barColor Setter for barColor */
    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }

    /** @return Getter for thickness */
    public int getThickness() {
        return thickness;
    }

    /** @param thickness Setter for thickness */
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

}

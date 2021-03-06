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
package com.t3.transferable;

import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import com.t3.MD5Key;

/**
 * Class used to transfer token information between applications. Used in Drag & Drop. Some properties
 * are shared between applications, and some are specific. Those specific properties are stored in the map
 * with a key that indicates what app owns that data. 
 *  
 * @author jgorrell
 * @version $Revision$ $Date$ $Author$
 */
public class TokenTransferData extends HashMap<String, Object> implements Serializable {
    
    /*---------------------------------------------------------------------------------------------
     * Instance Variables
     *-------------------------------------------------------------------------------------------*/
    
    /** Name of the token. */
    private String name;
    
    /** The image used to display the token. An image icon is used because it is serializable */
    private ImageIcon token;
    
    /** The players that own this token. When <code>null</code> there are no owners */
    private Set<String> players;

    /** Flag indicating if this token is visible to players */
    private boolean isVisible;
    
    /** Location of the token on the map. These may be cell coordinates or map coordinates **/
    private Point location;

    /** The facing of the token on the map. A <code>null</code> value indicates no facing */
    private Integer facing;
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables
     *-------------------------------------------------------------------------------------------*/
    
    /** Prefix for all values that are used by map tool */ 
    public final static String T3PREFIX = "t3:";
    
    /** Maptool's token id key. The value is an <code>String</code> that can be used to create a <code>GUID</code> */
    public final static String ID = T3PREFIX + "id";
    
    /** Maptool's Z-order key. The value is an {@link MD5Key} used to identify an asset. */
    public final static String ASSET_ID = T3PREFIX + "assetId";
    
    /** Maptool's Z-order key. The value is an <code>Integer</code>. */
    public final static String Z = T3PREFIX + "z";
    
    /** Maptool's snap to scale key. The value is a <code>Boolean</code>. */
    public final static String SNAP_TO_SCALE = T3PREFIX + "snapToScale";
    
    /** Maptool's token width key. The value is an <code>Integer</code>. */
    public final static String WIDTH = T3PREFIX + "width";
    
    /** Maptool's token height key. The value is an <code>Integer</code>. */
    public final static String HEIGHT = T3PREFIX + "height";
    
    /** 
     * Maptool's snap to grid key. Tells if x,y are cell or zone coordinates. The value 
     * is a <code>Boolean</code>. 
     */
    public final static String SNAP_TO_GRID = T3PREFIX + "snapToGrid";
    
    /** 
     * Maptool's owned by all or just by list key. The value is an <code>Integer</code>.
     * The value 0 means that the token is owned by all, the value 1 indicates that the 
     * owners are specified in the <code>OWNER_LIST</code> property.
     */
    public final static String OWNER_TYPE = T3PREFIX + "ownerType";
    
    /** 
     * Maptool's type of token used by facing or stamping key. The value is a 
     * <code>String</code> containing the name of a <code>Type</code> enumeration value. 
     */
    public final static String TOKEN_TYPE = T3PREFIX + "tokenType";
    
    /** Maptool's notes for all key. The value is a <code>String</code>. */
    public final static String NOTES = T3PREFIX + "notes";
    
    /** Maptool's notes for GM key. The value is a <code>String</code>. */
    public final static String GM_NOTES = T3PREFIX + "gmNotes";
    
    /** Maptool's name for GM key. The value is a <code>String</code>. */
    public final static String GM_NAME = T3PREFIX + "gmName";

    /** Maptool's name for the portrait. The value is an {@link ImageIcon}. */
    public final static String PORTRAIT = T3PREFIX + "portrait";
    
    /** Maptool's name for the portrait. The value is an {@link Map}<code><String, String></code>. */
    public final static String MACROS = T3PREFIX + "macros";
    
    public static final String	STATES	= T3PREFIX + "states";
    
    public static final String	BARS	= T3PREFIX + "bars";
    
    /** Serial version id to hide changes during transfer */
    private static final long serialVersionUID = -1838917777325573062L;

	public static final String VISIBLE_OWNER_ONLY = T3PREFIX + "visibleOwnerOnly";

    /*---------------------------------------------------------------------------------------------
     * Instance Methods
     *-------------------------------------------------------------------------------------------*/
    
    /** @return Getter for isVisible */
    public boolean isVisible() {
        return isVisible;
    }

    /** @param aIsVisible Setter for isVisible */
    public void setVisible(boolean aIsVisible) {
        isVisible = aIsVisible;
    }

    /** @return Getter for name */
    public String getName() {
        return name;
    }

    /** @param aName Setter for name */
    public void setName(String aName) {
        name = aName;
    }

    /** @return Getter for players */
    public Set<String> getPlayers() {
        return players;
    }

    /** @param aPlayers Setter for players */
    public void setPlayers(Set<String> aPlayers) {
        players = aPlayers;
    }

    /** @return Getter for token */
    public ImageIcon getToken() {
        return token;
    }

    /** @param aToken Setter for token */
    public void setToken(ImageIcon aToken) {
        token = aToken;
    }

    /** @return Getter for facing */
    public Integer getFacing() {
        return facing;
    }

    /** @param aFacing Setter for facing */
    public void setFacing(Integer aFacing) {
        facing = aFacing;
    }

    /** @return Getter for location */
    public Point getLocation() {
        return location;
    }

    /** @param aLocation Setter for location */
    public void setLocation(Point aLocation) {
        location = aLocation;
    }
}

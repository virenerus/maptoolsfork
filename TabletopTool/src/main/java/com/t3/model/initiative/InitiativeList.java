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
package com.t3.model.initiative;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import javax.swing.Icon;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.t3.client.AppPreferences;
import com.t3.client.TabletopTool;
import com.t3.guid.GUID;
import com.t3.model.Token;
import com.t3.model.Zone;
import com.t3.util.guidreference.NullHelper;
import com.t3.util.guidreference.TokenReference;
import com.t3.util.guidreference.ZoneReference;
import com.t3.xstreamversioned.version.SerializationVersion;

/**
 * All of the tokens currently being shown in the initiative list. It includes a reference to all
 * the tokens in order, a reference to the current token, a displayable initiative value and a
 * hold state for each token.
 * 
 * @author Jay
 */
@SerializationVersion(1)
public class InitiativeList implements Serializable {

    /*---------------------------------------------------------------------------------------------
     * Instance Variables 
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * The tokens and their order within the initiative
     */
    private List<TokenInitiative> tokens = new ArrayList<TokenInitiative>();

    /**
     * The token in the list which currently has initiative.
     */
    private int current = -1;
    
    /**
     * The current round for initiative.
     */
    private int round = -1;
    
    /**
     * Used to add property change support to the round and current values.
     */
    private transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    
    /**
     * The zone that owns this initiative list, used for persistence
     */
    private ZoneReference zone;
    
    /**
     * Hold the update when this variable is greater than 0. Some methods need to call 
     * {@link #updateServer()} when they are called, but they also get called by other 
     * methods that update the server. This keeps it from happening multiple times.
     */
    private transient int holdUpdate;

    /**
     * Flag indicating that a full update is needed.
     */
    private boolean fullUpdate;
    
    /**
     * Hide all of the NPC's from the players.
     */
    private boolean hideNPC = AppPreferences.getInitHideNpcs();
    
    /*---------------------------------------------------------------------------------------------
     * Class Variables
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * Name of the tokens property passed in {@link PropertyChangeEvent}s.
     */
    public static final String TOKENS_PROP = "tokens";
    
    /**
     * Name of the round property passed in {@link PropertyChangeEvent}s.
     */
    public static final String ROUND_PROP = "round";
    
    /**
     * Name of the current property passed in {@link PropertyChangeEvent}s.
     */
    public static final String CURRENT_PROP = "current";
    
    /**
     * Name of the hide NPCs property passed in {@link PropertyChangeEvent}s.
     */
    public static final String HIDE_NPCS_PROP = "hideNPCs";
    
    /**
     * Name of the owner permission property passed in {@link PropertyChangeEvent}s.
     */
    public static final String OWNER_PERMISSIONS_PROP = "ownerPermissions";
    
    /**
     * Logger for this class
     */
    private static final Logger LOGGER = Logger.getLogger(InitiativeList.class);

    /*---------------------------------------------------------------------------------------------
     * Constructor
     *-------------------------------------------------------------------------------------------*/
        
    /**
     * Create an initiative list for a zone.
     * 
     * @param aZone The zone that owns this initiative list.
     */
    public InitiativeList(Zone aZone) {
        setZone(aZone);
    }
    
    /*---------------------------------------------------------------------------------------------
     * Instance Methods 
     *-------------------------------------------------------------------------------------------*/
        
    /**
     * Get the token initiative data at the passed index. Allows the other state to be set.
     * 
     * @param index Index of the token initiative data needed. 
     * @return The token initiative data for the passed index.
     */
    public TokenInitiative getTokenInitiative(int index) {
        return index < tokens.size() && index >= 0 ? tokens.get(index) : null;
    }

    /**
     * Get the number of tokens in this list.
     * 
     * @return Number of tokens
     */
    public int getSize() {
        return tokens.size();
    }
    
    /**
     * Get the token at the passed index.
     * 
     * @param index Index of the token needed. 
     * @return The token for the passed index.
     */
    public Token getToken(int index) {
        return index >= 0 && index < tokens.size() ? tokens.get(index).getToken() : null;
    }

    /**
     * Insert a new token into the initiative.
     * 
     * @param index Insert the token here.
     * @param token Insert this token.
     * @return The token initiative value that holds the token.
     */
    public TokenInitiative insertToken(int index, Token token) {
        startUnitOfWork();
        TokenInitiative currentInitiative = getTokenInitiative(getCurrent()); // Save the currently selected initiative
        if (index == -1) {
        	index = tokens.size();
        }
        TokenInitiative ti = new TokenInitiative(token);
        ti.setInitiativeList(this);
        tokens.add(index, ti);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, null, ti);
        setCurrent(indexOf(currentInitiative)); // Restore current initiative
        finishUnitOfWork();
        return ti;
    }
    
    /**
     * Insert a new token into the initiative.
     * 
     * @param tokens Insert these tokens.
     */
    public void insertTokens(List<Token>  tokens) {
        startUnitOfWork();
        for (Token token : tokens)
        	insertToken(-1, token);
        finishUnitOfWork();
    }
    
    /**
     * Find the index of the passed token.
     * 
     * @param token Search for this token.
     * @return A list of the indexes found for the listed token
     */
    public List<Integer> indexOf(Token token) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < tokens.size(); i++)
            if (token.equals(tokens.get(i).getToken()))
            	list.add(i);
        return list;
    }
    
    /**
     * Searches for the passed token in the list.
     * 
     * @param token Search for this token.
     * @return if this token is contained in the list
     */
    public boolean contains(Token token) {
        for (int i = 0; i < tokens.size(); i++)
            if (token.equals(tokens.get(i).getToken()))
            	return true;
        return false;
    }
    
    /**
     * Find the index of the passed token initiative.
     * 
     * @param ti Search for this token initiative instance
     * @return The index of the token initiative that was found or -1 if the token initiative was not found;
     */
    public int indexOf(TokenInitiative ti) {
        for (int i = 0; i < tokens.size(); i++)
            if (tokens.get(i).equals(ti))
            	return i;
        return -1;
    }
    
    /**
     * Remove a token from the initiative.
     * 
     * @param index Remove the token at this index.
     * @return The token that was removed.
     */
    public Token removeToken(int index) {

        // If we are deleting the token with initiative, drop back to the previous token, if we're at the beginning, clear current
        startUnitOfWork();
        TokenInitiative currentInitiative = getTokenInitiative(getCurrent()); // Save the currently selected initiative
        int currentInitIndex = indexOf(currentInitiative);
        if (currentInitIndex == index) {
            if (tokens.size() == 1) {
                currentInitiative = null;
            } if (index == 0) {
                currentInitiative = getTokenInitiative(1);
            } else {
                currentInitiative = getTokenInitiative(currentInitIndex - 1);
            } // endif
        } // endif

        TokenInitiative ti = tokens.remove(index);
        Token old = ti.getToken();        
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, ti, null);
        setCurrent(indexOf(currentInitiative)); // Restore current initiative
        finishUnitOfWork();
        return old; 
    }
    
    /** @return Getter for current */
    public int getCurrent() {
        return current;
    }
    
    /** @param aCurrent Setter for the current to set */
    public void setCurrent(int aCurrent) {
        if (current == aCurrent)
        	return;
        startUnitOfWork();
        if (aCurrent < 0 || aCurrent >= tokens.size())
        	aCurrent = -1; // Don't allow bad values
        int old = current;
        current = aCurrent;
        getPCS().firePropertyChange(CURRENT_PROP, old, current);
        finishUnitOfWork();
    }
    
    /**
     * Go to the next token in initiative order.
     */
    public void nextInitiative() {
        if (tokens.isEmpty())
        	return;
        startUnitOfWork();
        int newRound = (round < 0) ? 1 : (current + 1 >= tokens.size()) ? round + 1 : round;
        int newCurrent = (current < 0 || current + 1 >= tokens.size()) ? 0 : current + 1;
        setCurrent(newCurrent);
        setRound(newRound);
        finishUnitOfWork();
    }

    /**
     * Go to the previous token in initiative order.
     */
    public void prevInitiative() {
        if (tokens.isEmpty())
            return;
        startUnitOfWork();
        int newRound = (round < 2) ? 1 : (current - 1 < 0) ? round - 1 : round;
        int newCurrent = (current < 1) ? (round < 2 ? 0 : tokens.size() - 1): current - 1;
        setCurrent(newCurrent);
        setRound(newRound);
        finishUnitOfWork();
    }

    /** @return Getter for round */
    public int getRound() {
        return round;
    }

    /** @param aRound Setter for the round to set */
    public void setRound(int aRound) {
        if (round == aRound)
        	return;
        startUnitOfWork();
        int old = round;
        round = aRound;
        getPCS().firePropertyChange(ROUND_PROP, old, aRound);
        finishUnitOfWork();
    }
    
    /**
     * Add a listener to any property change.
     * 
     * @param listener The listener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(listener);
    }

    /**
     * Add a listener to the given property name
     * 
     * @param propertyName Add the listener to this property name.
     * @param listener The listener to be added.
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPCS().addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Remove a listener for all property changes.
     * 
     * @param listener The listener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(listener);
    }

    /**
     * Remove a listener from a given property name
     * 
     * @param propertyName Remove the listener from this property name.
     * @param listener The listener to be removed.
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        getPCS().removePropertyChangeListener(propertyName, listener);
    }
    
    /**
     * Start a new unit of work.
     */
    public void startUnitOfWork() {
        holdUpdate += 1;
        if (holdUpdate == 1) 
            fullUpdate = false;
        LOGGER.debug("startUnitOfWork(): " + holdUpdate + " full: " + fullUpdate);
    }
    
    /**
     * Finish the current unit of work and update the server.
     */
    public void finishUnitOfWork() {
        fullUpdate = true;
        finishUnitOfWork(null);
    }
    
    /**
     * Finish the current unit of work on a single initiative item and update the server.
     * 
     * @param ti Only need to update this token initiative.
     */
    public void finishUnitOfWork(TokenInitiative ti) {
        assert holdUpdate > 0 : "Trying to close unit of work when one is not open.";
        holdUpdate -= 1;
        LOGGER.debug("finishUnitOfWork(" + (ti == null ? "" : ti.getId().toString()) + "): = " + holdUpdate + " full: " + fullUpdate);
        if (holdUpdate == 0) {
            if (fullUpdate || ti == null) {
                updateServer();
            } else {
                updateServer(ti);
            } // endif
        } // endif
    }
    
    /**
     * Remove all of the tokens from the model and clear round and current 
     */
    public void clearModel() {
        if (current == -1 && round == -1 && tokens.isEmpty())
        	return;
        startUnitOfWork();
        setCurrent(-1);
        setRound(-1);
        if (!tokens.isEmpty()) {
            List<TokenInitiative> old = tokens;
            tokens = new ArrayList<TokenInitiative>();
            getPCS().firePropertyChange(TOKENS_PROP, old, tokens);
        } // endif
        finishUnitOfWork();
    }

    /**
     * Updates occurred to the tokens. 
     */
    public void update() {
        
        // No zone, no tokens
        if (getZone() == null) {
            clearModel();
            return;
        } // endif
        
        // Remove deleted tokens
        startUnitOfWork();
        boolean updateNeeded = false;
        ListIterator<TokenInitiative> i = tokens.listIterator();
        while (i.hasNext()) {
            TokenInitiative ti = i.next();
            if (!ti.tokenExists()) {
                int index = tokens.indexOf(ti);
                if (index <= current)
                	setCurrent(current - 1);
                i.remove();
                updateNeeded = true;
                getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, ti, null);
            } // endif
        } // endwhile
        if (updateNeeded) {
            finishUnitOfWork();
        } else if (holdUpdate == 1) {
            holdUpdate -= 1; // Do no updates.
            LOGGER.debug("finishUnitOfWork() - no update");
        } // endif
    }
    
    /**
     * Sort the tokens by their initiative state from largest to smallest. If the initiative state string can be converted into a 
     * {@link Double} that is done first. All values converted to {@link Double}s are always considered bigger than the {@link String}
     * values. The {@link String} values are considered bigger than any <code>null</code> values.
     */
    public void sort() {
        startUnitOfWork();
        TokenInitiative currentInitiative = getTokenInitiative(getCurrent()); // Save the currently selected initiative
        Collections.sort(tokens);
        getPCS().firePropertyChange(TOKENS_PROP, null, tokens);
        setCurrent(indexOf(currentInitiative)); // Restore current initiative
        finishUnitOfWork();
    }
    
    /** @return Getter for zone */
    public Zone getZone() {
        return NullHelper.value(zone);
    }

    /** @return Getter for pcs */
    private PropertyChangeSupport getPCS() {
        if (pcs == null)
        	pcs = new PropertyChangeSupport(this);
        return pcs;
    }
    
    /**
     * Move a token from it's current position to the new one.
     * 
     * @param oldIndex Move the token at this index
     * @param index To here.
     */
    public void moveToken(int oldIndex, int index) {
        
        // Bad index, same index, oldIndex->oldindex+1, or moving the last token to the end of the list do nothing.
        if (oldIndex < 0 || oldIndex == index || (oldIndex == tokens.size() - 1 && index == tokens.size()) || oldIndex == (index-1))
            return;
        
        // Save the current position, the token moves but the initiative does not.
        TokenInitiative newInitiative = null;
        TokenInitiative currentInitiative = getTokenInitiative(getCurrent()); // Save the current initiative
        if (oldIndex == current) {
        	newInitiative = getTokenInitiative(oldIndex != 0 ? oldIndex -1 : 1);
        	current = (oldIndex != 0 ? oldIndex -1 : 1);
        }

        startUnitOfWork();
        current = -1;
        TokenInitiative ti = tokens.remove(oldIndex);
        ti.setInitiativeList(this);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, oldIndex, ti, null);
        
        // Add it at it's new position
        index -= index > oldIndex ? 1 : 0;
        tokens.add(index, ti);
        getPCS().fireIndexedPropertyChange(TOKENS_PROP, index, null, ti);

        // Set/restore proper initiative
        if (newInitiative == null)
        	current = indexOf(currentInitiative);
        else 
        	setCurrent(indexOf(newInitiative)); 
        finishUnitOfWork();
    }
    
    /**
     * Update the server with the new list
     */
    public void updateServer() {
        if (zone== null)
        	return;
        LOGGER.debug("Full update");
        TabletopTool.serverCommand().updateInitiative(this, null);
    }

    /**
     * Update the server with the new Token Initiative
     * 
     * @param ti Item to update
     */
    public void updateServer(TokenInitiative ti) {
        if (zone == null)
            return;
        LOGGER.debug("Token Init update: " + ti.getId());
        TabletopTool.serverCommand().updateTokenInitiative(zone.getId(), ti.getId(), ti.isHolding(), ti.getRawState(), indexOf(ti));
    }

    /** @param aZone Setter for the zone */
    public void setZone(Zone aZone) {
        zone = NullHelper.referenceZone(aZone);
    }

    /** @return Getter for hideNPC */
    public boolean isHideNPC() {
        return hideNPC;
    }

    /** @param hide Setter for hideNPC */
    public void setHideNPC(boolean hide) {
        if (hide == hideNPC)
        	return;
        startUnitOfWork();
        boolean old = hideNPC;
        hideNPC = hide;
        getPCS().firePropertyChange(HIDE_NPCS_PROP, old, hide);
        finishUnitOfWork();
    }

    /** @return Getter for tokens */
    public List<TokenInitiative> getTokens() {
        return Collections.unmodifiableList(tokens);
    }

    /*---------------------------------------------------------------------------------------------
     * TokenInitiative Inner Class
     *-------------------------------------------------------------------------------------------*/
    
    /**
     * This class holds all of the data to describe a token w/in initiative.
     * 
     * @author Jay
     */
    @SerializationVersion(1)
    public static class TokenInitiative implements Comparable<TokenInitiative> {
        
        /*---------------------------------------------------------------------------------------------
         * Instance Variables 
         *-------------------------------------------------------------------------------------------*/
        
        /**
         * The token which is needed for persistence. It is immutable.
         */
        private TokenReference token;
        
        /**
         * Flag indicating that the token is holding it's initiative.
         */
        private boolean holding;
        
        /**
         * Optional state that can be displayed in the initiative panel. 
         */
        private InitiativeValue state;
        
        /**
         * Save off the icon so that it can be displayed as needed.
         */
        private transient Icon displayIcon;
        
        private InitiativeList initiativeList;

        /*---------------------------------------------------------------------------------------------
         * Constructors
         *-------------------------------------------------------------------------------------------*/
        
        /**
         * Create the token initiative for the passed token.
         * 
         * @param aToken Add this token to the initiative.
         */
        public TokenInitiative(Token aToken) {
        	token=NullHelper.referenceToken(aToken);
        }
        
        /*---------------------------------------------------------------------------------------------
         * Instance Methods 
         *-------------------------------------------------------------------------------------------*/
        
        public boolean tokenExists() {
			return token.isValid();
		}

		/** @return Getter for token */
        public Token getToken() {
            return NullHelper.value(token);
        }


        /** @return Getter for id */
        public GUID getId() {
            return NullHelper.getId(token);
        }

        /** @return Getter for holding */
        public boolean isHolding() {
            return holding;
        }

        /** @param isHolding Setter for the holding to set */
        public void setHolding(boolean isHolding) {
            if (holding == isHolding)
            	return;
            initiativeList.startUnitOfWork();
            boolean old = holding;
            holding = isHolding;
            initiativeList.getPCS().fireIndexedPropertyChange(TOKENS_PROP, initiativeList.tokens.indexOf(this), old, isHolding);
            initiativeList.finishUnitOfWork(this);
        }

        /** @return Getter for state */
        public Object getState() {
        	if(state==null)
        		return null;
        	else
        		return state.getValue();
        }
        
        /** @return Getter for state */
        public InitiativeValue getRawState() {
            return state;
        }

        public void setState(String state) {
        	this.setState(InitiativeValue.create(state));
        }
        
        public void setState(Number state) {
        	this.setState(InitiativeValue.create(state));
        }
        
        /** This method accepts a string as the new initiative value but it will try to convert it into a number first*/
        public void setUnparsedState(String state) {
			try {
				this.setState(Integer.valueOf(state));
			} catch(NumberFormatException e) {
				try {
					this.setState(Double.valueOf(state));
				} catch(NumberFormatException e2) {
					if(StringUtils.isBlank(state))
						this.setState((InitiativeValue)null);
					else
						this.setState(state);
				}
			}
		}
        
        /** @param aState Setter for the state to set */
        public void setState(InitiativeValue aState) {
            if (state == aState || (state != null && state.equals(aState)))
            	return;
            initiativeList.startUnitOfWork();
            Object old = state;
            state = aState;
            initiativeList.getPCS().fireIndexedPropertyChange(TOKENS_PROP, initiativeList.tokens.indexOf(this), old, aState);
            initiativeList.finishUnitOfWork(this);
        }

        /** @return Getter for displayIcon */
        public Icon getDisplayIcon() {
            return displayIcon;
        }

        /** @param displayIcon Setter for the displayIcon to set */
        public void setDisplayIcon(Icon displayIcon) {
            this.displayIcon = displayIcon;
        }
        
        public void update(boolean isHolding, String aState) {
        	this.update(isHolding, InitiativeValue.create(aState));
        }
        
        public void update(boolean isHolding, Number aState) {
        	this.update(isHolding, InitiativeValue.create(aState));
        }
        
        /**
         * Update the internal state w/o firing events. Needed for single token 
         * init updates. 
         * 
         * @param isHolding New holding state
         * @param aState New state
         */
        public void update(boolean isHolding, InitiativeValue aState) {
            boolean old = holding;
            holding = isHolding;
            Object oldState = state;
            state = aState;
            initiativeList.getPCS().fireIndexedPropertyChange(TOKENS_PROP, initiativeList.tokens.indexOf(this), old, isHolding);
            initiativeList.getPCS().fireIndexedPropertyChange(TOKENS_PROP, initiativeList.tokens.indexOf(this), oldState, aState);
        }

		public void setInitiativeList(InitiativeList initiativeList) {
			this.initiativeList = initiativeList;
		}

		@Override
		public int compareTo(TokenInitiative o) {
			if(o==null)
				return 1;
			else
				return state.compareTo(o.state);
		}
    }
}

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
package com.t3.swing.preference;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

import com.t3.swing.TaskPanel;
import com.t3.swing.TaskPanelGroup;

public class TaskPanelGroupPreferences implements PropertyChangeListener {

	private TaskPanelGroup group;

	private Preferences prefs;
	private boolean restoringState; // I don't like this, rethink it later.
	
	private static final String PREF_KEY = "state_list";
	
	public TaskPanelGroupPreferences(String appName, String controlName, TaskPanelGroup group) {
		this.group = group;
		
        prefs = Preferences.userRoot().node(appName + "/control/" + controlName);        
		
    	restoreTaskPanelStates();
        connect();
	}
	
	protected void connect() {

		for (TaskPanel taskPanel : group.getTaskPanels()) {
			connectToTaskPanel(taskPanel);
		}
		
		// Make sure to get all future task panels
		group.addPropertyChangeListener(TaskPanelGroup.TASK_PANEL_LIST, this);
	}
	
	private void connectToTaskPanel(TaskPanel taskPanel) {
		
		taskPanel.addPropertyChangeListener(TaskPanel.TASK_PANEL_STATE, this);
	}
	
	public void disconnect() {
		
		group.removePropertyChangeListener(TaskPanelGroup.TASK_PANEL_LIST, this);

		for (TaskPanel taskPanel : group.getTaskPanels()) {
			disconnectFromTaskPanel(taskPanel);
		}

		group = null;
		prefs = null;
	}

	private void disconnectFromTaskPanel(TaskPanel taskPanel) {
		
		taskPanel.removePropertyChangeListener(TaskPanel.TASK_PANEL_STATE, this);
	}

	private void saveTaskPanelStates() {
		
		List<String> stateList = new ArrayList<String>();
		
		for (TaskPanel taskPanel : group.getTaskPanels()) {
			stateList.add(taskPanel.getTitle());
			stateList.add(taskPanel.getState().name());
		}
		
		saveStates(stateList);
	}
	
	private void restoreTaskPanelState(TaskPanel taskPanel) {
		
		List<String> states = loadStates();
		
		try {
			restoringState = true;
			for (Iterator<String> iter = states.iterator(); iter.hasNext();) {
				
				String title = iter.next();
				String state = iter.next();
				
				if (taskPanel.getTitle().equals(title)) {
					taskPanel.setState(TaskPanel.State.valueOf(state));
					break;
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        	restoringState = false;
        }
	}
	
	private void restoreTaskPanelStates() {
		
		List<String> states = loadStates();
		
		try {
			restoringState = true;
			for (Iterator<String> iter = states.iterator(); iter.hasNext();) {
				
				String title = iter.next();
				String state = iter.next();
	
				TaskPanel taskPanel = group.getTaskPanel(title);
				
				if (taskPanel == null) {
					continue;
				}
				
				taskPanel.setState(TaskPanel.State.valueOf(state));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			restoringState = false;
		}
	}
	
	private List<String> loadStates() {
		
		String stateList = prefs.get(PREF_KEY, null);
		if (stateList == null) {
			return new ArrayList<String>();
		}
		
		String[] states = stateList.split("\\|");
		
		return Arrays.asList(states);
	}
	
	private void saveStates(List<String> stateList) {
		
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iter = stateList.iterator(); iter.hasNext();) {
			
			builder.append(iter.next()).append("|").append(iter.next());
			
			if (iter.hasNext()) {
				builder.append("|");
			}
		}
		
		prefs.put(PREF_KEY, builder.toString());
	}
	
	////
	// PROPERTY CHANGE LISTENER
	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (restoringState) {
			return;
		}
		
		if (TaskPanelGroup.TASK_PANEL_LIST.equals(evt.getPropertyName())) {
			TaskPanel taskPanel = (TaskPanel) evt.getNewValue();
			connectToTaskPanel(taskPanel);
			restoreTaskPanelState(taskPanel);
		} else {
			
			saveTaskPanelStates();
		}
	}
}

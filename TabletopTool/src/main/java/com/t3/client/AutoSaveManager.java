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
package com.t3.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.Timer;

import org.apache.log4j.Logger;

import com.t3.language.I18N;
import com.t3.model.campaign.Campaign;
import com.t3.persistence.PersistenceUtil;

/**
 * @author tylere
 * 
 *         Attempts to recover campaigns when the application crashes.
 */
public class AutoSaveManager implements ActionListener {
	private static final Logger log = Logger.getLogger(AutoSaveManager.class);
	private Timer autoSaveTimer;
	public static final File AUTOSAVE_FILE = new File(AppUtil.getAppHome("autosave"), "AutoSave" + AppConstants.CAMPAIGN_FILE_EXTENSION); //$NON-NLS-1$

	public void start() {
		restart();
	}

	/**
	 * Queries the auto-save increment from {@link AppPreferences} and starts a new timer.
	 */
	public void restart() {
		int interval = AppPreferences.getAutoSaveIncrement();

		//convert to milliseconds
		int delay = interval * 60 * 1000;
		if (log.isDebugEnabled())
			log.debug("Starting autosave manager; interval in seconds is " + (interval * 60)); //$NON-NLS-1$

		if (autoSaveTimer == null) {
			if (interval <= 0) { // auto-save is turned off with <= 0
				return;
			} else {
				autoSaveTimer = new Timer(delay, this);
				autoSaveTimer.start(); // Start it running...
			}
		} else {
			if (interval <= 0) {
				autoSaveTimer.stop(); // auto-save is off; stop the Timer first
				autoSaveTimer = null;
			} else {
				autoSaveTimer.setDelay(delay);
				autoSaveTimer.restart(); // Set the new delay and restart the Timer
			}
		}
	}

	/**
	 * Applications can use this to pause the timer. The {@link #restart()} method can be called at any time to reset
	 * and start the timer.
	 */
	public void pause() {
		if (autoSaveTimer != null && autoSaveTimer.isRunning())
			autoSaveTimer.stop();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Don't autosave if we don't "own" the campaign
		if (!TabletopTool.isHostingServer() && !TabletopTool.isPersonalServer()) {
			return;
		}

		if (AppState.isSaving()) {
			log.debug("Canceling autosave because user has initiated save operation"); //$NON-NLS-1$
			return;
		}
		try {
			TabletopTool.getFrame().setStatusMessage(I18N.getString("AutoSaveManager.status.autoSaving"));
			long startCopy = System.currentTimeMillis();

			// This occurs on the event dispatch thread, so it's ok to mess with the models.
			// We need to clone the campaign so that we can save in the background, but
			// not have concurrency issues with the original model.
			//
			// NOTE: This is a cheesy way to clone the campaign, but it makes it so that I
			// don't have to keep all the various models' clone methods updated on each change.
			final Campaign campaign = new Campaign(TabletopTool.getCampaign());
			if (log.isInfoEnabled())
				log.info("Time to copy Campaign object (ms): " + (System.currentTimeMillis() - startCopy)); //$NON-NLS-1$

			// Now that we have a copy of the model, save that one
			// TODO: Replace this with a swing worker
			new Thread(null, new Runnable() {
				@Override
				public void run() {
					AppState.setIsSaving(true);
					pause();
					long startSave = System.currentTimeMillis();
					try {
						PersistenceUtil.saveCampaign(campaign, AUTOSAVE_FILE);
						TabletopTool.getFrame().setStatusMessage(I18N.getText("AutoSaveManager.status.autoSaveComplete", System.currentTimeMillis() - startSave));
					} catch (IOException ioe) {
						TabletopTool.showError("AutoSaveManager.failed", ioe);
					} catch (Throwable t) {
						TabletopTool.showError("AutoSaveManager.failed", t);
					} finally {
						AppState.setIsSaving(false);
					}
				}
			}, "AutoSaveThread").start();
		} catch (Throwable t) {
			// If this routine fails, be sure the isSaving is turned off.  This should not be necessary:
			// If the exception occurs anywhere before the .start() method of Thread, the boolean
			// does not need to be reset anyway.  And if the .start() method is successful, this code
			// will never be invoked, in which case the .run() method will decide when to set/reset
			// the flag.  For safety's sake I retrieve the current value and report it if it's true, but
			// we shouldn't be able to get here in that case...
			if (AppState.isSaving()) {
				TabletopTool.showError(I18N.getString("AutoSaveManager.failed") + "<br/>\nand AppState.isSaving() is true!", t);
				AppState.setIsSaving(false);
			} else {
				TabletopTool.showError("AutoSaveManager.failed", t);
			}
		}
	}

	/**
	 * Removes any autosaved files
	 */
	public void purge() {
		if (AUTOSAVE_FILE.exists()) {
			AUTOSAVE_FILE.delete();
		}
	}

	/**
	 * Removes the campaignFile if it's from Autosave, forcing to save as new
	 */
	public void tidy() {
		if (AUTOSAVE_FILE.equals(AppState.getCampaignFile())) {
			AppState.setCampaignFile(null);
		}
		purge();
	}

	/**
	 * Check to see if autosave recovery is necessary.
	 */
	public void check() {
		if (AUTOSAVE_FILE.exists()) {
			boolean okay;
			okay = TabletopTool.confirm("msg.confirm.recoverAutosave", AUTOSAVE_FILE.lastModified());
			if (okay)
				AppActions.loadCampaign(AUTOSAVE_FILE);
		}
	}
}

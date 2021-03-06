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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.t3.client.ui.commandpanel.CommandPanel;
import com.t3.language.I18N;

/**
 * @author frank
 */
public class ChatAutoSave {
	private static Logger log = Logger.getLogger(ChatAutoSave.class);
	private static final ChatAutoSave self = new ChatAutoSave();

	private final Timer countdown;
	private TimerTask task;
	private long delay;
	private static String chatlog = null;

	private ChatAutoSave() {
		log.debug("Creating chat log autosave timer"); //$NON-NLS-1$
		// Only way to set the delay is to call changeTimeout()
		delay = 0;
		countdown = new Timer();
	}

	private static TimerTask createTimer(final long timeout) {
		TimerTask t = new TimerTask() {
			@Override
			public void run() {
				if (log.isDebugEnabled())
					log.debug("Chat log autosave countdown complete from " + timeout); //$NON-NLS-1$
				if (chatlog == null) {
					String filename = AppPreferences.getChatFilenameFormat();
					// FJE Ugly kludge to replace older default entry with newer default
					// TODO This is going into 1.3.b77 so remove it in 3-4 builds
					if ("chatlog.html".equals(filename)) { //$NON-NLS-1$
						AppPreferences.clearChatFilenameFormat();
						filename = AppPreferences.getChatFilenameFormat();
					}
					chatlog = String.format(filename, new Date()).replace(':', '-');
				}
				File chatFile = new File(AppUtil.getAppHome("autosave").toString(), chatlog); //$NON-NLS-1$
				if (log.isInfoEnabled())
					log.info("Saving log to '" + chatFile + "'"); //$NON-NLS-1$ //$NON-NLS-2$

				CommandPanel chat = TabletopTool.getFrame().getCommandPanel();
				String old = TabletopTool.getFrame().getStatusMessage();
				try (FileWriter writer = new FileWriter(chatFile)){
					TabletopTool.getFrame().setStatusMessage(I18N.getString("ChatAutoSave.status.chatAutosave")); //$NON-NLS-1$
					
					writer.write(chat.getMessageHistory());
					if (log.isInfoEnabled())
						log.info("Log saved"); //$NON-NLS-1$
				} catch (IOException e) {
					// If this happens should we track it and turn off the autosave?  Perhaps
					// after a certain number of consecutive failures?  Or maybe just lengthen
					// the amount of time between attempts in that case?  At a minimum we
					// should probably give the user a chance to turn it off as part of this
					// message box that pops up...
					TabletopTool.showWarning("msg.warn.failedAutoSavingMessageHistory", e); //$NON-NLS-1$
				} finally {
					TabletopTool.getFrame().setStatusMessage(old);
				}
			}
		};
		return t;
	}

	private static ChatAutoSave getInstance() {
		return self;
	}

	public static void changeTimeout(int timeout) {
		getInstance().delay = timeout * 1000 * 60;
		getInstance().start();
	}

	private void stop() {
		if (task != null) {
			task.cancel();
			task = null;
		}
	}

	private void start() {
		if (delay > 0) {
			stop();
			task = createTimer(delay);
			countdown.schedule(task, 5000, delay); // Wait 5s, then save the log every 'delay' ms
		}
	}
}

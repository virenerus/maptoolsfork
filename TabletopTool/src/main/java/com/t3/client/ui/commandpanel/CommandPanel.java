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
package com.t3.client.ui.commandpanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import org.apache.log4j.Logger;

import com.t3.AppEvent;
import com.t3.AppEventListener;
import com.t3.client.AppActions;
import com.t3.client.AppPreferences;
import com.t3.client.AppStyle;
import com.t3.client.T3MacroContext;
import com.t3.client.TabletopTool;
import com.t3.client.ui.chat.ChatProcessor;
import com.t3.client.ui.chat.SmileyChatTranslationRuleGroup;
import com.t3.image.ImageUtil;
import com.t3.model.ObservableList;
import com.t3.model.Token;
import com.t3.model.chat.PlayerSpeaker;
import com.t3.model.chat.Speaker;
import com.t3.model.chat.TextMessage;
import com.t3.model.chat.TokenSpeaker;
import com.t3.swing.SwingUtil;
import com.t3.util.ImageManager;
import com.t3.util.StringUtil;
import com.t3.util.guidreference.NullHelper;
import com.t3.util.guidreference.TokenReference;

public class CommandPanel extends JPanel implements Observer {
	private static final long serialVersionUID = 8710948417044703674L;

	private final List<String> commandHistory = new LinkedList<String>();
	
	private final static Logger log=Logger.getLogger(CommandPanel.class);

	private JLabel characterLabel;
	private JTextPane commandTextArea;
	private MessagePanel messagePanel;
	private int commandHistoryIndex;
	private TextColorWell textColorWell;
	private JToggleButton scrollLockButton;
	private JToggleButton chatNotifyButton;
	private AvatarPanel avatarPanel;
	private JPopupMenu emotePopup;
	private JButton emotePopupButton;
	private String typedCommandBuffer;

	// Chat timers
//	private long chatNotifyDuration; // Initialize it on first load
//	private Timer chatTimer;

	private ChatProcessor chatProcessor;

	private TokenReference impersonatedToken;

	public CommandPanel() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createLineBorder(Color.gray));

		add(BorderLayout.SOUTH, createSouthPanel());
		add(BorderLayout.CENTER, getMessagePanel());
		initializeSmilies();
		addFocusHotKey();
	}

	public ChatProcessor getChatProcessor() {
		return chatProcessor;
	}

	private void initializeSmilies() {
		SmileyChatTranslationRuleGroup smileyRuleGroup = new SmileyChatTranslationRuleGroup();
		emotePopup = smileyRuleGroup.getEmotePopup();

		chatProcessor = new ChatProcessor();
		chatProcessor.install(smileyRuleGroup);
	}

	/**
	 * Whether the player is currently impersonating a token
	 */
	public boolean isImpersonating() {
		return impersonatedToken != null;
	}
	
	/**
	 * The name currently in use; if the user is not impersonating a token, this will return the player's name.
	 */
	public Speaker getSpeaker() {
		if (impersonatedToken == null)
			return new PlayerSpeaker(TabletopTool.getPlayer());
		else if(!impersonatedToken.isValid()) {
			this.setImpersonatedToken(null);
			return new PlayerSpeaker(TabletopTool.getPlayer());
		}

		return new TokenSpeaker(impersonatedToken.getId());
	}

	public Token getImpersonatedToken() {
		return NullHelper.value(impersonatedToken);
	}

	public void setImpersonatedToken(TokenReference token) {
		if (token != null) {
			impersonatedToken = token;
			avatarPanel.setImage(ImageManager.getImageAndWait(token.value().getImageAssetId()));
			setCharacterLabel("Speaking as: " + getSpeaker().toString());
		} else {
			impersonatedToken = null;
			avatarPanel.setImage(null);
			setCharacterLabel("");
		}
	}

	public JButton getEmotePopupButton() {
		if (emotePopupButton == null) {
			try {
				emotePopupButton = new JButton(new ImageIcon(ImageUtil.getImage("com/t3/client/image/smiley/emsmile.png")));
				emotePopupButton.setMargin(new Insets(0, 0, 0, 0));
				emotePopupButton.setContentAreaFilled(false);
				emotePopupButton.setBorderPainted(false);
				emotePopupButton.setFocusPainted(false);
				emotePopupButton.setOpaque(false);
				emotePopupButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						emotePopup.show(emotePopupButton, 0, 0);
					}
				});
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return emotePopupButton;
	}

	public JToggleButton getScrollLockButton() {
		if (scrollLockButton == null) {
			scrollLockButton = new JToggleButton();
			scrollLockButton.setIcon(new ImageIcon(AppStyle.chatScrollImage));
			scrollLockButton.setSelectedIcon(new ImageIcon(AppStyle.chatScrollLockImage));
			scrollLockButton.setToolTipText("Scroll lock");
			scrollLockButton.setUI(new BasicToggleButtonUI());
			scrollLockButton.setBorderPainted(false);
			scrollLockButton.setFocusPainted(false);
			scrollLockButton.setPreferredSize(new Dimension(16, 16));
		}
		return scrollLockButton;
	}

	/**
	 * Gets the button for sending or suppressing notification that a player is typing in the chat text area.
	 * 
	 * @return the notification button
	 */

	public JToggleButton getNotifyButton() {
		if (chatNotifyButton == null) {
			chatNotifyButton = new JToggleButton();
			chatNotifyButton.setIcon(new ImageIcon(AppStyle.showTypingNotification));
			chatNotifyButton.setSelectedIcon(new ImageIcon(AppStyle.hideTypingNotification));
			chatNotifyButton.setToolTipText("Show/hide typing notification");
			chatNotifyButton.setUI(new BasicToggleButtonUI());
			chatNotifyButton.setBorderPainted(false);
			chatNotifyButton.setFocusPainted(false);
			chatNotifyButton.setPreferredSize(new Dimension(16, 16));
			chatNotifyButton.addItemListener(new ItemListener() {
				private ChatTypingListener ours = null;

				@Override
				public void itemStateChanged(ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						if (ours != null)
							commandTextArea.removeKeyListener(ours);
						ours = null;
						// Go ahead and turn off the chat panel right away.
						TabletopTool.getFrame().getChatTypingPanel().setVisible(false);
					} else if (e.getStateChange() == ItemEvent.DESELECTED) {
						if (ours == null)
							ours = new ChatTypingListener();
						commandTextArea.addKeyListener(ours);
					}
				}
			});
		}
		return chatNotifyButton;
	}

	private JComponent createSouthPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel subPanel = new JPanel(new BorderLayout());

		subPanel.add(BorderLayout.EAST, createTextPropertiesPanel());
		subPanel.add(BorderLayout.NORTH, createCharacterLabel());
		subPanel.add(BorderLayout.CENTER, createCommandPanel());

		panel.add(BorderLayout.WEST, createAvatarPanel());
		panel.add(BorderLayout.CENTER, subPanel);

		return panel;
	}

	private JComponent createAvatarPanel() {
		avatarPanel = new AvatarPanel(new Dimension(60, 60));
		return avatarPanel;
	}

	private JComponent createCommandPanel() {
		JScrollPane pane = new JScrollPane(getCommandTextArea(), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		return pane;
	}

	private JComponent createTextPropertiesPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;

		panel.add(getTextColorWell(), constraints);

		// constraints.gridy++;
		// panel.add(Box.createVerticalStrut(2), constraints);

		constraints.gridy++;
		panel.add(getScrollLockButton(), constraints);

		constraints.gridx = 1;
		constraints.gridy = 0;
		panel.add(getEmotePopupButton(), constraints);

		constraints.gridy = 1;
		panel.add(getNotifyButton(), constraints);

		return panel;
	}

	public String getMessageHistory() {
		return messagePanel.getMessagesText();
	}

	public void setCharacterLabel(String label) {
		characterLabel.setText(label);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50, 50);
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public JLabel createCharacterLabel() {
		characterLabel = new JLabel("", JLabel.LEFT);
		characterLabel.setText("");
		characterLabel.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
		return characterLabel;
	}

	/**
	 * Creates the label for live typing.
	 * 
	 * @return
	 */

	public JTextPane getCommandTextArea() {
		if (commandTextArea == null) {
			commandTextArea = new JTextPane() {
				@Override
				protected void paintComponent(Graphics g) {
					super.paintComponent(g);

					Dimension size = getSize();
					g.setColor(Color.gray);
					g.drawLine(0, 0, size.width, 0);
				}
			};
			commandTextArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			commandTextArea.setPreferredSize(new Dimension(50, 40)); // XXX should be resizable
			commandTextArea.setFont(new Font("sans-serif", 0, AppPreferences.getFontSize()));
			commandTextArea.addKeyListener(new ChatTypingListener());
			SwingUtil.useAntiAliasing(commandTextArea);

			ActionMap actions = commandTextArea.getActionMap();
			actions.put(AppActions.COMMIT_COMMAND_ID, AppActions.COMMIT_COMMAND);
			actions.put(AppActions.ENTER_COMMAND_ID, AppActions.ENTER_COMMAND);
			actions.put(AppActions.CANCEL_COMMAND_ID, AppActions.CANCEL_COMMAND);
			actions.put(AppActions.COMMAND_UP_ID, new CommandHistoryUpAction());
			actions.put(AppActions.COMMAND_DOWN_ID, new CommandHistoryDownAction());

			InputMap inputs = commandTextArea.getInputMap();
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), AppActions.CANCEL_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), AppActions.COMMIT_COMMAND_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), AppActions.COMMAND_UP_ID);
			inputs.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), AppActions.COMMAND_DOWN_ID);

			// Resize on demand
			TabletopTool.getEventDispatcher().addListener(TabletopTool.PreferencesEvent.Changed, new AppEventListener() {
				@Override
				public void handleAppEvent(AppEvent event) {
					commandTextArea.setFont(commandTextArea.getFont().deriveFont((float) AppPreferences.getFontSize()));
					doLayout();
				}
			});
		}
		return commandTextArea;
	}

	/**
	 * KeyListener for command area to handle live typing notification. Implements an idle timer that removes the typing
	 * notification after the duration set in AppPreferences expires.
	 */
	private class ChatTypingListener extends KeyAdapter {
		@Override
		public void keyReleased(KeyEvent kre) {
			// Get the key released
			int key = kre.getKeyCode();

			if (key == KeyEvent.VK_ENTER) {
				// User hit enter, reset the label and stop the timer
				TabletopTool.serverCommand().setLiveTypingLabel(TabletopTool.getPlayer().getName(), false);
			} else if (!chatNotifyButton.isSelected()) {
				TabletopTool.serverCommand().setLiveTypingLabel(TabletopTool.getPlayer().getName(), true);
			}
		}
	}

	/**
	 * Execute the command in the command field.
	 */
	public void commitCommand() {
		commitCommand(null);
	}

	/**
	 * Disables the chat notification toggle if the GM enforces notification
	 * 
	 * @param boolean whether to disable the toggle
	 */

	public void disableNotifyButton(Boolean disable) {
		// Little clumsy, but when the menu item is _enabled_, the button should be _disabled_
		if (!TabletopTool.getPlayer().isGM()) {
			chatNotifyButton.setSelected(false);
			chatNotifyButton.setEnabled(!disable);
			maybeAddTypingListener();
		}
	}

	/**
	 * Execute the command in the command field.
	 * 
	 * @param macroContext
	 *            The context we are calling the macro in.
	 */
	public void commitCommand(T3MacroContext macroContext) {
		String text = commandTextArea.getText().trim();

		// Command history
		// Don't store up a bunch of repeats
		if (commandHistory.size() == 0 || !text.equals(commandHistory.get(commandHistory.size() - 1))) {
			commandHistory.add(text);
			typedCommandBuffer = null;
		}
		commandHistoryIndex = commandHistory.size();

		// Make sure they aren't trying to break out of the div
		// FIXME: as above, </{"div"}> can be used to get around this
		int divCount = StringUtil.countOccurances(text, "<div");
		int closeDivCount = StringUtil.countOccurances(text, "</div>");
		while (closeDivCount < divCount) {
			text += "</div>";
			closeDivCount++;
		}
		if (closeDivCount > divCount) {
			TabletopTool.addServerMessage(TextMessage.me("You have too many &lt;/div&gt;."));
			commandTextArea.setText("");
			return;
		}
		
		
		//try {
			ChatExecutor.executeChat(text,this.getSpeaker());
			//FIXMESOON print the message as needed on different computers
			//MacroEngine.getInstance().evaluate(text, macroContext);
		/*} catch (MacroException e) {
			e.printStackTrace();
			//FIXMESOON log error
		}*/
		commandTextArea.setText("");
		TabletopTool.serverCommand().setLiveTypingLabel(TabletopTool.getPlayer().getName(), false);
	}

	public void clearMessagePanel() {
		messagePanel.clearMessages();
	}

	/**
	 * Cancel the current command in the command field.
	 */
	public void cancelCommand() {
		commandTextArea.setText("");
		validate();
		TabletopTool.getFrame().hideCommandPanel();
	}

	public void startMacro() {
		TabletopTool.getFrame().showCommandPanel();
		commandTextArea.requestFocusInWindow();
		commandTextArea.setText("/");
	}

	public void startChat() {
		TabletopTool.getFrame().showCommandPanel();
		commandTextArea.requestFocusInWindow();
	}

	public TextColorWell getTextColorWell() {
		if (textColorWell == null) {
			textColorWell = new TextColorWell();
		}
		return textColorWell;
	}

	private class CommandHistoryUpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (commandHistory.size() == 0) {
				return;
			}
			if (commandHistoryIndex == commandHistory.size()) {
				typedCommandBuffer = getCommandTextArea().getText();
			}
			commandHistoryIndex--;
			if (commandHistoryIndex < 0) {
				commandHistoryIndex = 0;
			}
			commandTextArea.setText(commandHistory.get(commandHistoryIndex));
		}
	}

	private class CommandHistoryDownAction extends AbstractAction {
		private static final long serialVersionUID = 7070274680351186504L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (commandHistory.size() == 0) {
				return;
			}
			commandHistoryIndex++;
			if (commandHistoryIndex == commandHistory.size()) {
				commandTextArea.setText(typedCommandBuffer != null ? typedCommandBuffer : "");
				commandHistoryIndex = commandHistory.size();
			} else if (commandHistoryIndex >= commandHistory.size()) {
				commandHistoryIndex--;
			} else {
				commandTextArea.setText(commandHistory.get(commandHistoryIndex));
			}
		}
	}

	@Override
	public void requestFocus() {
		commandTextArea.requestFocus();
	}

	private MessagePanel getMessagePanel() {
		if (messagePanel == null) {
			messagePanel = new MessagePanel();
			// Update whenever the preferences change
			TabletopTool.getEventDispatcher().addListener(TabletopTool.PreferencesEvent.Changed, new AppEventListener() {
				@Override
				public void handleAppEvent(AppEvent event) {
					messagePanel.refreshRenderer();
				}
			});
		}
		return messagePanel;
	}

	public void addMessage(TextMessage message) {
		messagePanel.addMessage(message);
	}

	public void setTrustedMacroPrefixColors(Color foreground, Color background) {
		getMessagePanel().setTrustedMacroPrefixColors(foreground, background);
	}

	public static class TextColorWell extends JPanel {
		private static final long serialVersionUID = -9006587537198176935L;

		//Set the Color from the saved chat color from AppPreferences
		private Color color = AppPreferences.getChatColor();

		public TextColorWell() {
			setMinimumSize(new Dimension(15, 15));
			setPreferredSize(new Dimension(15, 15));
			setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			setToolTipText("Set the color of your speech text");

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Color newColor = JColorChooser.showDialog(TextColorWell.this, "Text Color", color);
					if (newColor != null) {
						setColor(newColor);
					}
				}
			});
		}

		public void setColor(Color newColor) {
			color = newColor;
			repaint();
			AppPreferences.setChatColor(color); //Set the Chat Color in AppPreferences
		}

		public Color getColor() {
			return color;
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(color);
			g.fillRect(0, 0, getSize().width, getSize().height);
		}
	}

	private class AvatarPanel extends JComponent {
		private static final long serialVersionUID = -8027749503951260361L;
		private static final int PADDING = 5;

		private final Dimension preferredSize;

		private Image image;
		private Rectangle cancelBounds;

		public AvatarPanel(Dimension preferredSize) {
			this.preferredSize = preferredSize;
			setImage(null);
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (cancelBounds != null && cancelBounds.contains(e.getPoint())) {
						JTextPane commandArea = getCommandTextArea();
						commandArea.setText("/im");
						TabletopTool.getFrame().getCommandPanel().commitCommand();
					}
				}
			});
		}

		public void setImage(Image image) {
			this.image = image;
			setPreferredSize(image != null ? preferredSize : new Dimension(0, 0));
			invalidate();
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Dimension size = getSize();
			g.setColor(getBackground());
			g.fillRect(0, 0, size.width, size.height);

			cancelBounds = null;
			if (image == null) {
				return;
			}
			Dimension imgSize = new Dimension(image.getWidth(null), image.getHeight(null));
			SwingUtil.constrainTo(imgSize, size.width - PADDING * 2, size.height - PADDING * 2);

			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.drawImage(image, (size.width - imgSize.width) / 2, (size.height - imgSize.height) / 2, imgSize.width, imgSize.height, this);

			// Cancel
			BufferedImage cancelButton = AppStyle.cancelButton;
			int x = size.width - cancelButton.getWidth();
			int y = 2;
			g.drawImage(cancelButton, x, y, this);
			cancelBounds = new Rectangle(x, y, cancelButton.getWidth(), cancelButton.getHeight());
		}
	}

	////
	// OBSERVER
	@Override
	public void update(Observable o, Object arg) {
		ObservableList<TextMessage> textList = TabletopTool.getMessageList();
		ObservableList.Event event = (ObservableList.Event) arg;
		switch (event) {
		case append:
			addMessage(textList.get(textList.size() - 1));
			break;
		case add:
		case remove:
			//resetMessagePanel();
			break;
		case clear:
			clearMessagePanel();
			break;
		default:
			throw new IllegalArgumentException("Unknown event: " + event);
		}
	}

	private void addFocusHotKey() {
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		Object actionObject = new Object();
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ks, actionObject);
		getActionMap().put(actionObject, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent event) {
				requestFocus();
			}
		});
	}

	/**
	 * Convenience method to run a command with ability to preserve the text in the commandTextArea.
	 * 
	 * @param command
	 *            Command string
	 * @param preserveOldText
	 *            true for preserving, false to remove the old text
	 */
	public void quickCommit(String command, boolean preserveOldText) {
		String oldText = commandTextArea.getText();
		commandTextArea.setText(command);
		commitCommand();
		if (preserveOldText) {
			commandTextArea.setText(oldText);
		}
	}

	public void quickCommit(String command) {
		quickCommit(command, true);
	}

	/*
	 * Gets the chat notification duration. Method is unused at this time.
	 * 
	 * @return time in milliseconds before chat notifications disappear
	 * 
	 * public long getChatNotifyDuration(){ return chatNotifyDuration; }
	 */

	/**
	 * If the GM enforces typing notification and no listener is present (because the client had notification off), a
	 * new listener is added to the command text area
	 */
	private void maybeAddTypingListener() {
		if (commandTextArea.getListeners(ChatTypingListener.class).length == 0) {
			commandTextArea.addKeyListener(new ChatTypingListener());
		}
	}
}

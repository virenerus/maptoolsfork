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
package com.t3.client.ui.macrobuttons.buttongroups;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.AbstractBorder;

import com.t3.client.AppStyle;
import com.t3.client.TabletopTool;
import com.t3.client.ui.TokenPopupMenu;
import com.t3.client.ui.macrobuttons.buttons.MacroButton;
import com.t3.client.ui.macrobuttons.panels.AbstractMacroPanel;
import com.t3.client.ui.token.EditTokenDialog;
import com.t3.client.ui.zone.ZoneRenderer;
import com.t3.guid.GUID;
import com.t3.model.MacroButtonProperties;
import com.t3.model.Token;
import com.t3.swing.SwingUtil;
import com.t3.util.ImageManager;
import com.t3.util.guidreference.NullHelper;
import com.t3.util.guidreference.TokenReference;

public abstract class AbstractButtonGroup extends JPanel implements DropTargetListener, MouseListener {
	protected DropTarget dt;
	private TokenReference token;
	private List<Token> tokenList;
	private List<MacroButtonProperties> propertiesList; 
	private AbstractMacroPanel panel;
	private String panelClass = "";
	private String groupLabel = "";
	private String groupClass = "";
	private String macroGroup = "";
	private AreaGroup area;
	
	@Override
	public void dragEnter(DropTargetDragEvent event) {
		//System.out.println("BG: drag enter");
	}

	@Override
	public void dragOver(DropTargetDragEvent event) {
		//System.out.println("BG: drag over");
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent event) {
		//System.out.println("BG: drag action changed");
	}

	@Override
	public void dragExit(DropTargetEvent event) {
		//System.out.println("BG: drag exit");
	}

	@Override
	public void drop(DropTargetDropEvent event) {
		//System.out.println("BG: drop!");
	}

	public Token getToken() {
		return NullHelper.value(token);
	}

	public TokenReference getTokenReference(){
		return this.token;
	}
	
	public void setTokenReference(TokenReference token){
		this.token=token;
	}

	public List<Token> getTokenList() {
		return tokenList;
	}

	public void setTokenList(List<Token> tokenList){
		this.tokenList = tokenList;
	}
	
	public String getGroupClass(){
		return groupClass;
	}
	
	public void setGroupClass(String groupClass){
		this.groupClass = groupClass;
	}
	
	public String getGroupLabel(){
		return groupLabel;
	}
	
	public void setGroupLabel(String label){
		this.groupLabel = label;
	}
	
	public AbstractMacroPanel getPanel(){
		return panel;
	}
	
	public void setPanel(AbstractMacroPanel panel){
		this.panel = panel;
	}
	
	public String getPanelClass(){
		return panelClass;
	}
	
	public void setPanelClass(String panelClass){
		this.panelClass = panelClass;
	}
	
	public List<MacroButtonProperties> getPropertiesList(){
		return propertiesList;
	}
	
	public void setPropertiesList(List<MacroButtonProperties> propertiesList){
		this.propertiesList = propertiesList;
	}

	public String getMacroGroup(){
		return macroGroup;
	}
	
	public void setMacroGroup(String group){
		this.macroGroup=group;
	}
	
	public AreaGroup getArea() {
		return area;
	}
	
	public void setArea(AreaGroup newArea) {
		area = newArea;
	}
	
	protected String getTokenName(Token token) {
		// if a token has a GM name, put that to button title too
		if (token.getGMName() != null && token.getGMName().trim().length() > 0) {
			return token.getName() + " (" + token.getGMName() + ")";
		} else {
			return token.getName();
		}
	}

	// Override these mouse events in subclasses to specify component specific behavior.
	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
	}

	@Override
	public void mouseReleased(MouseEvent event)	{
		Token token = getToken();
		if (SwingUtilities.isRightMouseButton(event)) {
			if (getPanelClass()=="CampaignPanel" && !TabletopTool.getPlayer().isGM()) {
				return;
			}
			// open button group menu
			new ButtonGroupPopupMenu(getPanelClass(),area,getMacroGroup(),token).show(this, event.getX(), event.getY());
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	protected ThumbnailedBorder createBorder(String label) {
		if(getToken() != null) {
			ImageIcon i = new ImageIcon(ImageManager.getImageAndWait(getToken().getImageAssetId()));
			Image icon = i.getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT);
			return new ThumbnailedBorder(icon, label);
		} else {
			return new ThumbnailedBorder(null, label);
		}
	}

	protected class ThumbnailedBorder extends AbstractBorder {
		
		private Image image;
		private String label;
		private Rectangle imageBounds;
		
		//private final int X_OFFSET = 5;
		
		public ThumbnailedBorder(Image image, String label) {
			this.image = image;
			this.label = label;
						
			addMouseListener(new MouseHandler());
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			//TODO: change magic numbers to final fields
			// match line color to default titledborder line color
			g.setColor(new Color(165, 163, 151));
			
			if (image==null && label==null){
				g.drawRoundRect(2, 2, c.getWidth()-3, c.getHeight()-3, 6, 6);
			} else {
				g.drawRoundRect(2, 12, c.getWidth()-5, c.getHeight()-13, 6, 6);
				// clear the left and right handside of the image to show space between border line and image
				g.setColor(c.getBackground());
				g.fillRect(8, 0, 24, 20);
				g.drawImage(image, 10, 2, null);

				int strx = image != null ? 30 : 5;

				// clear the left and right of the label
				FontMetrics metrics = g.getFontMetrics();
				int stringHeight = metrics.getHeight();
				int stringWidth = metrics.stringWidth(label);
				g.fillRect(strx, 0, stringWidth + 5, stringHeight);

				// set the area for mouse listener
				if (image != null) {
					imageBounds = new Rectangle(10, 2, image.getWidth(null) + stringWidth, image.getHeight(null));
					// display impersonated image if impersonated
					if (getToken() != null && getToken().isBeingImpersonated()) {
						g.drawImage(AppStyle.impersonatePanelImage, (int) imageBounds.getMaxX() + 5, 4, null);
					}
				}
				
				g.setColor(Color.BLACK);
				g.drawString(label, strx+3, (20-stringHeight)/2+stringHeight-2);
			}
		}

		@Override
		public Insets getBorderInsets(Component component) {
			return new Insets(5, 5, 5, 5);
		}

		@Override
		public boolean isBorderOpaque() {
			return true;
		}
		
		private class MouseHandler extends MouseAdapter {
			@Override
			public void mouseReleased(MouseEvent event) {
				Token token = getToken();
				if (imageBounds != null && imageBounds.contains(event.getPoint())) {
					if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2 && !SwingUtil.isShiftDown(event)) {
						// open edit token dialog
						EditTokenDialog tokenPropertiesDialog = TabletopTool.getFrame().getTokenPropertiesDialog();
						tokenPropertiesDialog.showDialog(token);

						// update token in the renderer if it is changed
						if (tokenPropertiesDialog.isTokenSaved()) {
							ZoneRenderer renderer = TabletopTool.getFrame().getCurrentZoneRenderer();
							renderer.repaint();
							renderer.flush(token);
							TabletopTool.serverCommand().putToken(renderer.getZone().getId(), token);
							renderer.getZone().putToken(token);
						}
					} else if (SwingUtilities.isRightMouseButton(event)) {
						// open token popup menu
						Set<GUID> GUIDSet = new HashSet<GUID>();
						GUIDSet.add(NullHelper.getId(AbstractButtonGroup.this.token));
						ZoneRenderer renderer = TabletopTool.getFrame().getCurrentZoneRenderer();
						new TokenPopupMenu(GUIDSet, event.getX(), event.getY(), renderer, token).showPopup(AbstractButtonGroup.this);
					} else if (SwingUtilities.isLeftMouseButton(event) && SwingUtil.isShiftDown(event)) {
						// impersonate token toggle
						if (token.isBeingImpersonated()) {
							TabletopTool.getFrame().getCommandPanel().quickCommit("/im");
						} else {
							TabletopTool.getFrame().getCommandPanel().quickCommit("/im " + NullHelper.value(AbstractButtonGroup.this.token), false);
						}
					}					
				}
			}
		}

		public MouseAdapter getMouseAdapter() {
			return new MouseHandler();
		}
	}
	
	public static void clearHotkeys(AbstractMacroPanel panel, String macroGroup) {
		for(int areaGroupCount = 0; areaGroupCount < panel.getComponentCount(); areaGroupCount++) {
			AreaGroup area = (AreaGroup) panel.getComponent(areaGroupCount);
			for(ButtonGroup group : area.getButtonGroups()) {
				if(macroGroup.equals(group.getMacroGroup())) {
					for(MacroButton nextButton : group.getButtons()) {
						nextButton.clearHotkey();
					}
				}
			}
		}
	}
}

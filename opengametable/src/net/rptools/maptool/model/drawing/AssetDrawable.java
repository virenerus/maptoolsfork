/*
 *  This software copyright by various authors including the RPTools.net
 *  development team, and licensed under the LGPL Version 3 or, at your
 *  option, any later version.
 *
 *  Portions of this software were originally covered under the Apache
 *  Software License, Version 1.1 or Version 2.0.
 *
 *  See the file LICENSE elsewhere in this distribution for license details.
 */

package net.rptools.maptool.model.drawing;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Area;

import net.rptools.lib.MD5Key;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.ui.zone.ZoneRenderer;
import net.rptools.maptool.model.GUID;
import net.rptools.maptool.util.ImageManager;

/**
 * This class allows an asset to be used as a drawable.
 * 
 * @author jgorrell
 * @version $Revision: 5945 $ $Date: 2013-06-02 21:05:50 +0200 (Sun, 02 Jun 2013) $ $Author: azhrei_fje $
 */
public class AssetDrawable extends AbstractDrawing {

	/**
	 * Id of the asset to be drawn
	 */
	private MD5Key assetId;

	/**
	 * The id of the zone where this drawable is painted.
	 */
	private GUID zoneId;

	/**
	 * The bounds of the asset drawn
	 */
	private Rectangle bounds;

	/**
	 * Build a drawable that draws an asset.
	 * 
	 * @param anAssetId The id of the asset to be drawn.
	 * @param theBounds The bounds used to paint the drawable.
	 * @param aZoneId The id of the zone that draws this drawable.
	 */
	public AssetDrawable(MD5Key anAssetId, Rectangle theBounds, GUID aZoneId) {
		assetId = anAssetId;
		bounds = theBounds;
		zoneId = aZoneId;
	}

	/**
	 * @see net.rptools.maptool.model.drawing.Drawable#draw(java.awt.Graphics2D, net.rptools.maptool.model.drawing.Pen)
	 */
	public void draw(Graphics2D g) {
	}

	@Override
	protected void drawBackground(Graphics2D g) {
		ZoneRenderer renderer = MapTool.getFrame().getZoneRenderer(zoneId);
		Image image = ImageManager.getImage(assetId, renderer);
		g.drawImage(image, bounds.x, bounds.y, renderer);
	}

	/**
	 * @see net.rptools.maptool.model.drawing.Drawable#getBounds()
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	public Area getArea() {
		return null;
	}

}
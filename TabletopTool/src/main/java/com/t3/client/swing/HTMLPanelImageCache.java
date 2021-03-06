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
package com.t3.client.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.t3.MD5Key;
import com.t3.client.TabletopTool;
import com.t3.image.ImageUtil;
import com.t3.swing.SwingUtil;
import com.t3.util.ImageManager;

public class HTMLPanelImageCache extends Dictionary<URL, Image> {

	private final Map<String, Image> imageMap = new HashMap<String, Image>();

	public void flush() {
		imageMap.clear();
	}

	@Override
	public Enumeration elements() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Image get(Object key) {
		URL url = (URL) key;

		// URLs take a huge amount of time in equals(), so simplify by
		// converting to a string
		Image image = imageMap.get(url.toString());
		if (image == null) {

			String protocol = url.getProtocol();
			String path = url.getHost() + url.getPath();

			if ("cp".equals(protocol)) {
				try {
					image = ImageUtil.getImage(path);
				} catch (IOException ioe) {
					TabletopTool.showWarning("Can't find 'cp://" + key.toString() + "' in image cache?!", ioe);
				}
			} else if ("asset".equals(protocol)) {
				// Look for size request
				int index = path.indexOf("-");
				int size = -1;
				if (index >= 0) {
					String szStr = path.substring(index + 1);
					path = path.substring(0, index);
					size = Integer.parseInt(szStr);
				}
				image = ImageManager.getImageAndWait(new MD5Key(path));

				if (size > 0) {
					Dimension sz = new Dimension(image.getWidth(null), image.getHeight(null));
					SwingUtil.constrainTo(sz, size);

					BufferedImage img = new BufferedImage(sz.width, sz.height, ImageUtil.pickBestTransparency(image));
					Graphics2D g = img.createGraphics();
					g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g.drawImage(image, 0, 0, sz.width, sz.height, null);
					g.dispose();

					image = img;
				}
			} else {
				// Normal method
				image = Toolkit.getDefaultToolkit().createImage(url);
			}
			imageMap.put(url.toString(), image);
		}
		return image;
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Enumeration keys() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Image put(URL key, Image value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Image remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}
}

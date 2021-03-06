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
package com.t3.client.walker;

import com.t3.model.CellPoint;
import com.t3.model.Path;

public interface ZoneWalker {
	public void setWaypoints(CellPoint... points);

	public void addWaypoints(CellPoint... point);

	public CellPoint replaceLastWaypoint(CellPoint point);

	public boolean isWaypoint(CellPoint point);

	public int getDistance();

	public Path<CellPoint> getPath();

	public CellPoint getLastPoint();

	/**
	 * Remove an existing waypoint. Nothing is removed if the passed point is not a waypoint.
	 * 
	 * @param point
	 *            The point to be removed
	 * @return The value <code>true</code> is returned if the point is removed.
	 */
	boolean removeWaypoint(CellPoint point);

	/**
	 * Toggle the existence of a way point. A waypoint is added if the passed point is not on an existing waypoint or a
	 * waypoint is removed if it is on an existing point.
	 * 
	 * @param point
	 *            Point being toggled
	 * @return The value <code>true</code> if a waypoint was added, <code>false</code> if one was removed.
	 */
	boolean toggleWaypoint(CellPoint point);
}

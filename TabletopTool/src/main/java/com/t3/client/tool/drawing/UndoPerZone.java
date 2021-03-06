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
package com.t3.client.tool.drawing;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.apache.log4j.Logger;

import com.t3.client.TabletopTool;
import com.t3.model.ModelChangeEvent;
import com.t3.model.ModelChangeListener;
import com.t3.model.Zone;
import com.t3.model.drawing.Drawable;
import com.t3.model.drawing.Pen;

/**
 * /** This class controls the undo/redo behavior for drawables. (Rewritten by Azhrei)
 * <p>
 * This class is instantiated by the Zone constructor (and is not copied by the Zone copy constructor). It's purpose is
 * to record all changes to drawables for the zone, allowing for easy undo/redo functionality. In the future it would be
 * possible to add a listbox showing what edits are in the list and allowing individual edits to be deleted. This would
 * require replacement of the Swing UndoManager however, as that class does not support non-linear editing of the
 * UndoManager.
 * 
 * @author jgorrell
 * @version $Revision: 5828 $ $Date: 2011-11-26 18:29:24 -0500 (Sat, 26 Nov 2011) $ $Author: azhrei_fje $
 */
public class UndoPerZone implements ModelChangeListener {
	private static final Logger log = Logger.getLogger(UndoPerZone.class);

	/**
	 * Swing's undo/redo support
	 */
	private final UndoManager manager;
	private Zone zone = null;

	public UndoPerZone(Zone z) {
		// All we need to do is register for the Zone.Event.DRAWABLE_REMOVED event so that we can
		// eliminate that drawable out of the undo list.  As of SVN 5771, this is only used by the
		// CLEAR ALL DRAWINGS function.  See ServerMethodHandler.clearAllDrawings()

		// Using an event handler is the right way to do this, but the DrawableUndoManager would
		// need to register with every zone as they are created.  Tying into that process is too much
		// intrusion this late in the game.  Instead, I'll modify the Zone class to invoke this class
		// whenever a drawable is removed.  The coupling is too tight, but it'll work for 1.3.

		// This is weird.  It seems that this constructor can be called via Reflection when a new Zone is being instantiated by the Hessian library.
		// That creation process causes null to be passed as the zone so we can't register ourselves as a modelChangeListener for that zone or
		// we get NPE errors.  Instead, we need to let the Zone constructor instantiate use, then add us as a change listener.  Seems a little weird
		// to me, but the whole TabletopTool as both server/client is weird anyway...
		zone = z;
		manager = new UndoManager();
	}

	protected UndoPerZone(UndoPerZone upz) throws IllegalArgumentException {
		throw new IllegalArgumentException("copy constructor of UndoPerZone");
	}

	private void checkZone() {
		if (zone == null && log.isDebugEnabled())
			log.debug("zone == null (!)");
	}

	/**
	 * Add a drawable to the undo set.
	 * 
	 * @param pen
	 *            The pen used to draw.
	 * @param drawable
	 *            The drawable just drawn.
	 */
	public void addDrawable(Pen pen, Drawable drawable) {
		checkZone();
		if (log.isDebugEnabled())
			log.debug("drawable " + drawable + " being added to zone " + zone.getName());
		manager.addEdit(new DrawableUndoableEdit(pen, drawable));
		com.t3.client.AppActions.UNDO_PER_MAP.isAvailable();
		com.t3.client.AppActions.REDO_PER_MAP.isAvailable();
	}

	public boolean canUndo() {
		return manager.canUndo();
	}

	public boolean canRedo() {
		return manager.canRedo();
	}

	/**
	 * Undo the last edit if one exists.
	 */
	public void undo() {
		checkZone();
		if (!canUndo()) {
			if (log.isDebugEnabled())
				log.debug("Can't undo from zone " + zone.getName());
			return;
		}
		if (log.isDebugEnabled())
			log.debug("Undoing last change on zone " + zone.getName());
		manager.undo();
	}

	/**
	 * Redo the last undo if one exists.
	 */
	public void redo() {
		checkZone();
		if (!canRedo()) {
			if (log.isDebugEnabled())
				log.debug("Can't redo from zone " + zone.getName());
			return;
		}
		if (log.isDebugEnabled())
			log.debug("Redoing next change on zone " + zone.getName());
		manager.redo();
	}

	/**
	 * Invoked when the user activates the "Clear All Drawings" menu option. Could also be used just before writing the
	 * Zone out to persistent storage in order to keep the file size as small as possible (but leaving it there for
	 * debugging might be nice).
	 */
	public void clear() {
		manager.discardAllEdits();
	}

	/**
	 * This shouldn't need to be used since all operations are handled internally.
	 * 
	 * @return
	 */
	@Deprecated
	public UndoManager getUndoManager() {
		return manager;
	}

	/**
	 * Class used to undo/redo drawables. The GM can undo/redo any drawables, but clients should be able to manipulate
	 * only their own.
	 * 
	 * @author jgorrell
	 * @version $Revision: 5828 $ $Date: 2011-11-26 18:29:24 -0500 (Sat, 26 Nov 2011) $ $Author: azhrei_fje $
	 */
	private class DrawableUndoableEdit extends AbstractUndoableEdit {
		private static final long serialVersionUID = -1373046215655231284L;

		/**
		 * The pen used to modify the zone.
		 */
		private final Pen pen;

		/**
		 * What has been drawn.
		 */
		private final Drawable drawable;

		/**
		 * Create the undoable edit.
		 * 
		 * @param aPen
		 *            The pen for drawing.
		 * @param aDrawable
		 *            The drawable rendered.
		 */
		public DrawableUndoableEdit(Pen aPen, Drawable aDrawable) {
			pen = aPen;
			drawable = aDrawable;
		}

		/**
		 * To undo, send the drawable id to the server's <code>undoDraw</code> command.
		 * 
		 * @see javax.swing.undo.UndoableEdit#undo()
		 */
		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			// Tell the server to undo the drawable.
			TabletopTool.serverCommand().undoDraw(zone.getId(), drawable.getId());
		}

		/**
		 * @see javax.swing.undo.UndoableEdit#redo()
		 */
		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			// Render the drawable again, but don't add it to the undo manager.
			TabletopTool.serverCommand().draw(zone.getId(), pen, drawable);
		}
	}

	@Override
	public void modelChanged(ModelChangeEvent event) {
		log.debug("Inside the modelChanged() event");
	}
}

/*
 * Lucane - a collaborative platform
 * Copyright (C) 2003  Jonathan Riboux <jonathan.riboux@wanadoo.fr>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.lucane.client.widgets.htmleditor;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * @author Jonathan Riboux
 *
 * <p>
 * Actions for the HTMLEditor.
 * </p>
 */
/* TODO add copy/paste actions, add undo actions, show state on buttons */
class HTMLEditorToolbar extends JToolBar {

	private HashMap actions = null;

	private JPopupMenu sizeMenu;
	private JPopupMenu familyMenu;
	private JPopupMenu alignMenu;

	private JEditorPane editor;

	public HTMLEditorToolbar(JEditorPane editor) {

		/* The editor */
		this.editor = editor;
		this.setFloatable(false);

		/* The font size menu */
		sizeMenu = new JPopupMenu();
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(8));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(10));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(12));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(14));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(18));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(24));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(32));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(48));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(72));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(96));
		sizeMenu.add(new HTMLEditorActions.FontSizeAction(128));

		/* The font family menu */
		familyMenu = new JPopupMenu();
		familyMenu.add(new HTMLEditorActions.FontFamilyAction("Times New Roman"));
		familyMenu.add(new HTMLEditorActions.FontFamilyAction("Arial"));
		familyMenu.add(new HTMLEditorActions.FontFamilyAction("Georgia"));

		JButton tmpBtn;

		/* The left align button */
		tmpBtn = new JButton(new HTMLEditorActions.LeftAlignAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The center align button */
		tmpBtn = new JButton(new HTMLEditorActions.CenterAlignAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The right align button */
		tmpBtn = new JButton(new HTMLEditorActions.RightAlignAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		this.addSeparator();

		/* The bold button */
		tmpBtn = new JButton(new HTMLEditorActions.BoldAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The itallic button */
		tmpBtn = new JButton(new HTMLEditorActions.ItalicAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The underline button */
		tmpBtn = new JButton(new HTMLEditorActions.UnderlineAction());
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		this.addSeparator();

		/* The font size button */
		tmpBtn = new JButton(new HTMLEditorActions.ShowFontSizeMenuAction(sizeMenu));
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The font family button */
		tmpBtn = new JButton(new HTMLEditorActions.ShowFontFamilyMenuAction(familyMenu));
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);

		/* The text color button */
		tmpBtn = new JButton(new HTMLEditorActions.TextColorAction(this.getParent(), editor));
		tmpBtn.setFocusable(false);
		tmpBtn.setText("");
		this.add(tmpBtn);
	}

	public void setEnabled(boolean b) {
		super.setEnabled(b);
		Component[] cpns = this.getComponents();
		for (int i = 0; i < cpns.length; i++)
			cpns[i].setEnabled(b);
	}
}
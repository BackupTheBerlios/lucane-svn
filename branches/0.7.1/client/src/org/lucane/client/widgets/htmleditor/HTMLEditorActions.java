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

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JPopupMenu;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLEditorKit;

import org.lucane.client.Client;
import org.lucane.client.util.Translation;

/**
 * @author Jonathan Riboux
 *
 * <p>
 * Actions for the HTMLEditor.
 * </p>
 */
public class HTMLEditorActions {
	public static class TextColorAction extends AbstractAction {
		private JEditorPane editor = null;
		private Component cmp = null;

		private TextColorAction() {
		}

		public TextColorAction(Component cmp, JEditorPane editor) {
			this.editor = editor;
			this.cmp = cmp;
			
			putValue(Action.NAME, Translation.tr("toolbar.textColor"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.textColor"));
			try {
				ImageIcon img = Client.getIcon("fg_color.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}

		public void actionPerformed(ActionEvent ae) {
			Color color =
				JColorChooser.showDialog(
					cmp,
					"Text color",
					editor.getForeground());
			if (color != null)
				new StyledEditorKit.ForegroundAction(
					"",
					color).actionPerformed(
					new ActionEvent(editor, ActionEvent.ACTION_PERFORMED, ""));
		}
	}

	public static class ShowMenuAction extends AbstractAction {
		private JPopupMenu mnu = null;

		private ShowMenuAction() {
		}

		public ShowMenuAction(JPopupMenu mnu) {
			this.mnu = mnu;
		}

		public void actionPerformed(ActionEvent ae) {
			if (!(ae.getSource() instanceof Component))
				return;
			Component source = (Component) ae.getSource();
			if (mnu != null)
				mnu.show(source, 0, source.getHeight());
		}
	}

	public static class BoldAction extends HTMLEditorKit.BoldAction {
		public BoldAction() {
			putValue(Action.NAME, Translation.tr("toolbar.bold"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.bold"));
			try {
				ImageIcon img = Client.getIcon("bold.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}

	public static class ItalicAction extends HTMLEditorKit.ItalicAction {
		public ItalicAction() {
			putValue(Action.NAME, Translation.tr("toolbar.italic"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.italic"));
			try {
				ImageIcon img = Client.getIcon("italic.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}

	public static class UnderlineAction extends HTMLEditorKit.UnderlineAction {
		public UnderlineAction() {
			putValue(Action.NAME, Translation.tr("toolbar.underlined"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.underlined"));
			try {
				ImageIcon img = Client.getIcon("underlined.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}

	public static class ShowFontSizeMenuAction extends ShowMenuAction {
		public ShowFontSizeMenuAction(JPopupMenu mnu) {
			super(mnu);
			putValue(Action.NAME, Translation.tr("toolbar.fontSize"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.fontSize"));
			try {
				ImageIcon img = Client.getIcon("font_size.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}

	public static class ShowFontFamilyMenuAction extends ShowMenuAction {
		public ShowFontFamilyMenuAction(JPopupMenu mnu) {
			super(mnu);
			putValue(Action.NAME, Translation.tr("toolbar.fontFamily"));
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.fontFamily"));
			try {
				ImageIcon img = Client.getIcon("font.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}

	public static class FontSizeAction extends HTMLEditorKit.FontSizeAction {
		public FontSizeAction (int size) {
			super("" + size, size);
		}
	}

	public static class FontFamilyAction extends HTMLEditorKit.FontFamilyAction {
		public FontFamilyAction (String fontName) {
			super(fontName, fontName);
		}
	}
	
	public static class LeftAlignAction extends StyledEditorKit.AlignmentAction {
		public LeftAlignAction() {
			super(Translation.tr("toolbar.alignLeft"), StyleConstants.ALIGN_LEFT);
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.alignLeft"));
			try {
				ImageIcon img = Client.getIcon("left.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}
		
	public static class CenterAlignAction extends StyledEditorKit.AlignmentAction {
		public CenterAlignAction() {
			super(Translation.tr("toolbar.alignCenter"), StyleConstants.ALIGN_CENTER);
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.alignCenter"));
			try {
				ImageIcon img = Client.getIcon("center.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}
		
	public static class RightAlignAction extends StyledEditorKit.AlignmentAction {
		public RightAlignAction() {
			super(Translation.tr("toolbar.alignRight"), StyleConstants.ALIGN_RIGHT);
			putValue(Action.SHORT_DESCRIPTION, Translation.tr("toolbar.alignRight"));
			try {
				ImageIcon img = Client.getIcon("right.png");
				putValue(Action.SMALL_ICON, img);
			} catch (Exception e) {
			}
		}
	}
	
}

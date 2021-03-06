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

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 * @author Jonathan Riboux
 *
 * <p>
 * An HTML editor capable of text formating via an integrated toolbar.
 * </p>
 */
public class HTMLEditor extends JComponent {

	private JPanel panel;
	private JEditorPane editor;
	private JScrollPane jScrollPane;
	private HTMLEditorKit kit;
	private HTMLEditorToolbar toolBar;

	public HTMLEditor() {
		init();
	}

	private void init() {
		panel = new JPanel();
		panel.setLayout(new BorderLayout());

		/* The editor ant the HTML editor toolkit */
		editor = new JEditorPane();
		kit = new HTMLEditorKit();
		editor.setEditorKit(kit);
		kit.install(editor);
		editor.setText("<html><head><style>body{margin:0px;padding:0px;} p{margin:0px;padding:0px;}</style></head></html>");

		/* The top toolbar */
		toolBar = new HTMLEditorToolbar(editor);
		toolBar.setFloatable(false);

		/* panel et scrollPane */
		jScrollPane = new JScrollPane(editor);
		jScrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(jScrollPane, BorderLayout.CENTER);
		panel.add(toolBar, BorderLayout.NORTH);

		this.setLayout(new BorderLayout());
		this.add(panel);

		this.validate();
	}

	public String getText() {
		return editor.getText();
	}

	public void setText(String txt) {
		editor.setText(txt);
	}

	public void setEditable(boolean b) {
		toolBar.setEnabled(b);
		editor.setEditable(b);
	}

	public boolean isEditable() {
		return editor.isEditable();
	}

	public void setToolbarVisible(boolean b) {
		toolBar.setVisible(b);
	}

	public boolean isToolbarVisible() {
		return toolBar.isVisible();
	}

	public void addHTML(String html) {
		String txt = editor.getText();
		int pos;
		pos = txt.lastIndexOf("</body>");
		if (pos == -1)
			pos = txt.lastIndexOf("</BODY>");
		if (pos >= 0)
			editor.setText(txt.substring(0, pos) + html + txt.substring(pos));

		editor.setCaretPosition(editor.getDocument().getLength());

		/*HTMLDocument doc=(HTMLDocument)editor.getDocument();
		editor.setCaretPosition(doc.getLength());
		
		int pos=editor.getCaretPosition();
		try {
			kit.read(new StringBufferInputStream(html),doc,pos);
			//kit.insertHTML(doc,pos,html,0,0,null);
		} catch (Exception e) {
		}*/
	}

	public JEditorPane getEditorPane() {
		return this.editor;
	}
	
	public String getBodyText() {
		String txt = editor.getText();
		int pos1=txt.indexOf("<body>")+6;
		int pos2=txt.lastIndexOf("</body>");
		return txt.substring(pos1,pos2);
	}

	public void clear() {
		editor.setText("<html><head><style>body{margin:0px;padding:0px;} p{margin:0px;padding:0px;}</style></head></html>");
	}
}

package org.lucane.applications.sendfile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.lucane.client.Client;
import org.lucane.client.widgets.ManagedWindow;
import org.lucane.client.widgets.htmleditor.HTMLEditor;

public class SendFileDialog extends ManagedWindow
implements ActionListener
{
	private SendFile plugin;
	private boolean start;
	
	private JTextField filePath;
	private HTMLEditor comment;

	//sender
	private JButton btnSend;
	private JButton btnCancel;
	private JButton btnSelect;
	
	//receiver
	private JButton btnAccept;
	private JButton btnReject;
	
	public SendFileDialog(SendFile plugin, String title, boolean start)
	{
		super(plugin, title);
		this.plugin = plugin;
		this.start = start;
		
		initComponents();
    	setPreferredSize(new Dimension(400, 300));
    	setName("dialog");
	}
	
	private void initComponents()
	{
		getContentPane().setLayout(new BorderLayout());
		
		//file selection panel
		JPanel filePanel = new JPanel(new BorderLayout());		
		filePanel.add(new JLabel(plugin.tr("lbl.file")), BorderLayout.WEST);
		filePath = new JTextField("");
		filePath.setEditable(false);
		filePanel.add(filePath, BorderLayout.CENTER);
		if(start)
		{
			btnSelect = new JButton(plugin.tr("btn.select"));
			btnSelect.addActionListener(this);
			filePanel.add(btnSelect, BorderLayout.EAST);
		}		
		getContentPane().add(filePanel, BorderLayout.NORTH);

		//comment zone
		comment = new HTMLEditor();
		comment.setBorder(BorderFactory.createTitledBorder(plugin.tr("lbl.comment")));
		getContentPane().add(comment, BorderLayout.CENTER);
		
		//button pannel
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JPanel buttons = new JPanel(new GridLayout(1, 2));
		if(start)
		{
			btnSend = new JButton(plugin.tr("btn.send"), Client.getIcon("ok.png"));
			btnCancel = new JButton(plugin.tr("btn.cancel"), Client.getIcon("cancel.png"));
			btnSend.addActionListener(this);
			btnCancel.addActionListener(this);
			buttons.add(btnSend);
			buttons.add(btnCancel);
		}
		else
		{
			btnAccept = new JButton(plugin.tr("btn.accept"), Client.getIcon("ok.png"));
			btnReject = new JButton(plugin.tr("btn.reject"), Client.getIcon("cancel.png"));
			btnAccept.addActionListener(this);
			btnReject.addActionListener(this);
			buttons.add(btnAccept);
			buttons.add(btnReject);
		}
		buttonPanel.add(buttons, BorderLayout.EAST);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);		
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath.setText(filePath);
	}
	
	public String getFilePath()
	{
		return this.filePath.getText();
	}
	
	public void setComment(String html)
	{
		this.comment.setText(html);
	}
	
	public String getComment()
	{
		return this.comment.getText();
	}
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource() == btnCancel)
			dispose();
		else if(ae.getSource() == btnReject)
			plugin.rejectFile();
		else if(ae.getSource() == btnAccept)
			plugin.acceptFile();
		else if(ae.getSource() == btnSelect)
			plugin.selectFile(this);
		else if(ae.getSource() == btnSend)
		{
			Runnable r = new Runnable() {
				public void run() {
					plugin.askForAccept(getFilePath(), getComment());
				}
			};
			new Thread(r).start();
		}
	}
}
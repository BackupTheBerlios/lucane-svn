/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Gautier Ringeisen <gautier_ringeisen@hotmail.com>
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
package org.lucane.client;

import org.lucane.client.widgets.*;
import org.lucane.common.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Connection Dialog
 */
class ConnectBox extends JFrame implements KeyListener, ActionListener
{
    private boolean wait;
    private boolean isConnected;
    private String serverName;
	private int serverPort;
    private JTextField tbName;
    private JPasswordField tbPasswd;
    private JButton btOk;
    private JButton btCancel;
    private JProgressBar pbStatus;
    private JPanel pnlMain;
    
    /**
     * Constructor
     *
     * @param defaultLogin the default login
     * @param passwd the password to validate or null
     * @param serverName the server
     * @param serverPort the server port
     */
    public ConnectBox(String defaultLogin, String passwd, String serverName, int serverPort)
    {
        super(Translation.tr("connectBoxTitle"));
        this.wait = true;
        this.isConnected = false;
        this.serverName = serverName;
        this.serverPort = serverPort;
        
        pnlMain = new JPanel(new GridLayout(3, 2, 2, 2));
        tbName = new JTextField();
        tbPasswd = new JPasswordField();
        
        btOk = new JButton(Translation.tr("connectBoxConnect"));
        btCancel = new JButton(Translation.tr("connectBoxCancel"));
		btOk.setIcon(Client.getIcon("ok.png"));
		btCancel.setIcon(Client.getIcon("cancel.png"));
        
        pbStatus = new JProgressBar(0, 100);
        pbStatus.setValue(0);
        pnlMain.add(new JLabel(Translation.tr("connectBoxLogin")));
        pnlMain.add(tbName);
        pnlMain.add(new JLabel(Translation.tr("connectBoxPasswd")));
        pnlMain.add(tbPasswd);
        pnlMain.add(btOk);
        pnlMain.add(btCancel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        btOk.addActionListener(this);
        btCancel.addActionListener(this);
        btOk.addKeyListener(this);
        btCancel.addKeyListener(this);
        tbPasswd.addKeyListener(this);
        tbName.addKeyListener(this);
        tbName.setText(defaultLogin);
        getContentPane().setLayout(new BorderLayout(4, 4));
        getContentPane().add(pnlMain, BorderLayout.CENTER);
        getContentPane().add(pbStatus, BorderLayout.SOUTH);
        
        setSize(280, 120);
        setResizable(false);
		setIconImage(Client.getIcon("lucane.png").getImage());
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((d.width-getWidth())/2, (d.height-getHeight())/2);
        setEnabled(true);
        setVisible(true);

		//-- auto connection
		if(passwd != null)
		{
        	tbPasswd.setText(passwd);
			tryToConnect();
		}
    }
    
    /**
     * Does the client have to wait ?
     *
     * @return true if it has to wait
     */
    public boolean waiting()
    {      
        return wait;
    }
    
    /**
     * Is the connection accepted ?
     *
     * @return tru if the connection is accepted
     */
    public boolean connectionAccepted()
    {
        return isConnected;
    }
    
    /**
     * Set the value of the progressbar
     *
     * @param v the new value
     */
    public void setProgressValue(int v)
    {
        pbStatus.setValue(v);
    }
    
    /**
     * Set the maximum value of the progressbar
     *
     * @param m the maximum value
     */
    public void setProgressMax(int m)
    {
        pbStatus.setMaximum(m + 1);
        pbStatus.setIndeterminate(false);
        pbStatus.setValue(1);
    }
    
    /**
     * Close the box
     */
    public void close()
    {
        setEnabled(false);
        setVisible(false);
    }
    
    public void keyPressed(KeyEvent ev){}
    public void keyReleased(KeyEvent ev) {}
    
    /**
     * Key listener. 
     * Push the OK button
     */
    public void keyTyped(KeyEvent ev)
    {       
        if (ev.getKeyChar() == '\n')
        {
            if (ev.getSource() == btCancel)
                btCancel.doClick();
            else
                btOk.doClick();
        }
    }
    
    /**
     * Action Listener
     */
    public void actionPerformed(ActionEvent ev)
    {              
        if ((JButton) ev.getSource() == btOk)
        {            
			tryToConnect();
        }
        else if ((JButton) ev.getSource() == btCancel)
        {
            wait = false;
        }
    }

	private void tryToConnect()
	{
        String msg;
        pbStatus.setIndeterminate(true);
        try
        {                
        	Client parent = Client.getInstance();
            Listener lstnr = parent.createNewListener();
            parent.setMyInfos(tbName.getText(), serverName, lstnr.getPort());
            parent.createNewCommunicator(serverName);
            msg = resquestForConnection();
            
            if (msg.startsWith("AUTH_ACCEPTED"))
            {                    
                //decipher the private key
                try
                {
                    
                    String privkey = msg.substring(14); //"AUTH_ACCEPTED "
                    String passwd = new String(tbPasswd.getPassword());
                    Communicator.getInstance().setPrivateKey(privkey, passwd);
                }
                catch (Exception e)
                {
					Logging.getLogger().warning(Translation.tr("connectBoxPrivKeyError"));
                }
                
                wait = false;
                isConnected = true;
            }
            else
            {
                pbStatus.setIndeterminate(false);
                pbStatus.setValue(0);
                DialogBox.error(getErrorMsg(msg));
            }
        }
        catch (Exception e)
        {
            pbStatus.setIndeterminate(false);
            pbStatus.setValue(0);
            DialogBox.error(Translation.tr("connectBoxBadDataError"));
        }
	}
    
    /**
     * Ask the server to authenticate the client
     *
     * @return the server's answer
     */
    private String resquestForConnection()
    {
        Client parent = Client.getInstance();
        StringTokenizer stk;
        String msg;
        String passwd = new String(tbPasswd.getPassword());
        msg = "AUTH "
            + MD5.encode(passwd)
            + " "
            + parent.getMyInfos().hostname
            + " "
            + parent.getMyInfos().port;
        
        try
        {           
            ConnectInfo serverInfos = new ConnectInfo("Server", serverName, serverName, 
                parent.getConfig().getServerPort(), "nokey", "Server");
            Communicator.getInstance().setProxyInfo();
			Logging.getLogger().finer("ConnectBox::requestForConnection(): CALL TO COMMUNICATOR");
            
            ObjectConnection oc = Communicator.getInstance().sendMessageTo(serverInfos, "Server", msg);
            msg = oc.readString();
            oc.close();
			Logging.getLogger().finer("ConnectBox::requestForConnection(): MSG '" + msg + "'");
            
            if ((msg == null) || (msg.equals("")))
                msg = "BAD_MESSAGE";
        }
        catch (Exception ex)
        {
        	Logging.getLogger().severe(ex.getMessage());
          ex.printStackTrace();
            msg = "NO_CONNECTION";
        }
        
        return msg;
    }
    
    /**
     * Translate the server response to an understable message
     *
     * @param msg the server's message
     * @return the userfriendly message
     */
    private String getErrorMsg(String msg)
    {
        
        if (msg.equals("BAD_MESSAGE"))
            return Translation.tr("connectBoxBadMessage1") + "\n" + Translation.tr("connectBoxBadMessage2");
        else if (msg.equals("NO_CONNECTION"))
            return Translation.tr("connectBoxNoConnection1") + "\n" + Translation.tr("connectBoxNoConnection2");
        else if (msg.equals("NOT_VALID_USER"))
            return Translation.tr("connectBoxNotValidUser1") + "\n" + Translation.tr("connectBoxNotValidUser2");
        else if (msg.equals("NOT_VALID_SERVER"))
            return Translation.tr("connectBoxNotValidServer1") + "\n" + Translation.tr("connectBoxNotValidServer2");
        else if (msg.equals("ACCESS_DENIED"))
            return Translation.tr("connectBoxAccessDenied1") + "\n" + Translation.tr("connectBoxAccessDenied2");
        else if (msg.equals("ALREADY_CONNECTED"))
            return Translation.tr("connectBoxAlreadyConnected1") + "\n" + Translation.tr("connectBoxAlreadyConnected2");
        else
            return Translation.tr("connectBoxBadMessage1") + "\n" + Translation.tr("connectBoxBadMessage2");
    }
}

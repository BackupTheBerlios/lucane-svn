/*
 * Lucane - a collaborative platform
 * Copyright (C) 2002  Gautier Ringeisen <gautier_ringeisen@hotmail.com>
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
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

import org.lucane.client.util.*;
import org.lucane.client.widgets.*;
import org.lucane.common.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Connection Dialog
 */
class ConnectBox
implements KeyListener, ActionListener
{
    private JDialog dialog;
    
    private boolean isConnected;
    private String serverName;
    private int serverPort;

    private JTextField tbName;
    private JPasswordField tbPasswd;
    private JButton btOk;
    private JButton btCancel;
    private JProgressBar pbStatus;
    
    /**
     * Constructor
     *
     * @param serverName the server
     * @param serverPort the server port
     */
    public ConnectBox(String serverName, int serverPort)
    {
        dialog = new JDialog((Frame)null, Translation.tr("connectBoxTitle"), true);
        this.isConnected = false;
        this.serverName = serverName;
        this.serverPort = serverPort;
        
        JPanel pnlMain = new JPanel(new BorderLayout());
        
        ImageIcon logo = Client.getIcon("login_logo.png");
        
        JLabel jLblLogo = new JLabel();
		jLblLogo.setBackground(Color.WHITE);
        jLblLogo.setIcon(logo);
		
        pnlMain.add(jLblLogo, BorderLayout.CENTER);
        
        FocusListener fl = new FocusListener() {
            public void focusGained(FocusEvent fe) {
                JTextField jtf = (JTextField)fe.getComponent();
                jtf.setSelectionStart(0);
                jtf.setSelectionEnd(jtf.getText().length());
            }
            public void focusLost(FocusEvent fe) {}
        };
        
        this.tbName = new JTextField();
        tbName.addFocusListener(fl);
        this.tbPasswd = new JPasswordField();
        tbPasswd.addFocusListener(fl);
        
        this.btOk = new JButton(Translation.tr("connectBoxConnect"));
        this.btCancel = new JButton(Translation.tr("connectBoxCancel"));
        this.btOk.setIcon(Client.getIcon("ok.png"));
        this.btCancel.setIcon(Client.getIcon("cancel.png"));
        
        this.pbStatus = new JProgressBar(0, 100);
        this.pbStatus.setValue(0);
        this.pbStatus.setFont(this.pbStatus.getFont().deriveFont(9f));
        this.pbStatus.setString("");
        this.pbStatus.setStringPainted(true);
        
		JPanel pnlInput = new JPanel(new GridLayout(2, 2, 2, 2));
		pnlInput.add(new JLabel(Translation.tr("connectBoxLogin")));
		pnlInput.add(tbName);
		pnlInput.add(new JLabel(Translation.tr("connectBoxPasswd")));
		pnlInput.add(tbPasswd);

		JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
		pnlButtons.add(btOk);
		pnlButtons.add(btCancel);

		JPanel pnlLogin = new JPanel(new BorderLayout(4, 4));
		pnlLogin.setBorder(new EmptyBorder(4, 4, 0, 4));
		pnlLogin.add(pnlInput, BorderLayout.CENTER);
		pnlLogin.add(pnlButtons, BorderLayout.SOUTH);

        pnlMain.add(pnlLogin, BorderLayout.SOUTH);
        
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btOk.addActionListener(this);
        btCancel.addActionListener(this);
        btOk.addKeyListener(this);
        btCancel.addKeyListener(this);
        tbPasswd.addKeyListener(this);
        tbName.addKeyListener(this);
        
        
        dialog.getContentPane().setLayout(new BorderLayout(4, 4));
        dialog.getContentPane().add(pnlMain, BorderLayout.CENTER);
        dialog.getContentPane().add(pbStatus, BorderLayout.SOUTH);
        
		dialog.pack();
		dialog.setResizable(false);
        
        
    }
    
    /**
     * Show the modal dialog
     * 
     * @param defaultLogin the login to display
     * @param passwd if non null, autoconnect
     */
    public void showModalDialog(String defaultLogin, String passwd)
    {
        //center dialog
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((d.width-dialog.getWidth())/2, (d.height-dialog.getHeight())/2);

        tbName.setText(defaultLogin);
        
        //-- auto connection
        if(passwd != null)
        {
            tbPasswd.setText(passwd);
            dialog.setEnabled(false);
            tryToConnect(Client.getInstance());
        }
        else
            dialog.show();
    }   
    
    /**
     * Is the connection accepted ?
     *
     * @return true if the connection is accepted
     */
    public boolean connectionAccepted()
    {
        return isConnected;
    }
    
    /**
     * Set the value of the progressbar
     *
     * @param v the new value
     * @param str the message
     */
    public void setProgressValue(int v, String str)
    {
        pbStatus.setValue(v);
        pbStatus.setString(str);
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
    public void closeDialog()
    {
        dialog.setVisible(false);
        dialog.dispose();
    }
        
    /**
     * Key listener. 
     * Push the OK button
     */
    public void keyTyped(KeyEvent ev)
    {       
        if (ev.getKeyChar() == KeyEvent.VK_ENTER) 
        {
            if (ev.getSource() == btCancel)
                btCancel.doClick();
            else
                btOk.doClick();
        }
    }
    public void keyPressed(KeyEvent ev) {}
    public void keyReleased(KeyEvent ev) {}
    
    /**
     * Action Listener
     */
    public void actionPerformed(ActionEvent ev)
    {              
        if ((JButton) ev.getSource() == btOk)
        {
            new Thread() {
                public void run() {
                    tryToConnect(Client.getInstance());
                }
            }.start();
        }
        else if ((JButton) ev.getSource() == btCancel) {
            closeDialog();
            System.exit(0);
        }
    }

    /**
     * Connect to the server and authenticate
     */
    private void tryToConnect(Client parent)
    {
        String msg;
        pbStatus.setIndeterminate(true);
        try
        {                
            Listener lstnr = parent.createNewListener();
            parent.setMyInfos(tbName.getText(), serverName, lstnr.getPort());
            parent.createNewCommunicator(serverName);
            msg = resquestForConnection(parent);
            
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
                
                isConnected = true;
                Client.getInstance().init();
                closeDialog();
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
    private String resquestForConnection(Client parent)
    {
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

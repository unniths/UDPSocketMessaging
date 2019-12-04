import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.util.Random;
import java.util.Scanner;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

public class ChatApp extends JFrame implements KeyListener{
	
	//label, button, text-field initial creation
	private JTextArea textArea;
	private JTextField textfield3;
	private JButton send, close;
	private InetAddress IPdest, ip2;
	private int port, port2;
	private Socket socket;
	
	public ChatApp (Socket connect, InetAddress ip, int port) {	
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		
		try {
			this.ip2 = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		this.port2 = 64000;
		
		// jframe title displaying other person's ip and port
		setTitle("Connected to: " + ip + ": "+ port);
		setSize(900,800);
		
		// jpanel settings
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		getContentPane().add(panel);
		
		// text area settings
		textArea = new JTextArea();
		textArea.setEditable(false);
		panel.add(textArea);
		
		// text field settings
		JPanel bottom = new JPanel(new BorderLayout());
		panel.add(bottom, BorderLayout.SOUTH);
		textfield3 = new JTextField();
		bottom.add(textfield3);
		
		// send button settings
		JPanel button = new JPanel(new BorderLayout());
		bottom.add(button, BorderLayout.EAST);
		send = new JButton("Send");
		
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// if the text-field isn't empty, it sends the message and display on your screen.
				if(!textfield3.getText().isEmpty()) {
					String message = textfield3.getText().toString().trim();
					InetAddress destination = null;
					textArea.append("You (" + ip2 + ": " + port2 + ") " + message + "\n");
					
					destination = ip;
					connect.send(message, destination, port);
					textfield3.setText("");
				}
				
			}
			
		});
		button.add(send, BorderLayout.WEST);
		
		// Listens for Enter key press
		textfield3.addKeyListener(this);
		
		
		close = new JButton("Close");
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
			
		});
		button.add(close);
		
		setVisible(true);  		
	}
	
	public JTextArea getText() {
		return this.textArea;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	//It clicks send button whenever I press enter key
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			send.doClick();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}

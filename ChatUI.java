import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ChatUI extends JFrame{
	
	//label, button, text-field initial creation
	private static Socket socket = new Socket(64000);
	private JLabel label, label1, label2, label3, label5, label6;
	private JTextField textfield, textfield2;
	private JButton connect;
	private static Map<InetAddress, ChatApp> newChat = new HashMap<InetAddress, ChatApp>();
	
	public ChatUI(){   
		setTitle("Chat Application");
		
		//enter name label
		label = new JLabel("IP Address  : ");
		label.setBounds(10, 10, 100, 100);
		add(label);
		
		label2 = new JLabel("Port Number : ");
		label2.setBounds(10, 50, 100, 100);
		add(label2);
		
		//label for debug
		label1 = new JLabel();
		label1.setText("My IP: " + socket.getMyAddress());
		label1.setBounds(10, 200, 300, 100);
		add(label1);
		
		label3 = new JLabel();
		label3.setText("My Port: " + socket.getMyPortNumber());
		label3.setBounds(10, 220, 300, 100);
		add(label3);
		
		label5 = new JLabel();
		label5.setBounds(10, 240, 300, 100);
		add(label5);
		
		label6 = new JLabel();
		label6.setBounds(10, 260, 300, 100);
		add(label6);
		
		//text field to enter name
		textfield = new JTextField();
		textfield.setBounds(110, 45, 130, 30);
		add(textfield);
		
		textfield2 = new JTextField();
		textfield2.setBounds(110, 85, 130, 30);
		add(textfield2);

		//submit button
		connect = new JButton("Connect!");
		connect.setBounds(100, 170, 140, 40);  
		add(connect);
		
		setSize(300,400);
		setLayout(null);
		setVisible(true);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   

		//action listener
		connect.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				//if the input isn't empty, displays the input and creates chat window
				if(!textfield.getText().isEmpty() && !textfield2.getText().isEmpty()) {
					int portNum = Integer.parseInt(textfield2.getText());
					String ipNum = textfield.getText();
					
					//This is converting String to InetAddress
					InetAddress ip = null;
					try {
						ip = InetAddress.getByName(ipNum);
					} catch (UnknownHostException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					label5.setText("Input IP   : " + ipNum);
					textfield.setText("");
					label6.setText("Input Port : " + portNum);
					textfield2.setText("");
					ChatApp createChat = new ChatApp(socket, ip, portNum);
					newChat.put(ip, createChat);
				}
				
			}
		});
		
		//Whenever you receive a message, this keeps looping and receiving
		do {
			receive();
		} while (true);
	}
	
	//This receives the message and display it on the chat application
	public static void receive() {
		DatagramPacket inpacket = null;
		
		do {
			inpacket = socket.receive();
			
			if(inpacket != null) {
				byte[] inBuffer = inpacket.getData();
				InetAddress ip = inpacket.getAddress();
				int port = inpacket.getPort();
				String message = new String(inBuffer);
				
				if(!newChat.containsKey(ip)) {
					ChatApp newchat = new ChatApp(socket, ip, port);
					newChat.put(ip, newchat);
					newchat.getText().append("Person (" + ip + ": " + port + ") " + message + "\n");
					newchat.setVisible(true);
				} else {
					ChatApp currentChat = newChat.get(ip);
					currentChat.getText().append("Person (" + ip + ": " + port + ") " + message + "\n");
					currentChat.setVisible(true);
				}
			}
		} while (inpacket == null);
	}
	
	public static Socket getSocket() {
		return socket;
	}
	
	public static void main(String[] args) {
		ChatUI startChat = new ChatUI();
	}
	
}

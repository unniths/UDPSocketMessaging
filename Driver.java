package nov18;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Driver extends JFrame {
    private static JFrame f;
    private static JPanel p;
    private static JButton b;
    private static JButton broadcastbutton;
    private static JLabel iplabel;
    private static JLabel portlabel;
    private static JLabel broadcastlabel;
    private static JTextField iptxt;
    private static JTextField porttxt;
    private static JTextField broadcasttxt;
    public static InetAddress ReceiverIP;
    public static String message;
    public static String ipname;
    public static String portname;
    public static String you;
    public static String Receiver = "";
    public static String NameOfReceiver = "";
    public static String User = "Shiva";
    public static int portnumber = 64000;
    public static Socket socket = new Socket(64000);
    private static Map<InetAddress, ChatWindow> newWindow = new HashMap();
    static InetAddress yoIP, ip;
    static String myIP;

    public Driver() throws UnknownHostException {
        f = new JFrame("Start Messaging!");
        f.setSize(550, 350);
        f.setDefaultCloseOperation(3);
        f.setLayout(new BorderLayout());
        p = new JPanel();
        p.setBackground(Color.PINK);
        iplabel = new JLabel("Enter an IP address");
        portlabel = new JLabel("\n Enter a port: ");
        broadcastlabel = new JLabel("\n Message you want to broadcast:");
        iptxt = new JTextField(43);
        porttxt = new JTextField(43);
        broadcasttxt = new JTextField(43);
        p.add(iplabel);
        p.add(iptxt);
        p.add(portlabel);
        p.add(porttxt);
        p.add(broadcastlabel);
        p.add(broadcasttxt, "South");
        b = new JButton("Chat");
        p.add(b, "East");
        broadcastbutton = new JButton("Broadcast");
        p.add(broadcastbutton, "South");
        f.add(p);
        f.setVisible(true);
        broadcastbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InetAddress broadcastIPAddress = null;

                try {
                    //broadcastIPAddress = InetAddress.getByName("255.255.255.255"); //should work for all
                    broadcastIPAddress = InetAddress.getByName("192.168.43.255"); //should work on my mobile hotspot
                } catch (UnknownHostException var4) {
                    var4.printStackTrace();
                }

                NameOfReceiver = broadcasttxt.getText().trim(); //took off unnecessary "Driver.__" for variables
                broadcasttxt.setText("");
                String sentence = "????? " + Driver.NameOfReceiver + " ##### " + User;
                System.out.println(sentence);
                Driver.socket.send(sentence, broadcastIPAddress, Driver.portnumber);
            }
        });
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!Driver.iptxt.getText().isEmpty() && !Driver.porttxt.getText().isEmpty()) {
                    int portNum = Integer.parseInt(Driver.porttxt.getText());
                    String ipNum = Driver.iptxt.getText();
                    InetAddress ip = null;

                    try {
                        ip = InetAddress.getByName(ipNum);
                    } catch (UnknownHostException var6) {
                        var6.printStackTrace();
                    }

                    ChatWindow createChat = new ChatWindow(Driver.socket, ip, portNum);
                    Driver.newWindow.put(ip, createChat);
                }

            }
        });

        while(true) {
            receive();
        }
    }

    public static void receive() throws UnknownHostException {
        DatagramPacket datagrampacket = null;

        do {
            datagrampacket = socket.receive();
            //System.out.println(datagrampacket);
            if (datagrampacket != null) {
                byte[] inBuffer = datagrampacket.getData();
                ip = datagrampacket.getAddress();
                int port = datagrampacket.getPort();
                String message = new String(inBuffer);
                System.out.println("IP address of sender: " + ip);
                System.out.println("Message: " + message);


                if (AddressRequest(message) && you.equalsIgnoreCase(User)) {
                    message = "##### " + User + " ##### " + myIP; //changed the name of the ResponseString to message
                    socket.send(message, ip, port);

                    if (!newWindow.containsKey(ip)) {
                        ChatWindow newchat = new ChatWindow(socket, ip, port);
                        newWindow.put(ip, newchat);
                        newchat.getText().append(Receiver + ": " + message + "\n");
                        newchat.setVisible(true);
                    }

                } else if (RequestAnswered(message)){
                    if(!newWindow.containsKey(ip)){
                        ChatWindow newchat = new ChatWindow(socket, ip, portnumber);
                        newWindow.put(ip, newchat);
                        newchat.getText().append(Receiver + ": " + message + "\n");
                        newchat.setVisible(true);
                    }
                    else{
                        ChatWindow current = newWindow.get(ip);
                        current.getText().append(Receiver + ": " + message + "\n");
                    }
                }
                else if (!newWindow.containsKey(ip)){
                    ChatWindow newchat = new ChatWindow(socket, ip, portnumber);
                    newWindow.put(ip, newchat);
                    newchat.getText().append(Receiver + ": " + message + "\n");
                }
                else if (newWindow.containsKey(ip)){
                    ChatWindow current = newWindow.get(ip);
                    current.getText().append(Receiver + ": " + message + "\n");
                }
            }
        } while(datagrampacket == null);

    }

    private static boolean AddressRequest(String message) {
        if (message.startsWith("?????")) {
            String[] splittedMessage = message.split(" ");
            if (splittedMessage[2].equalsIgnoreCase("#####")) {
                you = splittedMessage[1];
                Receiver = splittedMessage[3];

                for(int i = 0; i < splittedMessage.length; ++i) {
                    System.out.println(splittedMessage[i]);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean RequestAnswered(String message) {
        if (message.startsWith("#####")) {
            String[] splittedMessage = message.split(" ");
            if (splittedMessage[1].equalsIgnoreCase(NameOfReceiver) &&
                    splittedMessage[2].equalsIgnoreCase("#####")) {
                Receiver = splittedMessage[1];

                try {
                    ip = InetAddress.getByName(splittedMessage[3]); //was formerly known as
                } catch (UnknownHostException var3) {              // "ReceiverIP", combined with regular IP
                    var3.printStackTrace();
                }

                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) throws UnknownHostException {
        new Driver();
    }

    static {
        try {
            yoIP = InetAddress.getLocalHost();
        } catch (UnknownHostException var1) {
            var1.printStackTrace();
        }

        myIP = yoIP.getHostAddress();
        System.out.println("My IP is: " + myIP);
    }
}

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.sound.midi.Receiver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

public class Driver extends JFrame {

    private static JFrame f, f2;
    private static JPanel p, p2;
    private static JButton b, broadcastbutton;
    private static JLabel iplabel, portlabel, broadcastlabel;
    private static JTextField iptxt, porttxt, broadcasttxt;
    //public static Socket sock;
    public static InetAddress ReceiverIP;
    public static String message, ipname, portname, you;
    public static String Receiver = "";
    public static String NameOfReceiver = "";
    public static String User = "Shiva";
    public static int portnumber = 64000;
    public static Socket socket = new Socket(64000);
    //public static Socket socket;
    private static Map<InetAddress, ChatWindow> newWindow = new HashMap<InetAddress, ChatWindow>();

    static InetAddress yoIP;

    static {
        try {
            yoIP = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    static String myIP = yoIP.getHostAddress();



    public Driver() throws UnknownHostException {


        //frame, its size, x button working, layout design
        f = new JFrame("Start Messaging!");
        f.setSize(550, 350);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        //panel made, set background pink, used to put textfield and button
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
        p.add(broadcasttxt, BorderLayout.SOUTH);
        b = new JButton("Chat");
        p.add(b, BorderLayout.EAST);
        broadcastbutton = new JButton("Broadcast");
        p.add(broadcastbutton, BorderLayout.SOUTH);

        //added the panel to the frame and positioned it where it looks neat
        f.add(p);
        f.setVisible(true);

        broadcastbutton.addActionListener(new ActionListener(){


            public void actionPerformed(ActionEvent e){
                //open broadcast window to specifically broadcast
                InetAddress broadcastIPAddress = null;

                try{
                    broadcastIPAddress = InetAddress.getByName("255.255.255.255");
                } catch (UnknownHostException ex) {
                    ex.printStackTrace();
                }

                NameOfReceiver = broadcasttxt.getText().trim();
                broadcasttxt.setText("");
                String sentence = "????? " + NameOfReceiver + " ##### " + User;
                socket.send(sentence, broadcastIPAddress,portnumber);
                System.out.println("You broadcasted: "+ sentence);





            }
        });

        //added the button, gave it an actionListener so that it could actually do something.
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (!iptxt.getText().isEmpty() && !porttxt.getText().isEmpty()) {
                    int portNum = Integer.parseInt(porttxt.getText()); //parseInt is to make the string into a number
                    String ipNum = iptxt.getText();

                    InetAddress ip = null;
                    try {
                        ip = InetAddress.getByName(ipNum);
                    } catch (UnknownHostException e1) {
                        e1.printStackTrace();
                    }

                    ChatWindow createChat = new ChatWindow(socket, ip, portNum);
                    newWindow.put(ip, createChat);


                }

            }
        });

        do {
            receive();
        } while (true);


    }

    public static void receive() throws UnknownHostException {
        DatagramPacket datagrampacket = null;

        do {
            datagrampacket = socket.receive();
            //System.out.println(socket.receive());

            if (datagrampacket != null) {
                byte[] inBuffer = datagrampacket.getData();
                System.out.println("Passed line 1");
                InetAddress ip = datagrampacket.getAddress();
                System.out.println("IP Test: " + ip);
                int port = datagrampacket.getPort();
                String message = new String(inBuffer);
                System.out.println("IP address of sender: " + ip);
                System.out.println("Message: "+ message);

                System.out.println(AddressRequest((message)));
                if (AddressRequest((message)) && (you.equalsIgnoreCase(User))){
                    String responseString = "##### " + User + " ##### " + myIP ;
                    socket.send(responseString, ip ,portnumber);

                    if(!newWindow.containsKey(ip)){
                        ChatWindow newchat = new ChatWindow(socket, ip, portnumber);
                        newWindow.put(ip, newchat);
                        newchat.getText().append(Receiver + ": " + message + "\n");
                        //newchat.setVisible(true);
                    }
                }

                else if(RequestAnswered(message)){
                    System.out.println("Test 2: " +  RequestAnswered(message));
                    if(!newWindow.containsKey(ReceiverIP)){
                        ChatWindow newchat = new ChatWindow(socket, ReceiverIP, port);
                        newWindow.put(ReceiverIP, newchat);
                        newchat.getText().append(Receiver + ": " + message + "\n");
                        newchat.setVisible(true);
                        if (!newWindow.containsKey(ip)) {
                            newWindow.put(ip, newchat);
                            newchat.getText().append("Recipient (" + ip + "/" + port + ") :" + message + "\n");
                            newchat.setVisible(true);
                        } else {
                            ChatWindow currentChat = newWindow.get(ip);
                            currentChat.getText().append("Recipient (" + ip + "/" + port + ") :" + message + "\n");
                            currentChat.setVisible(true);
                        }

                    }
                }

            }



        } while (datagrampacket == null);
    }

    private static boolean AddressRequest(String message){

        if(message.startsWith("?????")){
            String[] splittedMessage = message.split(" ");

            if(splittedMessage[2].equalsIgnoreCase("#####")){
                you = splittedMessage[1];
                Receiver = splittedMessage[3];

                for(int i=0; i < splittedMessage.length; i++){
                    System.out.println(splittedMessage[i]);
                } return true;
            }
        } return false;
    }

    public static boolean RequestAnswered(String message){

        if(message.startsWith("#####")){

            String[] splittedMessage = message.split(" ");
            if (splittedMessage[2].equalsIgnoreCase(NameOfReceiver) &&
                    splittedMessage[2].equalsIgnoreCase("#####")){
                Receiver = splittedMessage[1];
                try{
                    ReceiverIP = InetAddress.getByName(splittedMessage[3]);
                } catch(UnknownHostException e){
                    e.printStackTrace();
                } return true;
            }
        } return false;
    }

    public static void main(String[] args) throws UnknownHostException {
        new Driver();
    }

}
//At this point, 11/13 at 6:11 AM, my code is able to send and receive broadcasts with a couple of hiccups including:
//   a) The first sent broadcast goes to my IP address rather than the person I'm trying to send to.
//   b) When receiving/sending broadcasts, the initial message with the #'s and ?'s shows in the chat.
//   c) The name of the person you are talking to does not appear.
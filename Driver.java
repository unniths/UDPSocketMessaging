import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

public class Driver extends JFrame{

    private static JFrame f;
    private static JPanel p;
    private static JButton b;
    private static JLabel iplabel, portlabel;
    private static JTextField iptxt, porttxt;
    //public static Socket sock;
    public static InetAddress myAddress;
    public static String message, ipname, portname;
    public static Socket socket = new Socket(64000);
    private static Map<InetAddress, ChatWindow> newWindow = new HashMap<InetAddress, ChatWindow>();



    public Driver() {

        //receive();

        //frame, its size, x button working, layout design
        f = new JFrame("Start Messaging!");
        f.setSize(500, 300);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout());

        //panel made, set background pink, used to put textfield and button
        p = new JPanel();
        p.setBackground(Color.PINK);

        iplabel = new JLabel("Enter an IP address");
        portlabel = new JLabel("\n Enter a port");

        iptxt = new JTextField(40);
        porttxt = new JTextField(40);
        //iplabel.setVisible(true);
        //iplabel.setBackground(Color.WHITE);

        //added the button, gave it an actionListener so that it could actually do something.
        b = new JButton("Chat");
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if(!iptxt.getText().isEmpty() && !porttxt.getText().isEmpty()){
                    int portNum = Integer.parseInt(porttxt.getText());
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

        //added the textfield, label, and button to the panel(and tried positioning them)
        p.add(iplabel);
        p.add(iptxt);
        p.add(portlabel);
        p.add(porttxt);

        p.add(b, BorderLayout.EAST);

        //added the panel to the frame and positioned it where it looks neat
        f.add(p);
        f.setVisible(true);


    }

    public static void receive(){
        DatagramPacket datagrampacket = null;

        do {
            datagrampacket = socket.receive();
            //System.out.println(socket.receive());

            if (datagrampacket != null) {
                //message = new String(datagrampacket.getData());
                //ChatWindow.conversation.append(datagrampacket.getAddress() + ":  " + message + "\n");
                byte[] inBuffer = datagrampacket.getData();
                InetAddress ip = datagrampacket.getAddress();
                int port = datagrampacket.getPort();
                String message = new String(inBuffer);

                if (!newWindow.containsKey(ip)) {
                    ChatWindow newchat = new ChatWindow(socket, ip, port);
                    newWindow.put(ip, newchat);
                    newchat.getText().append("Recepient (" + ip + "/" + port + ") :" + message + "\n");
                    newchat.setVisible(true);
                } else {
                    ChatWindow currentChat = newWindow.get(ip);
                    currentChat.getText().append("Recepient (" + ip + "/" + port + ") :" + message + "\n");
                    currentChat.setVisible(true);
                }
            } while (datagrampacket == null);
            /*else{
                ChatWindow frame = new ChatWindow(iptxt.getText(), porttxt.getText());


            }*/
        } while (datagrampacket == null);
    }

    public static void main(String[] args){
        Driver startProgram = new Driver();
    }
}

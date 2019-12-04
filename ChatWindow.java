import java.net.InetAddress;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ChatWindow {

    public static JFrame frame; //window
    public static JPanel panel; //displays the components on the window
    public static JTextArea conversation;
    public static JButton send;
    public static JButton receive;
    public static JButton close;
    public static JTextArea typehere;
    public static JScrollPane scroll;
    public static JScrollPane scroll2;


    public ChatWindow(Socket connect, InetAddress ip, int port) //constructor of the class
    {
        Driver.ipname = iptxt;
        Driver.portname = porttxt;
        gui();

    }
    public void gui(){ //declaring the method

        int port = Integer.parseInt(Driver.portname);
        Driver.socket = new Socket(port);
        Driver.myAddress = null;

        try{
            Driver.myAddress = InetAddress.getByName(Driver.ipname);
        } catch (Exception a) {
            a.printStackTrace();
            System.exit(-1);
        }


        typehere = new JTextArea(5,30);
        typehere.setVisible(true);
        typehere.setBackground(Color.WHITE);
        typehere.setLineWrap(true);

        conversation = new JTextArea(40,80);
        conversation.setEditable(false);
        conversation.setLineWrap(true);

        scroll = new JScrollPane(conversation);
        scroll2 = new JScrollPane(typehere);


        close = new JButton("Close");
        close.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent e){
                frame.dispose();
            }
        });
        send = new JButton("Send"); //declaring the button
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Driver.message = typehere.getText();
                Driver.socket.send(Driver.message, Driver.myAddress, 64000);
                conversation.append("You: " + Driver.message + "\n sent to: " + Driver.myAddress + "  port # " + Driver.portname + "\n");
                typehere.setText(" ");
            }
        });
        //send.addActionListener(this);
        /*
        receive = new JButton("Receive");
        receive.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                Driver.datagrampacket = Driver.socket.receive();
                if (Driver.datagrampacket != null) {
                    Driver.message = new String(Driver.datagrampacket.getData());
                    conversation.append(Driver.datagrampacket.getAddress() + ":  " + Driver.message + "\n");
                }
            }
        });*/


        //label = new JLabel("Messages"); //declaring the label
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.DARK_GRAY); //picks the color background
        //panel.add(typehere, BorderLayout.WEST);
        panel.add(send); //adding button to panel
        panel.add(receive);
        panel.add(close);
        panel.add(scroll, BorderLayout.NORTH);
        panel.add(scroll2, BorderLayout.CENTER);
        //conversation.add(scroll);
        //typehere.add(scroll2);

        //conversation.add(label); //adding label to pane

        frame = new JFrame("IP: [" + Driver.ipname + "]  " + "Port: [" + Driver.portname + "  ]");
        frame.setSize(700,700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //programs the close button on the window
        frame.setLayout(new BorderLayout());
        frame.add(panel,BorderLayout.SOUTH);
        frame.add(conversation,BorderLayout.CENTER);
        frame.setVisible(true);



    }

}



import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ChatWindow extends JFrame{

    public static JFrame frame; //window
    public static JPanel panel; //displays the components on the window
    public static JTextArea conversation;
    public static JButton send, close, receive;
    public static JTextArea typehere;
    public static JScrollPane scroll, scroll2;
    private InetAddress ip;
    private int port;


    public ChatWindow(Socket connect, InetAddress ip, int port) //constructor of the class
    {

        try{
            this.ip = InetAddress.getLocalHost();
        } catch (Exception a) {
            a.printStackTrace();
            System.exit(-1);
        }

        this.port = 64000;

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
                Driver.message = typehere.getText().trim();
                //Driver.socket.send(Driver.message, Driver.myAddress, 64000);
                InetAddress destination = null;
                conversation.append("You: " + Driver.message + "\n sent to: " + ip + "  port # " + port + "\n");

                destination = ip;
                connect.send(Driver.message,destination, port);
                typehere.setText(" ");
            }
        });



        //label = new JLabel("Messages"); //declaring the label
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.DARK_GRAY); //picks the color background
        panel.add(typehere, BorderLayout.WEST);
        panel.add(send); //adding button to panel
        panel.add(close);
        panel.add(scroll, BorderLayout.NORTH);
        panel.add(scroll2, BorderLayout.CENTER);
        //conversation.add(scroll);
        //typehere.add(scroll2);

        //conversation.add(label); //adding label to pane

        frame = new JFrame("IP: [" + ip + "]  " + "Port: [" + port + "]");
        frame.setSize(700,700);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); //programs the close button on the window
        frame.setLayout(new BorderLayout());
        frame.add(panel,BorderLayout.SOUTH);
        frame.add(conversation,BorderLayout.CENTER);
        frame.setVisible(true);



    }

    public JTextArea getText(){
        return this.conversation;
    }

}



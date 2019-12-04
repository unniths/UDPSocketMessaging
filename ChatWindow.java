package nov18;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatWindow extends JFrame {
    public static JFrame frame;
    public static JPanel panel;
    public static JTextArea conversation;
    public static JButton send;
    public static JButton close;
    //public static JButton receive;
    public static JTextArea typehere;
    public static JScrollPane scroll;
    public static JScrollPane scroll2;
    private InetAddress ip;
    private int port;

    public ChatWindow(final Socket connect, final InetAddress ip, final int port) {
        try {
            this.ip = InetAddress.getLocalHost();
        } catch (Exception var5) {
            var5.printStackTrace();
            System.exit(-1);
        }

        this.port = 64000;
        typehere = new JTextArea(5, 30);
        typehere.setVisible(true);
        typehere.setBackground(Color.WHITE);
        typehere.setLineWrap(true);
        conversation = new JTextArea(40, 80);
        conversation.setEditable(false);
        conversation.setLineWrap(true);
        scroll = new JScrollPane(conversation);
        scroll2 = new JScrollPane(typehere);
        close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatWindow.frame.dispose();
            }
        });
        send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Driver.message = ChatWindow.typehere.getText().trim();
                InetAddress destination = null;
                ChatWindow.conversation.append("You: " + Driver.message + "\n sent to: " + ip + "  port # " + port + "\n");
                destination = ip;
                connect.send(Driver.message, destination, port);
                ChatWindow.typehere.setText(" ");
            }
        });
        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(Color.DARK_GRAY);
        panel.add(typehere, "West");
        panel.add(send);
        panel.add(close);
        panel.add(scroll, "North");
        panel.add(scroll2, "Center");
        frame = new JFrame("IP: [" + ip + "]  Port: [" + port + "]");
        frame.setSize(700, 700);
        frame.setDefaultCloseOperation(2);
        frame.setLayout(new BorderLayout());
        frame.add(panel, "South");
        frame.add(conversation, "Center");
        frame.setVisible(true);
    }

    public JTextArea getText() {
        return conversation;
    }
}

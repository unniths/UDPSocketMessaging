import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Driver {

    private static JLabel iplbl;
    private static JLabel prlbl;
    private static JTextField iptxt;
    private static JTextField prtxt;
    private static JButton connect;

    public static void main(String[] args) {

        iplbl = new JLabel("IP address: ");
        iptxt = new JTextField(40);
        prlbl = new JLabel("            Port: ");
        prtxt = new JTextField(40);
        connect = new JButton("Connect");
        ButtonHandler listener = new ButtonHandler();
        connect.addActionListener(listener);

        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(iplbl);
        content.add(iptxt);
        content.add(prlbl);
        content.add(prtxt);
        content.add(connect);

        JFrame window = new JFrame("Main window");
        window.setContentPane(content);
        window.setSize(560, 200);
        window.setLocation(500, 700);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private static class ButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            ChatWindow wind = new ChatWindow(iptxt.getText(), prtxt.getText());
            wind.main(null);

        }
    }

}

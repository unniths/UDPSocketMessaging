import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ChatWindow {

    public static String ipname;
    public static String prname;
    public static JTextArea field;
    public static JTextArea field2;
    public static Socket socker;
    public static InetAddress myAddress;
    public static DatagramPacket packet;
    public static String message;

    public ChatWindow(String iptxt, String prtxt) {

        ipname = iptxt;
        prname = prtxt;

    }

    public static void main(String[] args) {

        int port = Integer.parseInt(prname);
        socker = new Socket(port);
        myAddress = null;

        try {
            myAddress = InetAddress.getByName(ipname);
        } catch (Exception a) {
            a.printStackTrace();
            System.exit(-1);
        }

        field = new JTextArea(40, 80);
        field2 = new JTextArea(10, 80);
        field.setLineWrap(true);
        field.setEditable(false);
        field2.setLineWrap(true);
        JButton close3 = new JButton("Close");
        JButton send = new JButton("Send");
        Sending listener = new Sending();
        send.addActionListener(listener);



        JScrollPane scroll = new JScrollPane(field);
        JScrollPane scroll2 = new JScrollPane(field2);

        JPanel conton = new JPanel();
        conton.setLayout(new FlowLayout());
        conton.add(scroll, BorderLayout.NORTH);
        conton.add(scroll2, BorderLayout.CENTER);
        conton.add(close3);
        conton.add(send);

        JFrame wind = new JFrame("IP: [ " + ipname + " ]  " + " Port: [ " + prname + " ] ");
        wind.setContentPane(conton);
        wind.setSize(1000, 1000);
        wind.setLocation(2000, 200);
        wind.setVisible(true);
        wind.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



    }

    private static class Sending implements ActionListener {
        public void actionPerformed(ActionEvent e) {


            packet = socker.receive();
            if (packet != null) {
                message = new String(packet.getData());
                field.append(packet.getAddress() + ": " + message + "\n");
            }

            message = field2.getText();
            socker.send(message, myAddress, 64000);
            field.append("message: "+message+"\n sent to: "+myAddress+" with port: "+prname+". If you don't receive it, click  the {Send} button again, please.\n \n");
            field2.setText("");

            packet = socker.receive();
            if (packet != null) {
                message = new String(packet.getData());
                field.append(packet.getAddress() + ": " + message + "\n");
            }

        }
    }

}

package nov18;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Socket {
    private int myPortNumber;
    private InetAddress myAddress;
    private DatagramSocket mySocket;
    private Thread receiveThread;
    public boolean receiveThreadShouldKeepRunning = true;
    private ConcurrentLinkedQueue<DatagramPacket> messageQueue = new ConcurrentLinkedQueue();

    public Socket(int myPortNumber) {
        this.myPortNumber = myPortNumber;

        try {
            this.myAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException var4) {
            var4.printStackTrace();
            System.exit(-1);
        }

        System.out.println("My IP Address  = " + this.myAddress.getHostAddress());
        System.out.println("My Port Number = " + this.myPortNumber);

        try {
            this.mySocket = new DatagramSocket(myPortNumber, myAddress);
        } catch (SocketException var3) {
            var3.printStackTrace();
            System.exit(-1);
        }

        this.receiveThread = new Thread(new Runnable() {
            public void run() {
                Socket.this.receiveThreadMethod();
            }
        });
        this.receiveThread.setName("Receive Thread for Port Number - " + this.myPortNumber);
        this.receiveThread.start();
    }

    public void send(String message, InetAddress destinationAddress, int destinationPort) {
        byte[] outBuffer = message.getBytes();
        DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length, destinationAddress, destinationPort);

        try {
            this.mySocket.send(outPacket);
        } catch (IOException var7) {
            var7.printStackTrace();
            System.exit(-1);
        }

    }

    public void receiveThreadMethod() {
        System.out.println("\nReceive Thread is Starting!!!!");

        try {
            this.mySocket.setSoTimeout(50);
        } catch (SocketException var9) {
            var9.printStackTrace();
            System.exit(-1);
        }

        do {
            byte[] inBuffer = new byte[1024];

            for(int i = 0; i < inBuffer.length; ++i) {
                inBuffer[i] = 32;
            }

            DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);

            try {
                this.mySocket.receive(inPacket);
                this.messageQueue.add(inPacket);
                byte[] inBuffer1 = inPacket.getData();
                String inMessage = new String(inBuffer1);
                InetAddress senderAddress = inPacket.getAddress();
                int senderPort = inPacket.getPort();
                System.out.println();
                System.out.println("Received Message = " + inMessage);
                System.out.println("Sender Address = " + senderAddress.getHostAddress());
                System.out.println("Sender Port = " + senderPort);
            } catch (SocketTimeoutException var7) {
            } catch (IOException var8) {
                var8.printStackTrace();
                System.exit(-1);
            }
        } while(this.receiveThreadShouldKeepRunning);

    }

    public DatagramPacket receive() {
        return (DatagramPacket)this.messageQueue.poll();
    }

    public void close() {
        System.out.println("\nClosing Socket and Stopping Receive Thread");
        this.receiveThreadShouldKeepRunning = false;

        try {
            TimeUnit.MILLISECONDS.sleep(100L);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
            System.exit(-1);
        }

        this.mySocket.close();
        System.out.println("Socket Closed");
    }

    public int getMyPortNumber() {
        return this.myPortNumber;
    }

    public InetAddress getMyAddress() {
        return this.myAddress;
    }
}

import java.io.IOException; //input/output
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class Socket {

    private int myPortNumber;
    private InetAddress myAddress;
    private DatagramSocket mySocket; //Line 17 and 18 pulled from imported and given names
    private Thread receiveThread; //Threads are made in order to run things concurrently/at the same time
    public boolean receiveThreadShouldKeepRunning = true; //receiveThread is always going to keep running since true
    private ConcurrentLinkedQueue<DatagramPacket> messageQueue = new ConcurrentLinkedQueue<DatagramPacket>();
    //1) It's declaring and initializing (itself) an imported "ConcurrentLinkedQueue" that could take in a <Datagram>,
    //   and it is called messageQueue.
    //2) You're putting something into it from the get go rather than making it null THEN putting a value.

    public Socket(int myPortNumber) { //Making a constructor that can take a portNumber
        this.myPortNumber = myPortNumber;

        try {
            this.myAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            System.exit(-1);
        }


        try {
            this.mySocket = new DatagramSocket(myPortNumber, myAddress);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(-1);
        }

        this.receiveThread = new Thread(
                new Runnable() {
                    public void run() {
                        receiveThreadMethod();
                    }
                });
        this.receiveThread.setName("Receive Thread for Port Number - " + this.myPortNumber);
        this.receiveThread.start();
    }

    public void send(String message,
                     InetAddress destinationAddress,
                     int destinationPort) {

        byte[] outBuffer;
        outBuffer = message.getBytes();

        DatagramPacket outPacket = new DatagramPacket(outBuffer,
                outBuffer.length,
                destinationAddress,
                destinationPort);

        try {
            this.mySocket.send(outPacket);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(-1);
        }
    }

    public void receiveThreadMethod() {

        System.out.println("\nReceive Thread is Starting!!!!");

        try {
            this.mySocket.setSoTimeout(50);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(-1);
        }


        do {
            byte[] inBuffer = new byte[1024];

            for ( int i = 0 ; i < inBuffer.length ; i++ ) {
                inBuffer[i] = ' ';
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

            } catch (SocketTimeoutException ste) {
                // nothing to do
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(-1);
            }

            /*String message = new String(inPacket.getData());
            System.out.println("From IP:" + inPacket.getAddress() + "Port #" + inPacket.getPort());
            messageQueue.add(inPacket);*/

        } while (receiveThreadShouldKeepRunning);

        //System.out.println("Receive Thread is Exiting!!!!");
    }

    public DatagramPacket receive() {
        return this.messageQueue.poll();
    }

    public void close() {
        System.out.println("\nClosing Socket and Stopping Receive Thread");
        this.receiveThreadShouldKeepRunning = false;

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
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

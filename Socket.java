import java.io.IOException;
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
	private DatagramSocket mySocket;
	
	private Thread receiveThread;
	private boolean receiveThreadShouldKeepRunning = true;
	
	// Datagram is in queue method
	private ConcurrentLinkedQueue<DatagramPacket> messageQueue = 
			new ConcurrentLinkedQueue<DatagramPacket>();
	
	
	// Socket function
	public Socket(int myPortNumber) {
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
	
	
	// Receives the message
	public void receiveThreadMethod() {
		
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
			
				// This is added part by professor
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
			
		} while (receiveThreadShouldKeepRunning);

	}

	// receive call
	public DatagramPacket receive() {
		return this.messageQueue.poll();
	}
	
	// This closes the chat GUI
	public void close() {
		this.receiveThreadShouldKeepRunning = false;
		
		try {
			TimeUnit.MILLISECONDS.sleep(100);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
			System.exit(-1);
		}
		
		this.mySocket.close();
	}


	// Returns my own port number
	public int getMyPortNumber() {
		return this.myPortNumber;
	}
	
	// Returns my own ip address
	public InetAddress getMyAddress() {
		return this.myAddress;
	}
}

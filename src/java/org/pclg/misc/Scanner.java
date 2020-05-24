// 2003.11.29 puesto en un paquete para evitar que sean compilados por ant cada vez.
package org.pclg.misc;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
	Chequea la red asociando direcciones IP con nombres de host.
*/
public class Scanner {
	private static final int START_OF_RANGE = 3300;
	private static final int END_OF_RANGE = 3350;
	//String targetIP = "192.168.0." + ii;
	private final String targetIP = "127.0.0.1";
	
	private Scanner() {
		final byte[] msg = {(byte) 'H'};
		final DatagramPacket rcvPacket = new DatagramPacket(msg, msg.length);
		for(int ii = START_OF_RANGE; ii < END_OF_RANGE; ii++) {
			try {
				int targetPort = 7;
				targetPort = ii;
				final DatagramSocket socket = new DatagramSocket();
			//	socket.connect(targetPort, InetAddress getByName(targetIP));
				final DatagramPacket sndPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(targetIP), targetPort);
System.out.println("enviando a " + targetIP + " en el puerto " + targetPort);
				socket.send(sndPacket);

System.out.println("recibiendo ");
				socket.setSoTimeout(5 * 1000);
				socket.receive(rcvPacket);
			//	InetAddress addr = socket.getInetAddress();
				final InetAddress addr = rcvPacket.getAddress();
				System.out.println("Conectado a " + addr.getHostAddress() + "=" + addr.getHostName());
			}
			catch(final SocketTimeoutException ex) {
				System.out.println("SocketTimeoutException");
			}
			catch(final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static void main(final String[] args) {
		new Scanner();
	}
}

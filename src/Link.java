import java.io.InputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 
 * An entity in the current link.
 *
 */
public class Link {
	private String id;

	// Local ports
	private static int TCP_PORT = 4600;
	private static int UDP_PORT = 4803;

	// Current ring, multicast parameters
	private static String ip_diff = "238.255.017.255";
	private static int port_diff = 8888;

	// Current ring, next link's
	private String next_ip;
	private String next_udp;

	// Outer ring, multicast parameters
	private static String out_ip_diff = "";
	private static int out_port_diff = -1;

	// Outer ring,next link's
	private String out_next_ip;
	private String out_next_udp;

	// Multitasking threads
	private Write write;
	private Read read;
	private ReadMulticast read_multi;

	// GBYE case
	private boolean leavingRing = false;

	// Duplicate case
	private boolean duplicate = false;

	/**
	 * Constructs a new object Link.
	 * 
	 * @param is
	 *            the is to set
	 */
	public Link(InputStream is) {
		// Generating ID
		String user = System.getProperty("user.name");
		String ip = Options.getLocalAddress();
		id = user + ip;
		id = id.substring(0, 8);

		write = new Write(is, this);
		read = new Read(this, UDP_PORT);

		read_multi = new ReadMulticast(this, port_diff);

	}

	/**
	 * Constructs a new object Link.
	 * 
	 * @param is
	 *            the is to set
	 * @param tcpPort
	 *            the TCP port to set
	 * @param udpPort
	 *            the UDP port to set
	 */
	public Link(InputStream is, int tcpPort, int udpPort) {
		// Generating ID
		String user = System.getProperty("user.name");
		String ip = Options.getLocalAddress();
		id = user + ip;
		id = id.substring(0, 8);

		setTCP_PORT(tcpPort);
		setUDP_PORT(udpPort);

		write = new Write(is, this);
		read = new Read(this, udpPort);

		read_multi = new ReadMulticast(this, port_diff);
	}

	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns next link's IP.
	 * 
	 * @return the next_ip
	 */
	public String getNext_ip() {
		return next_ip;
	}

	/**
	 * Returns next link's UDP port.
	 * 
	 * @return the next_udp
	 */
	public String getNext_udp() {
		return next_udp;
	}

	/**
	 * Returns next link's IP of the outer ring.
	 * 
	 * @return the out_next_ip
	 */
	public String getOut_next_ip() {
		return out_next_ip;
	}

	/**
	 * Returns next link's UDP port of the outer ring.
	 * 
	 * @return the out_next_udp
	 */
	public String getOut_next_udp() {
		return out_next_udp;
	}

	/**
	 * Returns the write.
	 * 
	 * @return the write
	 */
	public Write getWrite() {
		return write;
	}

	/**
	 * Returns the read.
	 * 
	 * @return the read
	 */
	public Read getRead() {
		return read;
	}

	/**
	 * Returns the read_multi.
	 * 
	 * @return the read_multi
	 */
	public ReadMulticast getRead_multi() {
		return read_multi;
	}

	/**
	 * Returns true if current link wants to leave the ring (inner and outer).
	 * 
	 * @return true if this is disconnecting
	 */
	public boolean isLeavingRing() {
		return leavingRing;
	}

	/**
	 * Returns true if this is duplicating.
	 * 
	 * @return true if duplicating
	 */
	public boolean isDuplicate() {
		return duplicate;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the next IP on inner ring.
	 * 
	 * @param next_ip
	 *            the next_ip to set
	 */
	public void setNext_ip(String next_ip) {
		this.next_ip = next_ip;
	}

	/**
	 * Sets the next UDP port on inner ring.
	 * 
	 * @param next_udp
	 *            the next_udp to set
	 */
	public void setNext_udp(String next_udp) {
		this.next_udp = next_udp;
	}

	/**
	 * Sets next IP on outer ring.
	 * 
	 * @param out_next_ip
	 *            the out_next_ip to set
	 */
	public void setOut_next_ip(String out_next_ip) {
		this.out_next_ip = out_next_ip;
	}

	/**
	 * Sets next UDP port on outer ring.
	 * 
	 * @param out_next_udp
	 *            the out_next_udp to set
	 */
	public void setOut_next_udp(String out_next_udp) {
		this.out_next_udp = out_next_udp;
	}

	/**
	 * Sets the write.
	 * 
	 * @param write
	 *            the write to set
	 */
	public void setWrite(Write write) {
		this.write = write;
	}

	/**
	 * Sets the read.
	 * 
	 * @param read
	 *            the read to set
	 */
	public void setRead(Read read) {
		this.read = read;
	}

	/**
	 * Sets the read_multi.
	 * 
	 * @param read_multi
	 *            the read_multi to set
	 */
	public void setRead_multi(ReadMulticast read_multi) {
		this.read_multi = read_multi;
	}

	/**
	 * Sets the leavingRing.
	 * 
	 * @param leavingRing
	 *            the leavingRing to set
	 */
	public void setLeavingRing(boolean leavingRing) {
		this.leavingRing = leavingRing;
	}

	/**
	 * Sets the duplicate.
	 * 
	 * @param duplicate
	 *            the duplicate to set
	 */
	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	/**
	 * Runs the link.
	 */
	public void live() {
		try {
			// Enables writing message for UDP purpose
			Thread writeUDP = new Thread(write);
			writeUDP.start();

			// Enables receiving UDP message
			Thread readUDP = new Thread(read);
			readUDP.start();

			// Enables receiving UDP Multicast message
			Thread readUDPMulti = new Thread(read_multi);
			readUDPMulti.start();

			// TCP server
			ServerSocket server = new ServerSocket(TCP_PORT);
			while (true) {
				Socket socket = server.accept();

				Connect connectTCP = new Connect(socket, this);
				Thread tcp = new Thread(connectTCP);
				tcp.start();
			}
		} catch (BindException e) {
			System.out.println("Set different ports in 'Link.java'");
			System.err.println("TCP port '" + TCP_PORT + "' is already in use");
			System.err.println("UDP port '" + UDP_PORT + "' is already in use");
			System.err.println("Application closed");
			System.exit(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disconnects from current ring.
	 */
	public void disconnect() {
		next_ip = null;
		next_udp = null;

		if (isDuplicate()) {
			out_next_ip = null;
			out_next_udp = null;
		}
	}

	/**
	 * Returns local TCP port.
	 * 
	 * @return the tcpPort
	 */
	public static int getTcpPort() {
		return TCP_PORT;
	}

	/**
	 * Returns local UDP port.
	 * 
	 * @return the udpPort
	 */
	public static int getUdpPort() {
		return UDP_PORT;
	}

	/**
	 * Returns multicast IP.
	 * 
	 * @return the ip_diff
	 */
	public static String getIp_diff() {
		return ip_diff;
	}

	/**
	 * Returns multicast port.
	 * 
	 * @return the port_diff
	 */
	public static int getPort_diff() {
		return port_diff;
	}

	/**
	 * Returns multicast IP of the outer ring.
	 * 
	 * @return the out_ip_diff
	 */
	public static String getOut_ip_diff() {
		return out_ip_diff;
	}

	/**
	 * Returns multicast port of the outer ring.
	 * 
	 * @return the out_port_diff
	 */
	public static int getOut_port_diff() {
		return out_port_diff;
	}

	/**
	 * Sets the local TCP port.
	 * 
	 * @param tCP_PORT
	 *            the TCP port to set
	 */
	public static void setTCP_PORT(int tCP_PORT) {
		TCP_PORT = tCP_PORT;
	}

	/**
	 * Sets the local UDP port.
	 * 
	 * @param uDP_PORT
	 *            the uDP_PORT to set
	 */
	public static void setUDP_PORT(int uDP_PORT) {
		UDP_PORT = uDP_PORT;
	}

	/**
	 * Sets multicast IP.
	 * 
	 * @param ip_diff
	 *            the ip_diff to set
	 */
	public static void setIp_diff(String ip_diff) {
		Link.ip_diff = ip_diff;
	}

	/**
	 * Sets multicast port.
	 * 
	 * @param port_diff
	 *            the port_diff to set
	 */
	public static void setPort_diff(int port_diff) {
		Link.port_diff = port_diff;
	}

	/**
	 * Sets multicast IP of the outer ring.
	 * 
	 * @param out_ip_diff
	 *            the out_ip_diff to set
	 */
	public static void setOut_ip_diff(String out_ip_diff) {
		Link.out_ip_diff = out_ip_diff;
	}

	/**
	 * Sets multicast port of the outer ring.
	 * 
	 * @param out_port_diff
	 *            the out_port_diff to set
	 */
	public static void setOut_port_diff(int out_port_diff) {
		Link.out_port_diff = out_port_diff;
	}

}

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Treats every received multicasted messages;
 *
 */
public class ReadMulticast implements Runnable {
	private Link link;
	private int port;

	/**
	 * Constructs a new object ReadMulticast.
	 * 
	 * @param link
	 *            the link to set
	 * @param port
	 *            the port to set
	 */
	public ReadMulticast(Link link, int port) {
		this.link = link;
		this.port = port;
	}

	/**
	 * Returns the link.
	 * 
	 * @return the link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * Returns the port.
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the link.
	 * 
	 * @param link
	 *            the link to set
	 */
	public void setLink(Link link) {
		this.link = link;
	}

	/**
	 * Sets the port.
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void run() {
		try {
			byte[] data = new byte[Options.getMaximumMessageSize()];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			while (true) {
				String ip_diff = Link.getIp_diff();
				int port_diff = Link.getPort_diff();

				MulticastSocket ms = new MulticastSocket(port_diff);
				ms.joinGroup(InetAddress.getByName(ip_diff));
				ms.receive(packet);

				String content = new String(packet.getData(), 0, packet.getLength());

				System.out.println();
				System.out.println(" ==== UDP MULTICAST ============");
				System.out.println("| " + content);

				// Prints sender's informations
				if (Options.isVerbose()) {
					System.out.println("\n|\t sent by : " + packet.getAddress().toString());
					System.out.println("|\t on port : " + packet.getPort());
				}
				System.out.println(" =============================");

				doQuery(content);
			}
		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Executes given query.
	 * 
	 * @param content
	 *            to execute
	 */
	public void doQuery(String content) {
		if (content != null) {
			String[] command = content.split(" ");
			String query = command[0];
			query = query.toLowerCase().replace("\n", "");

			switch (query) {

			case "down":
				System.out.println();
				System.out.println(" ===== BROKEN RING =====");
				System.out.println("| Request 'TEST' has been sent.");
				System.out.println("| Though, original caller didn't receive any response");
				System.out.println("| Therefore, current ring might be broken.");
				System.out.println("| Disconnecting from current ring.");
				link.disconnect();
				System.out.println("| Disconnected.");
				System.out.println(" =======================");
				break;

			}
		}
	}
}

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Set;
import java.util.TreeSet;

/**
 * Treats every received UDP message.
 */
public class Read implements Runnable {
	private Link link;
	private Set<String> messages;
	private int port;

	/**
	 * Constructs a new object Read.
	 * 
	 * @param link
	 *            the link to set
	 * @param port
	 *            the port to set
	 */
	public Read(Link link, int port) {
		this.link = link;
		this.port = port;
		messages = new TreeSet<String>();
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
	 * Returns a set of read/written messages.
	 * 
	 * @return the messages
	 */
	public Set<String> getMessages() {
		return messages;
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

	/**
	 * Sets the messages.
	 * 
	 * @param messages
	 *            the messages to set
	 */
	public void setMessages(Set<String> messages) {
		this.messages = messages;
	}

	@Override
	public void run() {
		try {
			DatagramSocket ds = new DatagramSocket(port);
			byte[] data = new byte[Options.getMaximumMessageSize()];

			DatagramPacket packet = new DatagramPacket(data, data.length);
			while (true) {
				ds.receive(packet);

				// Gets content of received UDP
				String content = new String(packet.getData(), 0, packet.getLength());

				// Retrieves message's idm
				String[] command = content.split(" ");

				// Prints content if not already read or sent, and execute it
				if (command != null && command.length >= 2) {

					String idm = command[1];

					if (!link.getRead().getMessages().contains(idm)) {
						System.out.println();
						System.out.println(" ==== UDP MESSAGE ============");
						System.out.println("| " + content);

						// Prints sender's informations
						if (Options.isVerbose()) {
							System.out.println("\n|\t sent by : " + packet.getAddress().toString());
							System.out.println("|\t on port : " + packet.getPort());
						}
						System.out.println(" =============================");

						// Executes query
						doQuery(content);
					}

					if (command[0].equals("TEST")) {
						RingState.setBroken(false);
					}

				}

				// adds message's idm to the set : prevent from sending it
				addToSet(content);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds given content to the set. Given content should be formatted as
	 * follow : "Message idm". It's the second part 'idm' that is added. If it's
	 * not correctly formatted, nothing is added.
	 * 
	 * @param content
	 *            the content to add
	 */
	public void addToSet(String content) {
		if (content != null) {
			String[] command = content.split(" ");
			if (command.length >= 2) {
				String idm = command[1];
				messages.add(idm);
			}
		}
	}

	/**
	 * Executes given query.
	 * 
	 * @param content
	 *            the query to execute
	 */
	public void doQuery(String content) {
		if (content != null) {
			String[] command = content.split(" ");
			String query = command[0];
			query = query.toLowerCase().replace("\n", "");

			switch (query) {

			case "whos":
				Command.resend(link, content);
				Command.memb(link);
				break;

			case "memb":
				Command.resend(link, content);
				break;

			case "gbye":
				Command.eybg(link, content);
				break;

			case "test":
				Command.resend(link, content);
				break;

			case "eybg":
				Command.disconnect(link, content);
				break;

			case "appl":
				Read.doAppl(link, content);
				break;

			default:
				System.out.println(content.replace("\n", "") + ": query not supported");
				break;

			}

		}
	}

	/**
	 * Executes given command which should be an application.
	 * 
	 * @param link
	 *            the link
	 * @param command
	 *            command to execute
	 */
	public static void doAppl(Link link, String command) {
		if (command != null && command.length() >= 3) {
			String[] list = command.split(" ");
			String idApp = list[2];
			idApp = idApp.toLowerCase();

			switch (idApp) {

			case "diff####":
				Command.resend(link, command);
				break;

			case "date####":
				Command.timestamp(link);
				break;

			case "datetime":
				Command.resend(link, command);
				break;

			default:
				System.out.println("\tApplication '" + idApp.toUpperCase() + "' not supported");
				Command.resend(link, command);
				break;
			}
		}
	}

}

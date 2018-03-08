import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Treats every received connection and linked it to the current link.
 *
 */
public class Connect implements Runnable {
	private Socket socket;
	private Link link;

	/**
	 * Constructs a new object Connect. Allows connexion with given link.
	 * 
	 * @param socket
	 *            TCP connexion
	 * @param link
	 *            entity
	 */
	public Connect(Socket socket, Link link) {
		this.socket = socket;
		this.link = link;
	}

	/**
	 * Returns the socket.
	 * 
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Sets the socket.
	 * 
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		String connexion = socket.getInetAddress().getHostAddress();
		try {

			Message message = new Message(null);

			System.out.println();
			System.out.println(" ===== CLIENT CONNEXION =====");
			System.out.println("| The client '" + connexion + "' is etablishing a connexion");

			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

			if (!link.isDuplicate()) {

				/*
				 * Checking if current this.link is not in a ring, if not,
				 * creates it : it means the ring contains two entities :
				 * this.link and the caller. Therefore, the IP and UDP port sent
				 * are this.link's'.
				 */
				if ((link.getNext_ip() == null) && (link.getNext_udp() == null)) {

					if (Options.isVerbose()) {
						System.out.println("| Current link is not in a ring.");
						System.out.println("| Initializing a ring with '" + connexion + "'");
					}

					String ip = Options.toProtocoleIP(Options.getLocalAddress());
					int udpPort = Link.getUdpPort();

					message.setContent(
							"WELC " + ip + " " + udpPort + " " + Link.getIp_diff() + " " + Link.getPort_diff());
				}
				/*
				 * Actually, this is already in a ring. The IP and UDP port sent
				 * are this.link's next's. TODO error with next_ip, should be
				 * local IP in duplication case
				 */
				else {
					try {
						String next_ip = link.getNext_ip();
						String next_udpPort = link.getNext_udp();

						message.setContent("WELC " + next_ip + " " + next_udpPort + " " + Link.getIp_diff() + " "
								+ Link.getPort_diff());
					} catch (NullPointerException e) {
						System.err.println("| Current link is not set");
					}
				}

				/*
				 * Sends the welcome message : WELC ip port ip-diff port-diff
				 */
				pw.println(message.getContent());
				pw.flush();
				System.out.println("|\tsent : '" + message.getContent() + "'");

				/*
				 * Now, starting to treat the received message.
				 */
				String receive = br.readLine();
				System.out.println("|\treceived : '" + receive + "'");

				// Testing if received message is correctly formated
				String[] command = receive.split(" ");
				if (command != null && command.length >= 3) {

					command[0] = command[0].toLowerCase();

					// Insertion case
					if (command[0].equals("newc") && Options.isIPAddress(command[1]) && Options.isPort(command[2])) {
						link.setNext_ip(Options.toProtocoleIP(command[1]));
						link.setNext_udp(command[2]);

						System.out.println("| [ Insertion process ]");

						if (Options.isVerbose()) {
							System.out.println("| Next IP set to : " + link.getNext_ip());
							System.out.println("| Next UDP port set to : " + link.getNext_udp());
						}

						pw.println("ACKC");
						pw.flush();

						System.out.println("| Link '" + connexion + "' successly added to the ring.");
					}

					// Duplication case
					else if (command[0].equals("dupl")) {

						System.out.println("| [ Duplication process ]");

						if (!link.isDuplicate()) {

							if (command.length >= 5) {
								String ip = command[1];
								String port = command[2];
								String ip_diff = command[3];
								String port_diff = command[4];

								boolean isIP = Options.isIPAddress(ip);
								boolean isPort = Options.isPort(port);
								boolean isMulticastIP = Options.isIPAddress(ip_diff);
								boolean isMulticastPort = Options.isPort(port_diff);

								if (isIP && isPort && isPort && isMulticastIP && isMulticastPort) {
									link.setOut_next_ip(ip);
									link.setOut_next_udp(port);
									Link.setOut_ip_diff(ip_diff);
									Link.setOut_port_diff(Integer.parseInt(port_diff));
									link.setDuplicate(true);

									System.out.println("| Initializing outer ring settings.");
									if (Options.isVerbose()) {
										System.out.println("| Next IP set to : " + ip);
										System.out.println("| Next UDP port set to : " + port);
										System.out.println("| Multicast IP set to : " + ip_diff);
										System.out.println("| Multicast port set to : " + port_diff);
										System.out.println("| Current link is now a duplicate : " + link.isDuplicate());
									}

									pw.println("ACKD" + " " + Link.getUdpPort());
									pw.flush();

									System.out.println("| Duplication success.");

								} else {
									String incorrect = "Incorrect format message, expected : 'NEWC|DUPL ip port ip-diff port-diff' (ip must given in IPv4)";

									pw.println(incorrect);
									pw.flush();

									System.out.println("| TCP connexion from '" + connexion + "' failed");
								}
							} else {
								String incorrect = "Lacking informations, expected : DUPL ip port ip-diff port-diff";
								pw.println(incorrect);
								pw.flush();
								System.out.println("| TCP connexion from '" + connexion + "' failed");
							}

						} else {
							pw.println("NOTC");
							pw.flush();

							if (Options.isVerbose()) {
								System.out.println("| Current link is already a duplicate.");
								System.out.println("| Duplication request from '" + connexion + "' failed");
							}
						}

					}

					// Wrong format
					else {
						String incorrect = "Incorrect format message, expected : 'NEWC|DUPL ip port ip-diff port-diff' (ip must given in IPv4)";

						pw.println(incorrect);
						pw.flush();

						System.out.println("| TCP connexion from '" + connexion + "' failed");
					}

				} else {
					System.out.println("| TCP connexion from '" + connexion + "' failed");
				}
			} else {
				pw.println("NOTC");
				pw.flush();

				if (Options.isVerbose()) {
					System.out.println("| Current link is already a duplicate.");
					System.out.println("| Duplication request from '" + connexion + "' failed");
				}
			}
			br.close();
			pw.close();

			System.out.println(" ============================");
		} catch (NullPointerException e) {
			System.out.println("| '" + connexion + "' has aborted connexion");
			System.out.println("| TCP connexion from '" + connexion + "' failed");
			System.out.println(" ============================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

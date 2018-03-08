import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 * Commands to execute.
 *
 */
public class Command {

	/**
	 * Returns true if this.link next IP and UDP port are correctly set during
	 * TCP connexion, else false.
	 * 
	 * @param link
	 *            the link to set
	 * @param command
	 *            command line in the terminal
	 * @return true if the next IP and UDP port are correctly set
	 */
	public static boolean connexionTCP(Link link, String[] command) {

		try {

			System.out.println(" ===== TCP : LINK START =====");

			String ip = command[1];
			String port = command[2];

			if (Options.isIPAddress(ip) && Options.isPort(port)) {

				Socket socket = new Socket(ip, Integer.parseInt(port));
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

				/*
				 * Treating welcome message : checking if received parameters
				 * are correctly formated
				 */
				String welcome = br.readLine();
				System.out.println("|\treceived '" + welcome + "'");
				String[] received = welcome.split(" ");

				if (received != null && received.length >= 3) {

					// Sets IP and UDP port
					String ip_received = received[1];
					String udpPort_received = received[2];

					if (Options.isVerbose()) {

						String correctIP = (Options.isIPAddress(ip_received)) ? "yes" : "no";
						String correctPort = (Options.isPort(udpPort_received)) ? "yes" : "no";

						System.out.println("| IP '" + ip_received + "' is correctly formated : " + correctIP);
						System.out.println(
								"| UDP port '" + udpPort_received + "' is correctly formated : " + correctPort);

					}

					link.setNext_ip(ip);
					link.setNext_udp(udpPort_received);

					// Sets multicast IP and port
					if (received.length >= 5) {
						String ip_diff = received[3];
						String port_diff = received[4];

						if (Options.isVerbose()) {
							String correctIp_iff = (Options.isIPAddress(ip_diff)) ? "yes" : "no";
							String correctPort_diff = (Options.isPort(port_diff)) ? "yes" : "no";

							System.out.println(
									"| Multicast IP '" + ip_diff + "' is correctly formated : " + correctIp_iff);
							System.out.println("| Multicast port '" + correctPort_diff + "' is correctly formated : "
									+ correctPort_diff);
						}

						Link.setIp_diff(ip_diff);
						Link.setPort_diff(Integer.parseInt(port_diff));
					}

					// Sends WELC
					String sending = "NEWC " + Options.toProtocoleIP(Options.getLocalAddress()) + " "
							+ Link.getUdpPort();
					if (Options.isVerbose()) {
						System.out.println("|\tsent '" + sending + "'");
					}
					pw.println(sending);
					pw.flush();

					/*
					 * Prints last message : should be 'ACKC\n'
					 */
					String ackc = br.readLine();
					System.out.println("|\treceived '" + ackc + "'");

					pw.close();
					br.close();
					socket.close();

					System.out.println(" ===== operation success =====");

					return true;
				} else {
					System.out.println("| Received WELC message format is not correctly formated.");
					socket.close();
					br.close();
					pw.close();
					return false;
				}
			} else {
				System.out.println("| Given IP and UDP port are not correctly formated.");
				return false;
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| unexpected format for command TCP");
				System.out.println("| correct usage is : TCP ip udp_port");
				System.out.println(" ===== End of error message =====");
			}
		} catch (UnknownHostException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| Can't log to " + command[1]);
				System.out.println("| Make sure distant server is currently running");
				System.out.println("| or verify that given IP is correct.");
				System.out.println(" ===== End of error message =====");
			}
		} catch (ConnectException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| Port '" + command[2] + "' seems to be taken or is not open, can't log in.");
				System.out.println(" ===== End of error message =====");
			}
		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Requests a TCP connexion for duplication.
	 * 
	 * @param link
	 *            the link to set
	 * @param command
	 *            command line in terminal
	 */
	public static void connexionDUPL(Link link, String[] command) {
		try {
			System.out.println(" ===== TCP : DUPLICATION ===== ");

			if (command != null && command.length >= 3) {
				String ip = command[1];
				String port = command[2];

				if (Options.isIPAddress(ip) && Options.isPort(port)) {
					Socket socket = new Socket(ip, Integer.parseInt(port));
					BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

					/*
					 * Treating welcome message : checking if received
					 * parameters are correctly formated
					 */
					String welcome = br.readLine();
					System.out.println("|\treceived '" + welcome + "'");
					String[] received = welcome.split(" ");

					if (received != null && received.length >= 3) {
						String ipReceived = received[1];
						String udpPortReceived = received[2];

						boolean isIPAddress = Options.isIPAddress(ipReceived);
						boolean isPort = Options.isPort(udpPortReceived);

						if (Options.isVerbose()) {
							System.out.println("| IP '" + ipReceived + "' is correctly formated : " + isIPAddress);
							System.out
									.println("| UDP port '" + udpPortReceived + "' is correctly formated : " + isPort);
						}

						if (isIPAddress && isPort) {

							link.setNext_ip(ipReceived);
							link.setNext_udp(udpPortReceived);

							if (Options.isVerbose()) {
								System.out.println("| Next IP set to : " + link.getNext_ip());
								System.out.println("| Next UDP port set to : " + link.getNext_udp());
							}

							/*
							 * Begins sending message.
							 */
							String dupl = "DUPL";
							String sending = dupl + " " + Options.toProtocoleIP(Options.getLocalAddress()) + " "
									+ Link.getUdpPort() + " " + Link.getIp_diff() + " " + Link.getPort_diff();

							pw.println(sending);
							pw.flush();

							if (Options.isVerbose()) {
								System.out.println("|\tsent : " + sending);
							}

							// ACKD UDP
							String ackd = br.readLine();
							System.out.println("|\treceived : " + ackd);

							// Retrieves UDP port
							if (ackd != null) {
								String[] list = ackd.split(" ");

								if (list != null && list.length >= 2) {
									String udp = list[1];

									if (!Options.isPort(udp)) {
										System.out.println("UDP port '" + udp + "' is not correct.");
									} else {
										link.setNext_udp(udp);
									}

								}
							}

							System.out.println("| Duplication success.");

						} else {
							System.out.println("| Received IP or UDP port is not correctly formated");
							System.out.println("| Aborting connexion.");
							System.out.println("| TCP connexion with '" + ip + "' failed");
						}

					}

					socket.close();
					br.close();
					pw.close();
				} else {
					System.out.println("| Given IP or UDP port is not correctly formated");
				}
			} else {
				System.out.println("| Lacking arguments, expected : DUPL ip port ip-diff port-diff");
			}

			System.out.println(" ==============================");
		} catch (ArrayIndexOutOfBoundsException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| unexpected format for command TCP");
				System.out.println("| correct usage is : TCP ip udp_port");
				System.out.println(" ===== End of error message =====");
			}
		} catch (UnknownHostException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| Can't log to " + command[1]);
				System.out.println("| Make sure distant server is currently running");
				System.out.println("| or verify that given IP is correct.");
				System.out.println(" ===== End of error message =====");
			}
		} catch (ConnectException e) {
			if (Options.isVerbose()) {
				System.out.println(" ===== Error : '" + e + "' =====");
				System.out.println("| Port '" + command[2] + "' seems to be taken or is not open, can't log in.");
				System.out.println(" ===== End of error message =====");
			}
		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Requests for present link in inner ring and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void whos(Link link) {
		// Generates message
		String whos = "WHOS";
		Message message = new Message(whos);

		// Formats message
		String content = whos + " " + message.getIdm();

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		// Sends on inner ring
		Command.send(link, content);

		// Sends on outer ring
		if (link.isDuplicate()) {
			Command.sendOuter(link, content);
		}
	}

	/**
	 * Sends MEMB on inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void memb(Link link) {
		// Generates message
		String memb = "MEMB";
		Message message = new Message(memb);

		// Formats message
		String content = memb + " " + message.getIdm() + " " + link.getId() + " "
				+ Options.toProtocoleIP(Options.getLocalAddress()) + " " + Link.getUdpPort();

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		// Sends on inner ring
		Command.send(link, content);

		// Sends on outer ring
		if (link.isDuplicate()) {
			Command.sendOuter(link, content);
		}
	}

	/**
	 * Sends GBYE on inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void gbye(Link link) {

		// Generates message
		String gbye = "GBYE";
		Message message = new Message(gbye);

		// Formats message
		String content = gbye + " " + message.getIdm() + " " + Options.toProtocoleIP(Options.getLocalAddress()) + " "
				+ Link.getUdpPort() + " " + link.getNext_ip() + " " + link.getNext_udp();

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		// Sends on inner ring
		Command.send(link, content);

		// Sends on outer ring
		if (link.isDuplicate()) {
			String content2 = gbye + " " + message.getIdm() + " " + Options.toProtocoleIP(Options.getLocalAddress())
					+ " " + Link.getUdpPort() + " " + link.getOut_next_ip() + " " + link.getOut_next_udp();

			Command.sendOuter(link, content2);
		}

		// Gets link ready to leave the ring
		link.setLeavingRing(true);

	}

	/**
	 * Sends EYBG to the next link on inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 * @param content
	 *            retrieves idm from it
	 */
	public static void eybg(Link link, String content) {

		if (content != null) {
			String[] command = content.split(" ");

			if (command != null && command.length >= 6) {
				String idm = command[1];
				String ip = command[2];
				String port = command[3];
				String ip_succ = command[4];
				String port_succ = command[5];

				if (!link.getRead().getMessages().contains(idm)) {

					boolean innerRing = link.getNext_ip().equals(ip) && link.getNext_udp().equals(port);
					boolean outerRing = false;

					
					if (link.getOut_next_ip() != null && link.getOut_next_udp() != null) {
						outerRing = link.getOut_next_ip().equals(ip) && link.getOut_next_udp().equals(port);
					}
					
					
					// If next link wants to leave the ring
					if (innerRing || outerRing) {
						
						// Generates message
						String EYBG = "EYBG";
						Message message = new Message(EYBG);

						// Formats message
						String command2 = EYBG + " " + message.getIdm();

						// Adds message's idm to the set : to avoid sending
						// it again
						link.getRead().getMessages().add(message.getIdm());

						// Sends message
						Command.send(link, command2);

						if (link.isDuplicate()) {
							Command.sendOuter(link, command2);
						}
						
						// Sets IP, UDP port
						link.setNext_ip(ip_succ);
						link.setNext_udp(port_succ);

						if (Options.isVerbose()) {
							System.out.println(" ===== SWITCHING GEARS =====");
							System.out.println("| Next IP set to : " + ip_succ);
							System.out.println("| Next UDP port set to : " + port_succ);
							System.out.println(" ===========================");
						}

					} else {

						if (Options.isVerbose()) {
							System.out.println(" ===== SWITCHING GEARS =====");
							System.out.println("| IP '" + ip + "' doesn't match successor's");
							System.out.println("| UDP port '" + port + "' doesn't match successor's");
							System.out.println("| Resending GBYE message to next link");
							System.out.println(" ===========================");
						}

						// Adds given message to list and sends it to the
						// next link
						Command.resend(link, content);
						link.getRead().getMessages().add(idm);

					}

				}
			}

		}

	}

	/**
	 * Sends TEST on inner ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void test(Link link) {
		// Generates message
		String test = "TEST";
		Message message = new Message(test);

		// Formats message
		String command = test + " " + message.getIdm() + " " + Options.toProtocoleIP(Link.getIp_diff()) + " "
				+ Link.getPort_diff();

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		Command.send(link, command);

		RingState.setBroken(true);
		Command.broken(link);
	}

	/**
	 * Sends DIFF on the inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 * @param content
	 *            of the message
	 */
	public static void diff(Link link, String content) {
		if (content != null) {
			// Generates message
			String appl = "APPL";
			String diff = Options.toProtocoleAPPL("DIFF");

			// given content is : 'diff message', we only want 'message'
			content = content.substring(5);

			int offset = (content.getBytes().length / Options.getMaximumMessageSize());

			int j = 0;
			int k = Options.getMaximumMessageSize();

			for (int i = 0; i < offset + 1; i++) {
				Message message = new Message(diff);

				String cut = "";

				try {
					cut = content.substring(j, k);
				} catch (StringIndexOutOfBoundsException e) {
					cut = content.substring(j, content.length());
				}

				j = k;
				k += Options.getMaximumMessageSize();

				// Formats message
				String command = appl + " " + message.getIdm() + " " + diff + " "
						+ Options.toProtocoleSizeMess(content.getBytes().length + "") + " " + cut;

				// Adds message's idm to the set : to avoid sending it again
				link.getRead().getMessages().add(message.getIdm());

				// Sends on inner ring
				Command.send(link, command);

				// Sends on outer ring
				if (link.isDuplicate()) {
					Command.sendOuter(link, command);
				}
			}
		} else {
			if (Options.isVerbose()) {
				System.out.println("DIFF, correct syntax is : DIFF myMessage");
			}
		}
	}

	/**
	 * Requests timestamp on inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void date(Link link) {
		// Generates message
		String appl = "APPL";
		String time = Options.toProtocoleAPPL("DATE");
		Message message = new Message(time);

		// Formats message
		String command = appl + " " + message.getIdm() + " " + time;

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		// Sends on inner ring
		Command.send(link, command);

		// Sends on outer ring
		if (link.isDuplicate()) {
			Command.sendOuter(link, command);
		}
	}

	/**
	 * Sends timestamp on inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void timestamp(Link link) {
		// Generates message
		String appl = "APPL";
		String datetime = Options.toProtocoleAPPL("DATETIME");
		Message message = new Message(datetime);

		// Generates timestamp
		Date date = new Date();

		// Formats message
		String command = appl + " " + message.getIdm() + " " + datetime + " " + date.toString();

		// Adds message's idm to the set : to avoid sending it again
		link.getRead().getMessages().add(message.getIdm());

		// Sends on inner ring
		Command.send(link, command);

		// Sends on outer ring
		if (link.isDuplicate()) {
			Command.sendOuter(link, command);
		}
	}

	/**
	 * Waits for a certain delay and if TEST message hasn't been received, then
	 * disconnect from the current ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void broken(Link link) {
		long delay = 10000; // 10 seconds

		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				if (RingState.isBroken()) {
					System.out.println();
					System.out.println(" ===== RING STATUS =====");
					System.out.println("| 'TEST' message has been sent since " + (delay / 1000) + " seconds");
					System.out.println("| Though, not any response has been detected.");
					System.out.println("| Therefore, current ring might be broken.");
					System.out.println("| Sending 'DOWN' to all connected links.");
					System.out.println(" =======================");
					Command.down(link);
					this.cancel();
					return;
				} else {
					System.out.println();
					System.out.println(" ===== RING STATUS =====");
					System.out.println("| Not any problem detected");
					System.out.println(" =======================");
					this.cancel();
					return;
				}
			}
		};

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, delay, 1000);
	}

	/**
	 * Resends received message on the inner and outer ring.
	 * 
	 * @param link
	 *            the link to set
	 * @param content
	 *            to resend
	 */
	public static void resend(Link link, String content) {
		if (content != null) {
			String[] command = content.split(" ");

			/*
			 * If the message's idm is not in the set, then sends the message.
			 */
			if (command != null && command.length >= 2) {
				String idm = command[1];

				if (!link.getRead().getMessages().contains(idm)) {
					// Adds message's idm to the set
					link.getRead().getMessages().add(idm);

					// Sends message on inner ring
					Command.send(link, content);

					// Sends message on outer ring
					if (link.isDuplicate()) {
						Command.sendOuter(link, content);
					}

				} else {
					if (Options.isVerbose()) {
						System.out.println("\tmessage:\n \t'" + content + "'\n\thas been already sent/received");
					}
				}
			}

		}

	}

	/**
	 * Multicasts DOWN on the inner ring.
	 * 
	 * @param link
	 *            the link to set
	 */
	public static void down(Link link) {
		try {
			String ip_diff = Link.getIp_diff();
			int port_diff = Link.getPort_diff();

			DatagramSocket ds = new DatagramSocket();
			InetSocketAddress address = new InetSocketAddress(ip_diff, port_diff);
			byte[] send = new byte[Options.getMaximumMessageSize()];

			String down = "DOWN";
			send = down.getBytes();

			DatagramPacket packet = new DatagramPacket(send, send.length, address);
			ds.send(packet);
			ds.close();

		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Disconnects from current ring.
	 * 
	 * @param link
	 *            the link to set
	 * @param content
	 *            to retrieves idm
	 */
	public static void disconnect(Link link, String content) {
		if (link != null) {

			if (link.isLeavingRing()) {

				if (!link.isDuplicate()) {
					link.disconnect();

					if (Options.isVerbose()) {
						System.out.println(" ==== DISCONNECTING ==========================");
						System.out.println("| Withdrawing from current ring.              |");
						System.out.println("| Next IP and next UDP port are set to null.  |");
						System.out.println("| Operation success.                          |");
						System.out.println(" =============================================");
					}
				}
				// Waits to receive both EYBG from inner and outer rings before
				// leaving
				else {

					if (content != null) {
						String[] command = content.split(" ");

						if (command != null && command.length >= 2) {

							String idm = command[1];

							if (!link.getRead().getMessages().contains(idm)) {

								// Adds message's idm to the set
								link.getRead().getMessages().add(idm);

								RingState.setWantToLeave(RingState.getWantToLeave() + 1);

								if (RingState.getWantToLeave() == 2) {

									if (Options.isVerbose()) {
										System.out.println(" ==== DISCONNECTING ==========================");
										link.disconnect();
										System.out.println("| Withdrawing from inner ring and outer ring. |");
										System.out.println("| Next IP and next UDP port are set to null.  |");
										System.out.println("| Operation success.                          |");
										System.out.println(" =============================================");
									}

									RingState.setWantToLeave(0);
								}
							}
						}

					}

				}
			} else {

				if (Options.isVerbose()) {
					System.out.println(" ==== DISCONNECTING ==========================");
					System.out.println("| This link didn't ask to leave current ring. |");
					System.out.println("| Operation failed.                           |");
					System.out.println(" =============================================");
				}

			}
		}
	}

	/**
	 * Returns inner ring's informations.
	 * 
	 * @param link
	 *            current informations
	 * @return inner ring's informations
	 */
	public static String info(Link link) {
		String separator = " ===== RESUME =====";
		String ip = "| IP : " + Options.getLocalAddress();
		String tcp_port = "| Local TCP port : " + Link.getTcpPort();
		String udp_port = "| Local UDP " + Link.getUdpPort();
		String next_ip = "| Next IP : " + link.getNext_ip();
		String next_udp = "| Next UDP port : " + link.getNext_udp();
		String ip_diff = "| Multicast IP : : " + Link.getIp_diff();
		String port_diff = "| Multicast port : " + Link.getPort_diff();
		String separatorLine = " ==================";

		String info = ip + "\n" + tcp_port + "\n" + udp_port + "\n" + next_ip + "\n" + next_udp + "\n" + ip_diff + "\n"
				+ port_diff + "\n";

		return separator + "\n" + info + separatorLine + "\n";
	}

	/**
	 * Returns outer ring's informations.
	 * 
	 * @param link
	 *            outer ring
	 * @return outer ring's informations.
	 */
	public static String info2(Link link) {
		String separator = " ===== OUTER RING RESUME =====";
		String isDuplicate = "| Duplicate : " + link.isDuplicate();
		String ip = "| Next IP : " + link.getOut_next_ip();
		String udp_port = "| Next UDP port : " + link.getOut_next_udp();
		String ip_diff = "| Multicast IP : " + Link.getOut_ip_diff();
		String port_diff = "| Multicast port : " + Link.getOut_port_diff();
		String separatorLine = " ===========================";

		String info = isDuplicate + "\n" + ip + "\n" + udp_port + "\n" + ip_diff + "\n" + port_diff + "\n";

		return separator + "\n" + info + separatorLine + "\n";
	}

	/**
	 * Returns all read/written messages' idm.
	 * 
	 * @param link
	 *            mailbox
	 * @return list of read/written messages' idm
	 */
	public static String mail(Link link) {
		Set<String> messages = link.getRead().getMessages();
		String info = "";
		String separator = "===== MAIL =====";
		String received = "Total : " + messages.size();
		String content = "";
		String separatorLine = "================";

		for (String idm : messages) {
			content += idm.replace("\n", "") + "\n";
		}

		info = separator + "\n" + received + "\n" + content + separatorLine;

		return info;
	}

	/**
	 * Sends given content on inner ring.
	 * 
	 * @param link
	 *            IP, UDP
	 * @param content
	 *            to send
	 */
	private static void send(Link link, String content) {
		try {
			DatagramSocket ds = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(link.getNext_ip());
			byte[] send = new byte[Options.getMaximumMessageSize()];

			// Sends message
			send = content.getBytes();
			DatagramPacket packet = new DatagramPacket(send, send.length, ipAddress,
					Integer.parseInt(link.getNext_udp()));
			ds.send(packet);

			// Prints sent message
			if (Options.isVerbose()) {
				System.out.println("\tmessage sent :");
				System.out.println("\t" + content);
			}

			ds.close();
		} catch (NumberFormatException e) {
			if (Options.isVerbose()) {
				System.err.println("Next IP or UDP port is not set.");
			}
		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Sends given content on outer ring.
	 * 
	 * @param link
	 *            the link
	 * @param content
	 *            to send
	 */
	public static void sendOuter(Link link, String content) {
		try {
			DatagramSocket ds = new DatagramSocket();
			InetAddress ipAddress = InetAddress.getByName(link.getOut_next_ip());
			byte[] send = new byte[Options.getMaximumMessageSize()];

			// Sends message
			send = content.getBytes();
			DatagramPacket packet = new DatagramPacket(send, send.length, ipAddress,
					Integer.parseInt(link.getOut_next_udp()));
			ds.send(packet);

			// Prints sent message
			if (Options.isVerbose()) {
				System.out.println("\tmessage sent on outer ring :");
				System.out.println("\t" + content);
			}

			ds.close();
		} catch (NumberFormatException e) {
			if (Options.isVerbose()) {
				System.err.println("Next IP or UDP port is not set.");
			}
		} catch (Exception e) {
			if (Options.isVerbose()) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns a list of available commands.
	 * 
	 * @return available commands
	 */
	public static String help() {
		String[] list = new String[13];
		String command = " ===== COMMANDS =====";
		String content = "";
		String separator = " ===================";

		String help = "HELP : display available commands.";

		/*
		 * Ring's basic commands
		 */
		String tcp = "TCP [ip] [port] : etablish a connexion with given IP.";
		String dupl = "DUPL [ip] [port] : ask for the given IP to duplicate itself. ";
		String whos = "WHOS : ask personal informations (such as Facebook, Instagram accounts) to all entities present in the current link.";
		String gbye = "GBYE : leave the current ring, wait for incoming 'EYBG' before doing it.";
		String test = "TEST : test if the current ring is broken, if so, disconnect from it.";

		/*
		 * APPL commands
		 */
		String diff = "DIFF [message] : send given message on the ring.";
		String date = "DATE : ask the datetime.";

		/*
		 * Personal commands use
		 */
		String info = "INFO : display current link's informations : IP, TCP port, UDP port, etc.";
		String info2 = "INFO2 : display outer ring's informations.";
		String mail = "MAIL : display sent/written UDP messages' idm.";
		String verbose = "VERBOSE [1|0] : set verbose to true|false.";
		String exit = "EXIT : exit the program.";

		list[0] = help;
		list[1] = tcp;
		list[2] = dupl;
		list[3] = whos;
		list[4] = gbye;
		list[5] = test;
		list[6] = diff;
		list[7] = date;
		list[8] = info;
		list[9] = info2;
		list[10] = mail;
		list[11] = verbose;
		list[12] = exit;

		for (int i = 0; i < list.length; i++)
			content += "| " + list[i] + "\n";

		return command + "\n" + content + separator;
	}

}


/**
 * Main.
 */
public class Main {

	/**
	 * Main.
	 * 
	 * @param args
	 *            arguments
	 */
	public static void main(String[] args) {
		Options.setVerbose(true);

		Link link;

		/*
		 * Sets TCP and UDP ports.
		 */
		if (args.length >= 2) {
			String tcpPort = args[0];
			String udpPort = args[1];

			boolean isTcpPort = Options.isPort(tcpPort);
			boolean isUdpPort = Options.isPort(udpPort);

			if (isTcpPort && isUdpPort)
				link = new Link(System.in, Integer.parseInt(tcpPort), Integer.parseInt(udpPort));
			else {
				System.out.println("At least one of the given argument is not correctly formated.");
				System.out.println("Link started with default values.");
				link = new Link(System.in);
			}
		} else {
			link = new Link(System.in);
		}

		if (Options.isVerbose()) {
			System.out.println(" ==== Verbose triggered : ON ======");
			System.out.println("| IP : " + Options.toProtocoleIP(Options.getLocalAddress()) + "              |");
			System.out.println("| ID : " + link.getId() + "                     |");
			System.out.println("| Local TCP port : " + Link.getTcpPort() + "             |");
			System.out.println("| Local UDP port : " + Link.getUdpPort() + "             |");
			System.out.println("| Multicast IP   : " + Options.toProtocoleIP(Link.getIp_diff()) + "  |");
			System.out.println("| Multicast port : " + Link.getPort_diff() + "             |");
			System.out.println(" ==================================");
		}

		System.out.println("You may want to run 'HELP' command first.");

		try {
			link.live();
		} catch (Exception e) {

		}
	}

}

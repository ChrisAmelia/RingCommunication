import java.io.InputStream;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * Treats every given command on the input stream (should be standard input).
 *
 */
public class Write implements Runnable {
	private InputStream is;
	private Scanner scanner;
	private Link link;

	/**
	 * Constructs a new object Write.
	 * 
	 * @param is
	 *            the is to set
	 * @param link
	 *            the link to set
	 */
	public Write(InputStream is, Link link) {
		this.is = is;
		this.link = link;
		scanner = new Scanner(is);
	}

	/**
	 * Returns the current input stream.
	 * 
	 * @return the is
	 */
	public InputStream getIs() {
		return is;
	}

	/**
	 * Returns the scanner.
	 * 
	 * @return the scanner
	 */
	public Scanner getScanner() {
		return scanner;
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
	 * Sets the input stream of the scanner.
	 * 
	 * @param is
	 *            the is to set
	 */
	public void setIs(InputStream is) {
		this.is = is;
	}

	/**
	 * Sets the scanner.
	 * 
	 * @param scanner
	 *            the scanner to set
	 */
	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
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

	@Override
	public void run() {
		while (true) {
			// String id = link.getId();
			String user = System.getProperty("user.name");
			String dir = System.getProperty("user.dir");
			String machine = "";
			try {
				machine = InetAddress.getLocalHost().getHostName();
			} catch (Exception e) {
			}

			String shell = user + "@" + machine + ":~" + dir + "$ ";
			System.out.print(shell);

			String content = scanner.nextLine();
			Message message = new Message(content);

			String[] command = message.getContent().split(" ");

			if (command != null && command.length >= 1) {

				command[0] = command[0].toLowerCase();

				switch (command[0]) {

				case "tcp":
					Command.connexionTCP(link, command);
					break;

				case "dupl":
					Command.connexionDUPL(link, command);
					break;

				case "info":
					String info = Command.info(link);
					System.out.println(info);
					break;

				case "info2":
					String info2 = Command.info2(link);
					System.out.println(info2);
					break;

				case "mail":
					String mail = Command.mail(link);
					System.out.println(mail);
					break;

				case "memb":
					Command.memb(link);
					break;

				case "whos":
					Command.whos(link);
					break;

				case "gbye":
					Command.gbye(link);
					break;

				case "test":
					Command.test(link);
					break;

				case "diff":
					Command.diff(link, content);
					break;

				case "date":
					Command.date(link);
					break;

				case "help":
					String help = Command.help();
					System.out.println(help);
					break;

				case "":
					break;

				case "verbose":
					boolean verbose = Options.isVerbose();

					if (command.length == 1) {
						System.out.println(verbose);
					} else if (command.length >= 2) {

						try {
							int value = Integer.parseInt(command[1]);

							if (value == 1) {
								Options.setVerbose(true);
							} else {
								Options.setVerbose(false);
							}

							System.out.println("Verbose set to " + Options.isVerbose());

						} catch (NumberFormatException e) {
							System.err.println(
									"Second argument must be 1 or 0, e.g : 'verbose 1' to trigger verbose mode");
						}

					}
					break;

				case "exit":
					System.exit(0);
					break;

				default:
					if (Options.isVerbose()) {
						System.out.println(command[0] + ": command not found");
					}
					break;

				}

			}
		}
	}

}

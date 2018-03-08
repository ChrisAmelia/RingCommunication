import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats, checks.
 *
 */
public class Options {
	private static boolean VERBOSE = false;
	private static final int MAXIMUM_MESSAGE_SIZE = 512;

	/**
	 * Returns verbose.
	 * 
	 * @return verbose
	 */
	public static boolean isVerbose() {
		return VERBOSE;
	}

	/**
	 * Sets the verbose
	 * 
	 * @param verbose
	 *            true or false
	 */
	public static void setVerbose(boolean verbose) {
		VERBOSE = verbose;
	}

	/**
	 * Returns non local IP address.
	 * 
	 * @return ip address
	 */
	public static String getLocalAddress() {
		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile("(\\d)+.(\\d)+.(\\d)+.(\\d)+");

		try {
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
			for (; n.hasMoreElements();) {
				NetworkInterface e = n.nextElement();
				Enumeration<InetAddress> a = e.getInetAddresses();
				for (; a.hasMoreElements();) {
					InetAddress addr = a.nextElement();

					matcher = pattern.matcher(addr.getHostAddress());
					if (matcher.matches() && !addr.getHostAddress().equals("127.0.0.1"))
						return addr.getHostAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Returns hex string of the given bytes.
	 * 
	 * @param b
	 *            array of bytes
	 * @return hex string
	 */
	public static String byteArrayToHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	/**
	 * Returns the time.
	 * 
	 * @return the time
	 */
	public static String getTime() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		return sdf.format(cal.getTime()).toString();
	}

	/**
	 * Returns true if the given IP is given in IPv4, else false.
	 * 
	 * @param ip
	 *            the ip to test
	 * @return true if given IP is IPv4
	 */
	public static boolean isIPAddress(String ip) {
		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile("(\\d)+.(\\d)+.(\\d)+.(\\d)+");
		matcher = pattern.matcher(ip);

		if (ip != null)
			return matcher.matches();
		return false;
	}

	/**
	 * Returns true if the given port is correctly formated, else false.
	 * 
	 * @param port
	 *            the port to test
	 * @return true if port is correctly formated
	 */
	public static boolean isPort(String port) {
		Pattern pattern;
		Matcher matcher;

		pattern = Pattern.compile("(\\d)+");
		matcher = pattern.matcher(port);

		if (port != null)
			return matcher.matches();
		return false;
	}

	/**
	 * Returns given IP in protocol format.
	 * 
	 * @param ip
	 *            ip to format
	 * @return ip
	 */
	public static String toProtocoleIP(String ip) {
		String[] list = ip.split("\\.");
		String protocoleIP = "";
		for (int i = 0; i < list.length; i++) {
			String currentIP = list[i];
			int length = currentIP.length();
			for (int j = 0; j < 3 - length; j++)
				currentIP = 0 + currentIP;
			protocoleIP += "." + currentIP;
		}
		protocoleIP = protocoleIP.substring(1);
		return protocoleIP;
	}

	/**
	 * Returns formated APPL.
	 * 
	 * @param appl
	 *            to format
	 * @return formated APPL
	 */
	public static String toProtocoleAPPL(String appl) {
		String protocoleAPPL = appl;
		int length = 8 - appl.length();
		for (int i = 0; i < length; i++)
			protocoleAPPL += "#";
		return protocoleAPPL;
	}

	/**
	 * Returns formated SizeMess.
	 * 
	 * @param size
	 *            to format
	 * @return formated size
	 */
	public static String toProtocoleSizeMess(String size) {
		String protocoleSize = "";
		int length = 3 - size.length();
		for (int i = 0; i < length; i++) {
			protocoleSize += "0";
		}
		protocoleSize += size;
		return protocoleSize;
	}

	/**
	 * Returns maximum message size in bytes.
	 * 
	 * @return maximum message size
	 */
	public static int getMaximumMessageSize() {
		return MAXIMUM_MESSAGE_SIZE;
	}

}
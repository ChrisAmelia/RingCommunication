/**
 * Mailbox.
 */
public class Message {
	private String idm;
	private String author;
	private String content;
	private String time;

	/**
	 * Constructs a new object Message.
	 * 
	 * @param content
	 *            the content to set
	 */
	public Message(String content) {
		this.content = content;

		author = System.getProperty("user.name");
		time = Options.getTime();

		int salt = (int) (Math.random() * 10000);

		String superSalt = "";

		if (content != null) {
			int beginIndex = 0;
			int endIndex = (int) (Math.random() * content.length());
			superSalt = content.substring(beginIndex, endIndex);
		}

		String codedId = Options.byteArrayToHexString((time).getBytes())
				+ Options.byteArrayToHexString(superSalt.getBytes())
				+ Options.byteArrayToHexString((salt + "").getBytes());

		idm = codedId.substring(codedId.length() - 8);
	}

	/**
	 * Returns the idm.
	 * 
	 * @return the idm
	 */
	public String getIdm() {
		return idm;
	}

	/**
	 * Returns the author.
	 * 
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * Returns the content.
	 * 
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Returns the time.
	 * 
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Sets the idm.
	 * 
	 * @param idm
	 *            the id to set
	 */
	public void setId(String idm) {
		this.idm = idm;
	}

	/**
	 * Sets the author.
	 * 
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Sets the content.
	 * 
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time
	 *            the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

}

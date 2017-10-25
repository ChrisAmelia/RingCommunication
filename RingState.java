
/**
 * Inner and/or outer ring's informations.
 *
 */
public class RingState {
	private static boolean broken = false;

	private static int wantToLeave = 0;

	/**
	 * Returns true if inner or outer ring is broken.
	 * 
	 * @return the broken
	 */
	public static boolean isBroken() {
		return broken;
	}

	/**
	 * Returns the number of rings that are ready to disconnect.
	 * 
	 * @return the wantToLeave
	 */
	public static int getWantToLeave() {
		return wantToLeave;
	}

	/**
	 * Sets broken's state.
	 * 
	 * @param state
	 *            true or false
	 */
	public static void setBroken(boolean state) {
		RingState.broken = state;
	}

	/**
	 * Sets number of rings wanting to leave.
	 * 
	 * @param wantToLeave
	 *            the wantToLeave to set
	 */
	public static void setWantToLeave(int wantToLeave) {
		RingState.wantToLeave = wantToLeave;
	}

}

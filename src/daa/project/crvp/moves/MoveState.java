/**
 * MoveState.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 01-05-2018
 */
package daa.project.crvp.moves;

import daa.project.crvp.problem.CVRPClient;

/**
 * Class that represents the state of one move, meaning as state the clients
 * that are being moved that state.
 */
public class MoveState {

	private CVRPClient firstClient;
	private CVRPClient secondClient;

	public MoveState(CVRPClient firstClient, CVRPClient secondClient) {
		setFirstClient(firstClient);
		setSecondClient(secondClient);
	}

	/**
	 * @return the firstClient
	 */
	public CVRPClient getFirstClient() {
		return firstClient;
	}

	/**
	 * @return the secondClient
	 */
	public CVRPClient getSecondClient() {
		return secondClient;
	}

	/**
	 * @param firstClient
	 *          the firstClient to set
	 */
	public void setFirstClient(CVRPClient firstClient) {
		this.firstClient = firstClient;
	}

	/**
	 * @param secondClient
	 *          the secondClient to set
	 */
	public void setSecondClient(CVRPClient secondClient) {
		this.secondClient = secondClient;
	}

	/*
	 * (non-Javadoc) Compare that the two clients are the same, no matter the
	 * position.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object anotherObject) {		
		if (anotherObject instanceof MoveState) {
			MoveState anotherState = (MoveState) anotherObject;
			if (getFirstClient().equals(anotherState.getFirstClient())) {
				return getSecondClient().equals(anotherState.getSecondClient());
			}
			else if (getFirstClient().equals(anotherState.getSecondClient())) {
				return getSecondClient().equals(anotherState.getFirstClient());
			}
		}
		return false;
	}
}

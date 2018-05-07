/**
 * MoveStateTest.java
 *
 * @author Ángel Igareta (angel@igareta.com)
 * @version 1.0
 * @since 01-05-2018
 */
package daa.project.crvp.moves.test;

import static org.junit.Assert.*;

import org.junit.Test;

import daa.project.crvp.moves.MoveState;
import daa.project.crvp.problem.CVRPClient;

/**
 * [DESCRIPTION]
 */
public class MoveStateTest {

	/**
	 * Test method for {@link daa.project.crvp.moves.MoveState#equals(java.lang.Object)}.
	 */
	@Test
	public void testEquals() {
		CVRPClient firstClient = new CVRPClient(50, 50, 30);
		CVRPClient secondClient = new CVRPClient(30, 100, 30);
		
		MoveState firstState = new MoveState(firstClient, secondClient);
		MoveState secondState = new MoveState(secondClient, firstClient);
		assertEquals(firstState, secondState);
		
		firstState = new MoveState(firstClient, secondClient);
		secondState = new MoveState(firstClient, secondClient);		
		assertEquals(firstState, secondState);
	}
	
	@Test
	public void testNotEquals() {
		CVRPClient firstClient = new CVRPClient(50, 50, 30);
		CVRPClient secondClient = new CVRPClient(30, 100, 30);
		
		MoveState firstState = new MoveState(firstClient, firstClient);
		MoveState secondState = new MoveState(secondClient, firstClient);
		assertNotEquals(firstState, secondState);
	}

}

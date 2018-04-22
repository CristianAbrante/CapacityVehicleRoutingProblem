package daa.project.crvp.IO;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.experimental.theories.*;

@RunWith(Theories.class)
public class ReaderFromFileTest {
	
	ReaderFromFile reader;
	
	public void setFile(String fileName) throws FileNotFoundException, IOException {
		reader = new ReaderFromFile(fileName);
	}
	
	@Test
	public void depotsNumberIsCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/correctTest.vrp");
		assertEquals(reader.getDepots().size(), 1);
	}
	
	@Test
	public void numberOfClientsIsCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/correctTest.vrp");
		assertEquals(reader.getNumberOfClients(), 34);
	}
	
	@Test
	public void depotIDIsCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/correctTest.vrp");
		assertEquals(reader.getProblemSpecification().getDepotID(), 0);
	}
	
	@Test
	public void vehiclesCapacityIsCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/correctTest.vrp");
		assertEquals(reader.getProblemSpecification().getCapacity(), 100);
	}
	
	@Test
	public void getMinimunOfVehicles() throws FileNotFoundException, IOException {
		setFile("./input/tests/correctTest.vrp");
		assertEquals(reader.getProblemSpecification().getMinimunVehicles(), 5);

	}
	
	@Test(expected = IllegalArgumentException.class)
	public void vehiclesCapacityIsNotCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/badCapacity.vrp");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void vehiclesMinimumNamberIsNotCorrect() throws FileNotFoundException, IOException {
		setFile("./input/tests/badNumberOfVehicles.vrp");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void moreClientsThatDeclared() throws FileNotFoundException, IOException {
		setFile("./input/tests/moreClientsThatDeclared.vrp");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void moreDemandsThatDeclared() throws FileNotFoundException, IOException {
		setFile("./input/tests/moreDemandsThatDeclared.vrp");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void moreArgumentsThatDeclared() throws FileNotFoundException, IOException {
		setFile("./input/tests/moreArgumentsThatDeclared.vrp");
	}
	
	
}

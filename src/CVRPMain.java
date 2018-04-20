import java.io.FileNotFoundException;
import java.io.IOException;

import daa.project.crvp.IO.ReaderFromFile;

public class CVRPMain {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		ReaderFromFile reader = new ReaderFromFile("./prueba.vrp");
	}

}

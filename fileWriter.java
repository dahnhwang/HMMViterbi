import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class fileWriter {

	File file;
	String writeString;

	public void fileWrite(File file, String writeString) {

		this.file = file;
		this.writeString = writeString;

		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			pw.println(writeString);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

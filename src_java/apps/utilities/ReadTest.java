package apps.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReadTest {
	public static void main(String[] args) throws IOException {
		BufferedWriter bw;
//		File f = new File("lol");
//		if (!f.exists()) {
//			f.mkdir();
//			f = new File("lol" + "/scores");
//			f.createNewFile();
//		}
		bw = new BufferedWriter(new FileWriter("lol" + "/scores.txt", true));
		bw.newLine();
		bw.append("__\n");
//		bw.flush();
		bw.close();
	}
}

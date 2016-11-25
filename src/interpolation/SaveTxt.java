package interpolation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaveTxt {

	public static void SaveTxt01(String cont,String filePath) {

		FileOutputStream fop = null;
		File file;
//		String content = "This is the text content";
		String content = cont;

		try {

//			file = new File("c:/newfile.txt");
			file = new File(filePath);
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = content.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

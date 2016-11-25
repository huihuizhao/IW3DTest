package interpolation;
import java.io.FileWriter;
import java.io.IOException;

public class SaveArraytoTxt {
	public static void SaveArray01(String[][] strArray, String filePath) {
		FileWriter fw = null;
		try {
			// fw = new FileWriter("d://test.txt",true);
			fw = new FileWriter(filePath, true);
			int rowLength = strArray.length;// 行长度
			int colLength = strArray[0].length;// 列长度。
			
			for (int i = 0; i < rowLength; i++) {
				String towTemp = "";
				for (int j = 0; j < colLength; j++) {
					towTemp = towTemp + strArray[i][j].toString()+" ";
				}
				String c = towTemp + "\r\n";
				fw.write(c);
			}


			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			System.out.println("写入失败");
			System.exit(-1);
		}
	}

}

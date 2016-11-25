package interpolation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class ReadCoordinates {

	public static String[][] readTxtFile(String filePath) {
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int n = 0;// 行数
				bufferedReader.mark((int) file.length());
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!lineTxt.startsWith("*") && !lineTxt.startsWith("#")) {
						n++;
					}
				}

				String[][] Values = new String[n][5];
				read.close();

				read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				bufferedReader = new BufferedReader(read);
				int i = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!lineTxt.startsWith("*") && !lineTxt.startsWith("#")) {
						String[] lineValues = lineTxt.trim().split("\\s+");// 以多个空格分隔
						int vL = lineValues.length;
						for (int j = 0; j < vL; j++) {
							Values[i][j] = lineValues[j];

						}

						System.out.println(lineTxt);
						i++;

					}

				}
				read.close();
				return Values;

			} else {
				System.out.println("找不到指定的文件");
			}

		} catch (

		Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return null;

	}

}

package interpolation;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class GetValues extends iw3d {
	iw3d parent;

	public GetValues(Frame fr) {
		parent = (iw3d) fr;

		String[][] coordinates = ReadCoordinates.readTxtFile("D:\\FocusMap\\Data\\coordinates.txt");
		File dir = new File("D:\\FocusMap\\Data\\数据需求整理\\CTD后处理数据\\data\\");
		File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
		int dataLength = 0;// 目录下所有文件中行数总和，采样数据的总条数
		int fk = 0;// 文件数据量

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();

				if (fileName.endsWith("cnv")) { // 判断文件名是否以.cnv结尾
					String strFileName = files[i].getAbsolutePath();
					String[][] data = ReadTxtData.readTxtFile(strFileName);
					dataLength = dataLength + data.length;
				} else {
					continue;
				}
			}

		}

		// String[][] data =
		// ReadTxtData.readTxtFile("D:\\FocusMap\\Data\\数据需求整理\\CTD后处理数据\\data\\w1h.cnv");

		double[][] values = new double[dataLength][4];
		int vi = 0;
		if (files != null) {
			for (int fn = 0; fn < files.length; fn++) {
				String fileName = files[fn].getName();

				if (fileName.endsWith("cnv")) { // 判断文件名是否以.cnv结尾
					String strFileName = files[fn].getAbsolutePath();
					String[][] data = ReadTxtData.readTxtFile(strFileName);
					for (int di = 0; di < data.length; di++) {
						if (data[di][0] != null) {
							values[vi][0] = Double.valueOf(coordinates[fn][2]);// 经度
							values[vi][1] = Double.valueOf(coordinates[fn][3]);// 维度
							values[vi][2] = Double.valueOf(data[di][0]);// 深度
							values[vi][3] = Double.valueOf(data[di][1]);// 温度

							vi++;
						}

					}

				} else {
					continue;
				}
			}

		}

		String[][] tempStringArray = new String[values.length][4];
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < 4; j++) {
				tempStringArray[i][j] = Double.toString(values[i][j]);
			}
		}

		SaveArraytoTxt.SaveArray01(tempStringArray, "D://FocusMap//程序代码//IW3D_Java三维插值//outTemp.txt");
		parent.getReadData(dataLength - 1, 3, values); // !!!

	}
}

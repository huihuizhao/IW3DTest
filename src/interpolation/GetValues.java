package interpolation;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class GetValues extends iw3d {
	iw3d parent;

	public GetValues(Frame fr) {
		parent = (iw3d) fr;

//		String[][] coordinates = ReadCoordinates.readTxtFile("D:\\FocusMap\\Data\\coordinates.txt");
		String[][] coordinates = ReadCoordinates.readTxtFile("src/interpolation/Data/testdata/coordinates.txt");
		
		File dir = new File("src/interpolation/Data/CTD/");
		File[] files = dir.listFiles(); // ���ļ�Ŀ¼���ļ�ȫ����������
		int dataLength = 0;// Ŀ¼�������ļ��������ܺͣ��������ݵ�������
		int fk = 0;// �ļ�������

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();

				if (fileName.endsWith("cnv")) { // �ж��ļ����Ƿ���.cnv��β
					String strFileName = files[i].getAbsolutePath();
					String[][] data = ReadTxtData.readTxtFile(strFileName);
					dataLength = dataLength + data.length;
				} else {
					continue;
				}
			}

		}

		// String[][] data =
		// ReadTxtData.readTxtFile("D:\\FocusMap\\Data\\������������\\CTD��������\\data\\w1h.cnv");

		double[][] values = new double[dataLength][4];
		int vi = 0;
		if (files != null) {
			for (int fn = 0; fn < files.length; fn++) {
				String fileName = files[fn].getName();

				if (fileName.endsWith("cnv")) { // �ж��ļ����Ƿ���.cnv��β
					String strFileName = files[fn].getAbsolutePath();
					String[][] data = ReadTxtData.readTxtFile(strFileName);
					for (int di = 0; di < data.length; di++) {
						if (data[di][0] != null) {
//							//�������
//							values[vi][0] = Double.valueOf(coordinates[fn][2]);// ����
//							values[vi][1] = Double.valueOf(coordinates[fn][3]);// ά��
//							values[vi][2] = Double.valueOf(data[di][0]);// ���
//							values[vi][3] = Double.valueOf(data[di][1]);// �¶�
							
//							//�������棬x��z����
//							values[vi][2] = Double.valueOf(coordinates[fn][2]);// ����
//							values[vi][1] = Double.valueOf(coordinates[fn][3]);// ά��
//							values[vi][0] = Double.valueOf(data[di][0]);// ���
//							values[vi][3] = Double.valueOf(data[di][1]);// �¶�
							
							//ά�����棬x��z����
							values[vi][0] = Double.valueOf(coordinates[fn][2]);// ����
							values[vi][2] = Double.valueOf(coordinates[fn][3]);// ά��
							values[vi][1] = Double.valueOf(data[di][0]);// ���
							values[vi][3] = Double.valueOf(data[di][1]);// �¶�
							

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

//		SaveArraytoTxt.SaveArray01(tempStringArray, "D://FocusMap//�������//IW3D_Java��ά��ֵ//outTemp.txt");
		SaveArraytoTxt.SaveArray01(tempStringArray, "src/interpolation/Data/testdata/outTemp.txt");

		
//		//x��z���������㾭������Ĳ�ֵ�������Line50����ͬʱ�����ظ��ˣ�
//		for (int i = 0; i < values.length; i++) {
//	double temp=values[i][2];
//	values[i][2]=values[i][0];
//	values[i][0]=temp;
//			
//		}
		
		parent.getReadData(dataLength - 1, 3, values); 


	}
}

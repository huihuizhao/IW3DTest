package interpolation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class ReadTxtData {

	public static String[][] readTxtFile(String filePath) {
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // �ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// ���ǵ������ʽ
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				int n = 0;// ����
				bufferedReader.mark((int) file.length());
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (!lineTxt.startsWith("*") && !lineTxt.startsWith("#")) {
						for(int si=0;si<100;si++)//��ϡ����
						{
							bufferedReader.readLine();
						}
						n++;
					}
				}

				String[][] Values = new String[n][18];
				bufferedReader.reset();
				int i = 0;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					for(int si=0;si<100;si++)//��ϡ����
					{
						bufferedReader.readLine();
					}
 
					if (!lineTxt.startsWith("*") && !lineTxt.startsWith("#")) {
						String[] lineValues = lineTxt.trim().split("\\s+");// �Զ���ո�ָ�
						int vL = lineValues.length;
						for (int j = 0; j < vL; j++) {
							Values[i][j] = lineValues[j];

						}

//						System.out.println(lineTxt);
						i++;

					}

				}
				read.close();
				return Values;

			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}

		} catch (

		Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return null;

	}

}
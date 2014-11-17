import common.Config;

import java.io.*;

public class IOUtils {
	//	private static String path = System.getProperty("user.dir") + "\\res";
	private static String path = System.getProperty("user.dir");

	public static void save(int[][] arr, String fileName) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path + "/res/"
					+ fileName + System.currentTimeMillis()));
			DataOutputStream dos = new DataOutputStream(fos);
			String writeString = generateOutString(arr);
			dos.write(writeString.getBytes());
			dos.flush();
			fos.close();
			dos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void load(String name) {
		// mission1384572416281
		try {
			FileInputStream fis = new FileInputStream(new File(path + "\\"
					+ name));
			DataInputStream dis = new DataInputStream(fis);
			byte[] b = new byte[fis.available()];
			dis.readFully(b);
			String s = new String(b);
			decodeString(s);
			dis.close();
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String generateOutString(int[][] arr) {
		String s = "";
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr.length; j++) {
				s = s + arr[i][j] + " ";
			}
			s += "\r\n";
		}
		return s;
	}

	private static int[][] decodeString(String str) {
		String[] split = str.split("\r\n");
		int[][] arr = new int[2 * Config.mazeCount + 1][2 * Config.mazeCount + 1];
		for (int i = 0; i < split.length; i++) {
			String[] split2 = split[i].split(" ");
			for (int j = 0; j < split2.length; j++) {
				arr[i][j] = Integer.parseInt(split2[j]);
			}
		}
		System.out.println("——————————————————————————");
		MazeUtils.print(arr);
		return arr;
	}
}

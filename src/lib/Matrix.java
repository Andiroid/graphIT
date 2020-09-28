package lib;

import java.util.*;
import java.io.*;

public class Matrix {

	public Matrix() {
			//HashMap matrix = new HashMap();
			//String file = new File(".").getAbsolutePath();
			//System.out.println(file);
			try {
				BufferedReader in = new BufferedReader(new FileReader("src/csv/Graph.CSV"));

				String str;
				String[][] out = null;
				String[] tempArray = null;
				while ((str = in.readLine()) != null) {
					//out += str+"\n";
					tempArray = str.split(";");
				}
				System.out.println(Arrays.toString(tempArray));

				//String str;
				//String out = "";
				//while ((str = in.readLine()) != null) {
				//	out += str+"\n";
				//}


				//String str;
				//while ((str = in.readLine()) != null) {
				//	System.out.println(str);
				//}

				//System.out.println(out);
				//String[][] deepArray = new String[][] {{"John", "Mary"}, {"Alice", "Bob"}};
				//System.out.println(Arrays.toString(deepArray));
				//System.out.println(Arrays.toString(out));
				//System.out.println(out[0]);

				//String[] values = in.readLine().split(";");
				//System.out.println(values);

			} catch (IOException e) {
				System.out.println("error");
			}
	}
	public void readMatrixFile() {
		//HashMap matrix = new HashMap();

		
		try {
			BufferedReader in = new BufferedReader(new FileReader("file:///home/andiroid/Documents/Graph.CSV"));
			String str;

			while ((str = in.readLine()) != null) {
				System.out.println(str);
			}
			System.out.println(str);
		} catch (IOException e) {
		}
	}
	
}

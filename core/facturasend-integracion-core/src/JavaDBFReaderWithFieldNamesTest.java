import java.io.*;

import com.linuxense.javadbf.*;

public class JavaDBFReaderWithFieldNamesTest {


	public static void main(String args[]) {

		DBFReader reader = null;
		try {

			// create a DBFReader object
			reader = new DBFReader(new FileInputStream("F:\\tsje\\data\\regciv-db4.dbf"));

			// get the field count if you want for some reasons like the following

			int numberOfFields = reader.getFieldCount();

			// use this count to fetch all field information
			// if required

			for (int i = 0; i < numberOfFields; i++) {

				DBFField field = reader.getField(i);

				// do something with it if you want
				// refer the JavaDoc API reference for more details
				//
				System.out.println(field.getName());
			}

			// Now, lets us start reading the rows

			DBFRow row;

			while ((row = reader.nextRow()) != null) {
				System.out.println(row.getString("Nombre"));
			}

			// By now, we have iterated through all of the rows

		} catch (DBFException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			DBFUtils.close(reader);
		}
	}
}
import java.security.*;
import java.io.*;

public class ComputeSHA {
	public static void main(String args[]) {

		//String fileName = args[0];

		File f = new File(args[0]);
		FileInputStream fin = null;

		try {
			
			fin = new FileInputStream(f);

			byte buffer[] = new byte[(int)f.length()];

			fin.read(buffer);

			MessageDigest md = MessageDigest.getInstance("SHA1");

			md.update(buffer);

			byte[] output = md.digest();

			System.out.println(bytesToHex(output));

		}
		catch(FileNotFoundException ex) {
			System.out.println(
				"File Read Error");
		}
		catch(IOException ex) {
			System.out.println(
				"File Read Error");
		}
		catch (Exception e) {
			System.out.println("Exception: "+e);
		}
	}

	public static String bytesToHex(byte[] b) {
    	char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    	StringBuffer buf = new StringBuffer();
    		for (int j=0; j<b.length; j++) {
				buf.append(hexDigit[(b[j] >> 4) & 0x0f]);
				buf.append(hexDigit[b[j] & 0x0f]);
			}
      	return buf.toString();
   }
}
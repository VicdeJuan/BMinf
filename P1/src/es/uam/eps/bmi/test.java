import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class test {
    public static void main(String[] args) throws IOException{
    ZipFile zipFile = new ZipFile("r.zip");

	    Enumeration<? extends ZipEntry> entries = zipFile.entries();

		while(entries.hasMoreElements()){
			ZipEntry entry = entries.nextElement();

			InputStream stream = zipFile.getInputStream(entry);
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(stream));
			System.out.println(reader.readLine());

		}
	}
}

package client;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExtendedBufferedInputStreamTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
//	public void test1() throws IOException {
//		System.out.println("test1: ");
//		ExtendedBufferedReader ebr = new ExtendedBufferedReader(new FileReader("src/client/test.txt"));
//		String line;
//		while((line=ebr.readLine()) != null) {
//			System.out.println(line+", "+ebr.getBytesRead()+", "+ebr.getCRLFCounter());
//			System.out.println(ebr.getTotalBytes());
//			System.out.println();
//		}
//		System.out.println("end test1 \r\n");
//	}
	
//	@Test
//	public void test2() throws IOException {
//		System.out.println("test2: ");
//		ExtendedBufferedReader ebr = new ExtendedBufferedReader(new FileReader("src/client/test.txt"));
//		int ch;
//		while((ch=ebr.read()) != -1) {
//			System.out.println("char: "+ch+", "+(char)ch);
//			System.out.println(ebr.getBytesRead()+", "+ebr.getEOLCounter());
//			System.out.println(ebr.getTotalBytes());
//			System.out.println();
//		}
//		System.out.println("end test2 \r\n");
//	}
	
	@Test
	public void test3() throws IOException {
		System.out.println("test3: ");
		ExtendedBufferedInputStream ebr = new ExtendedBufferedInputStream(new FileInputStream("src/client/webPage.html"));
		long count = 0;
		int Clength = 0;
		boolean body = false;
		boolean documentFinished = false;
		boolean first = true;
		while (!documentFinished) {
			String line;
			if (body) {
				line = ebr.readLine(Clength);
			} else {
				line = ebr.readLine();
			}
			System.out.println("FROM SERVER: " + line);
			if (line.contains("Content-Length:")) {
				String[] ClengthAr = line.split("Content-Length: ");
				Clength = Integer.parseInt(ClengthAr[1])+ 2; //klein cheatorke
			}
			count = ebr.getTotalBytes();
			System.out.println("COUNT: "+ count);
			System.out.println("Clength: "+ Clength);
			if ((first) &&  (line.length() == 0)) {
				System.out.println("body start");
				ebr.resetTotalBytes();
				first = false;
				body = true;
			}
			if ((!first) && (count >= Clength)) {
				System.out.println("Document finished");
				documentFinished = true;
				body = false;
				System.out.println("body gedaan");
			}
		}
		System.out.println("bytes counted: "+ebr.getTotalBytes()+ ", "+ebr.getBytesRead()+", "+ebr.getCRLFCounter());
		ebr.close();
	}
	
//	@Test
//	public void test4() throws IOException {
//		System.out.println("test4: ");
//		ExtendedBufferedReader ebr = new ExtendedBufferedReader(new FileReader("src/client/webPage2.html"));
//		long count = 0;
//		int Clength = 19563;
//		int ch;
//		while ((ch = ebr.read()) != -1) {
//			count ++;
//			}
//		System.out.println("COUNT: "+ count);
//		System.out.println("Clength: "+ Clength);
//		System.out.println("bytes counted: "+ebr.getTotalBytes()+ ", "+ebr.getBytesRead()+", "+ebr.getEOLCounter());
//	}
	
//	@Test
//	public void test5() throws IOException {
//		System.out.println("test5: ");
//		ExtendedBufferedReader ebr1 = new ExtendedBufferedReader(new FileReader("src/client/webPage2.html"));
//		long count = 0;
//		int Clength = 19563;
//		int ch;
//		while ((ch = ebr1.read()) != -1) {
//			System.out.println((char) ch);
//			count ++;
//		}
//		ExtendedBufferedReader ebr2 = new ExtendedBufferedReader(new FileReader("src/client/webPage2.html"));
//		String line;
//		while ((line = ebr2.readLine()) != null) {
//			System.out.println(line);
//		}
//		System.out.println("COUNT: "+ count);
//		System.out.println("Clength: "+ Clength);
//		System.out.println("bytes counted: "+ebr2.getTotalBytes()+ ", "+ebr2.getBytesRead()+", "+ebr2.getCRLFCounter());
//	}

}

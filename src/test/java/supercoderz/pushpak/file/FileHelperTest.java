package supercoderz.pushpak.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import supercoderz.pushpak.bson.BSONHelper;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;

public class FileHelperTest {
	
	Message message;

	@Before
	public void setup(){
		message=MessageFactory.createEmptyMessage();
		message.put("test", new Double(1));
	}
	
	/**
	 * Test that BSON data can be written to a file, the file exists and then clean up
	 * @throws IOException
	 */
	@Test
	public void testWriteToFile() throws IOException {
		byte[] data = BSONHelper.toBson(message);
		FileHelper.write("test.msg",data);
		File file = new File("test.msg");
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
	
	/**
	 * Test that you can write data to a file and read back the data correctly
	 * @throws IOException
	 */
	@Test
	public void testReadFile() throws IOException{
		byte[] data = BSONHelper.toBson(message);
		FileHelper.write("test.msg",data);
		File file = new File("test.msg");
		assertTrue(file.exists());
		byte[] res = FileHelper.read("test.msg");
		assertEquals(data.length,res.length);
		file.delete();
		assertFalse(file.exists());
	}
	
	/**
	 * Test that when a non existent file is read, a empty byte array is returned
	 * @throws IOException
	 */
	@Test
	public void testReadNonExistantFile() throws IOException{
		byte[] res = FileHelper.read("non exist.msg");
		assertEquals(0,res.length);
	}
	
	@Test
	public void testGetFiles() throws IOException {
		byte[] data = BSONHelper.toBson(message);
		FileHelper.write("test.msg.1", data);
		FileHelper.write("test.msg.2", data);
		FileHelper.write("test.msg.3", data);
		FileHelper.write("test.msg.4", data);
		String[] files = FileHelper.getFiles("test.*", ".");
		assertTrue(files.length == 4);
		ArrayList<String> list = new ArrayList<String>();
		File file = new File("test.msg.1");
		file.delete();
		file = new File("test.msg.2");
		file.delete();
		file = new File("test.msg.3");
		file.delete();
		file = new File("test.msg.4");
		file.delete();
	}

}

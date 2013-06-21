package supercoderz.pushpak.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import supercoderz.pushpak.bson.BSONHelper;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.utils.LogUtils;

public class FileHelperPerfTest {
	
	@BeforeClass
	public static void setup(){
		File file= new File("data");
		file.mkdir();
	}
	
	@AfterClass
	public static void teardown(){
		File file= new File("data");
		file.delete();
	}

	@Test
	public void testWriting1000Files() throws IOException {
		int numFiles=1001;
		internalTestFileWrite(numFiles);
	}

	@Test
	public void testWriting10000Files() throws IOException {
		int numFiles=10001;
		internalTestFileWrite(numFiles);
	}

	private void internalTestFileWrite(int numFiles) throws IOException {
		//create a large MapMessage
		Message message = MessageFactory.createEmptyMessage();
		for(int i=0;i<500;i++){
			message.put(String.valueOf(i), i);
		}
		//serialize it
		byte[] data=BSONHelper.toBson(message);
		LogUtils.info("FileHelperTest","Converted test message to data of size "+data.length);
		
		LogUtils.info("FileHelperTest","Starting to write files");
		//now do the writing
		long epoch=System.nanoTime();
		for(int i=1;i<numFiles;i++){
			FileHelper.write("data/"+String.valueOf(i), data);
		}
		long writing_complete=System.nanoTime()-epoch;
		LogUtils.info("FileHelperTest","Completed writing "+numFiles+" files in (milli secs) "+writing_complete/1000000);
		
		//now do the checking of all the files
		for(int i=1;i<numFiles;i++){
			File file=new File("./data/"+String.valueOf(i));
			assertTrue(file.exists());
			file.delete();
			assertFalse(file.exists());
		}
	}

}

package supercoderz.pushpak.file;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import supercoderz.pushpak.bson.BSONHelper;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.utils.LogUtils;

public class DataEncodeSerializeDeSerializeDecodeTest {

	@Test
	public void test1000Files() throws IOException {
		internalEcodeSerializeDeSerializeDecode(1001);
	}

	@Test
	public void test10000Files() throws IOException {
		internalEcodeSerializeDeSerializeDecode(10001);
	}

	private void internalEcodeSerializeDeSerializeDecode(int numFiles)
			throws IOException {
		File dir = new File("data");
		dir.mkdir();
		Message message = MessageFactory.createEmptyMessage();
		for (int i = 0; i < 500; i++) {
			message.put(String.valueOf(i), i);
		}

		long epoch = System.nanoTime();

		for (int i = 1; i < numFiles; i++) {
			byte[] data = BSONHelper.toBson(message);
			FileHelper.write("./data/" + String.valueOf(i), data);
		}

		for (int i = 1; i < numFiles; i++) {
			byte[] data = FileHelper.read("./data/" + String.valueOf(i));
			Message msg = BSONHelper.toMapMessage(data);
			assertTrue(msg.equals(message));
		}
		LogUtils.info("DataEncodeSerializeDeSerializeDecodeTest",
				"Test completed in (milli secs)" + (System.nanoTime() - epoch)/1000000);

		// now do the clean up of the files
		for (int i = 1; i < numFiles; i++) {
			File file = new File("./data/" + String.valueOf(i));
			file.delete();
		}
	}

}

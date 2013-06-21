package supercoderz.pushpak.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.utils.Constants;

public class FileBackedServerTest {

	FileBackedServer server;

	@Before
	public void setup() {
		server = new FileBackedServer("data", 10);
	}

	@Test
	public void testAcceptRetrieve() throws ServerException,
			InterruptedException {
		Message message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		server.accept(message);
		Message msg = server.retrieve("test.message");
		assertTrue(message.equals(msg));
		Thread.sleep(2000);
		(new File("./data/test.message.bson")).delete();
	}

	@Test
	public void testAcceptRetrieveWithOverflow() throws ServerException,
			InterruptedException {
		for (int i = 0; i < 20; i++) {
			Message message = MessageFactory.createEmptyMessage();
			message.put(Constants.MESSAGE_SUBJECT, String.valueOf(i));
			server.accept(message);
		}

		// wait for files to be written
		Thread.sleep(3000);

		for (int i = 0; i < 20; i++) {
			Message msg = server.retrieve(String.valueOf(i));
			assertNotNull(msg);
		}

		for (int i = 0; i < 20; i++) {
			(new File("./data/" + String.valueOf(i) + ".bson")).delete();
		}
	}

	@Test
	public void testAcceptRetrieveMultiple() throws ServerException,
			InterruptedException {
		for (int i = 0; i < 20; i++) {
			Message message = MessageFactory.createEmptyMessage();
			message.put(Constants.MESSAGE_SUBJECT, "test." + String.valueOf(i));
			server.accept(message);
		}

		// wait for files to be written
		Thread.sleep(6000);

		List<Message> messages = server.retrieveMessages("test.*");
		assertTrue(messages.size() == 20);

		for (int i = 0; i < 20; i++) {
			(new File("./data/" + "test." + String.valueOf(i) + ".bson"))
					.delete();
		}
	}

	@Test
	public void testAcceptRetrieveWithFlush() throws ServerException,
			InterruptedException {
		for (int i = 0; i < 10; i++) {
			Message message = MessageFactory.createEmptyMessage();
			message.put(Constants.MESSAGE_SUBJECT, String.valueOf(i));
			server.accept(message);
		}

		// stop the server
		server.shutdown();
		// and recreate it
		server = new FileBackedServer("data", 10);

		// wait for files to be written
		Thread.sleep(3000);

		for (int i = 0; i < 10; i++) {
			Message msg = server.retrieve(String.valueOf(i));
			assertNotNull(msg);
		}

		for (int i = 0; i < 20; i++) {
			(new File("./data/" + String.valueOf(i) + ".bson")).delete();
		}
	}

	@Test(expected = ServerException.class)
	public void testCannotAcceptInShutdown() throws ServerException {
		Message message = MessageFactory.createEmptyMessage();
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		server.shutdown();
		server.accept(message);
	}

	@Test(expected = ServerException.class)
	public void testCannotRetrieveInShutdown() throws ServerException {
		server.shutdown();
		server.retrieve("test.message");
	}

}

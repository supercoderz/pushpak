package supercoderz.pushpak.file;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.persistence.PersistenceException;
import supercoderz.pushpak.utils.Constants;

public class FilePersistenceManagerTest {
	
	FilePersistenceManager manager = new FilePersistenceManager("./data");

	@Test
	public void testPersistToFile() throws PersistenceException, InterruptedException {
		Message message=MessageFactory.createEmptyMessage();
		message.put("test", 1);
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		manager.persist(message);
		Thread.sleep(2000);
		File file = new File("data/test.message.bson");
		assertTrue(file.exists());
		file.delete();
		assertFalse(file.exists());
	}
	
	@Test
	public void testReadPersistedFile() throws PersistenceException, InterruptedException {
		Message message=MessageFactory.createEmptyMessage();
		message.put("test", 1);
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		manager.persist(message);
		Thread.sleep(2000);
		Message msg = manager.getMessage("test.message");
		assertTrue(msg.equals(message));
		File file = new File("data/test.message.bson");
		file.delete();
		assertFalse(file.exists());
	}

	@Test(expected=PersistenceException.class)
	public void testPersistEmptyFile() throws PersistenceException, InterruptedException {
		Message message=MessageFactory.createEmptyMessage();
		manager.persist(message);
	}
	
	@Test(expected=PersistenceException.class)
	public void testReadInavlidSubject() throws PersistenceException, InterruptedException {
		manager.getMessage("invalid");
	}

	@Test
	public void testGetMessages() throws PersistenceException,
			InterruptedException {
		Message message = MessageFactory.createEmptyMessage();
		message.put("test", 1);
		message.put(Constants.MESSAGE_SUBJECT, "test.message");
		manager.persist(message);
		Message message1 = MessageFactory.createEmptyMessage();
		message1.put("test", 1);
		message1.put(Constants.MESSAGE_SUBJECT, "test.message1");
		manager.persist(message1);
		Thread.sleep(2000);
		List<Message> messages = manager.getMessages("test.*");
		assertTrue(messages.size() == 2);
		File file = new File("data/test.message.bson");
		file.delete();
		file = new File("data/test.message1.bson");
		file.delete();
	}

}

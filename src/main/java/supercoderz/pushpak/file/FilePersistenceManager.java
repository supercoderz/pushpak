package supercoderz.pushpak.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import supercoderz.pushpak.bson.BSONHelper;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.persistence.IPersistenceManager;
import supercoderz.pushpak.persistence.PersistenceException;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public class FilePersistenceManager implements IPersistenceManager {

	LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
	String baseDir;
	boolean shutdown = false;

	/**
	 * Create File persistence manager in the given base directory
	 * 
	 * @param baseDir
	 *            the directory where the files should be written
	 */
	public FilePersistenceManager(String baseDir) {
		this.baseDir = baseDir;
		// create the directory
		File file = new File(baseDir);
		file.mkdir();
		// start polling for messages to be written to files on the disk
		new Thread(new FileWriterThread()).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.persistence.IPersistenceManager#persist(supercoderz
	 * .pushpak.message.Message)
	 */
	public void persist(Message message) throws PersistenceException {
		// queue this to be written to the disk by the thread
		String subject = (String) message.get(Constants.MESSAGE_SUBJECT);
		if (!message.containsKey(Constants.MESSAGE_SUBJECT) || subject == null
				|| subject.isEmpty()) {
			throw new PersistenceException("Invalid message subject " + subject);
		}
		queue.offer(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.persistence.IPersistenceManager#getMessage(java.lang
	 * .String)
	 */
	public Message getMessage(String subject) throws PersistenceException {
		try {
			LogUtils.debug("FileWriterThread",
					"Processing read from file for Message Subject " + subject);
			String filename = baseDir + File.separator + subject
					+ Constants.FILE_EXT;
			File file = new File(filename);
			if (!file.exists()) {
				throw new PersistenceException(
						"File does not exist for subject " + subject);
			}
			LogUtils.debug("FileWriterThread",
					"Created filename for message as " + filename);
			return BSONHelper.toMapMessage(FileHelper.read(filename));
		} catch (IOException e) {
			LogUtils.error("FilePersistenceManager",
					"Error reading file for subject " + subject + ". Error: "
							+ e.getMessage());
			throw new PersistenceException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.persistence.IPersistenceManager#shutdown()
	 */
	public void shutdown() {
		shutdown = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.persistence.IPersistenceManager#getMessages(java.
	 * lang.String)
	 */
	public List<Message> getMessages(String subjectRegex)
			throws PersistenceException {
		try {
			List<Message> messages = new ArrayList<Message>();
			String[] files = FileHelper.getFiles(subjectRegex, baseDir);
			LogUtils.debug("FilePersistenceManager",
					"Reading messages from files " + files);
			for (String filename : files) {
				messages.add(BSONHelper.toMapMessage(FileHelper.read(baseDir
						+ "/" + filename)));
			}
			return messages;
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Helper class to persist the messages to files in a separate thread
	 * 
	 */
	class FileWriterThread implements Runnable {
		public void run() {
			while (!shutdown) {
				Message message;
				try {
					message = queue.take();
					LogUtils.debug("FileWriterThread",
							"Received message for writing to disk");
					String filename = (String) message
							.get(Constants.MESSAGE_SUBJECT);
					LogUtils.debug("FileWriterThread", "Message Subject "
							+ filename);
					filename = baseDir + File.separator + filename
							+ Constants.FILE_EXT;
					LogUtils.debug("FileWriterThread",
							"Created filename for message as " + filename);
					FileHelper.write(filename, BSONHelper.toBson(message));
					LogUtils.debug("FileWriterThread",
							"Completed writing file to disk");
				} catch (InterruptedException e) {
					LogUtils.error(
							"FilePersistenceManager",
							"Error writing message as file to disk "
									+ e.getMessage());
				} catch (IOException e) {
					LogUtils.error(
							"FilePersistenceManager",
							"Error writing message as file to disk "
									+ e.getMessage());
				}
			}
		}
	}

}

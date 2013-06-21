package supercoderz.pushpak.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import supercoderz.pushpak.cache.CacheFactory;
import supercoderz.pushpak.cache.ICache;
import supercoderz.pushpak.file.FilePersistenceManager;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.persistence.IPersistenceManager;
import supercoderz.pushpak.persistence.PersistenceException;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public class FileBackedServer implements IServerHandler {

	IPersistenceManager fileManager;
	ICache cache;
	List<IListener> listeners = new ArrayList<IListener>();
	boolean shutdownInProgress = false;

	public FileBackedServer(String baseDir, int maxCacheEntries) {
		// create the file manager and the cache of given size
		LogUtils.info("FileBackedServer",
				"Initializing FileBackedServer in directory " + baseDir
						+ " with cache size " + maxCacheEntries);
		fileManager = new FilePersistenceManager(baseDir);
		LogUtils.debug("FileBackedServer",
				"Completed initializing FilePersistenceManager");
		cache = CacheFactory.createLRUCache(maxCacheEntries);
		LogUtils.debug("FileBackedServer",
				"Completed initializing Cache with size " + maxCacheEntries);
		LogUtils.info("FileBackedServer",
				"Completed initializing FileBackedServer in directory "
						+ baseDir + " with cache size " + maxCacheEntries);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.server.IServerHandler#accept(supercoderz.pushpak.
	 * message.Message)
	 */
	public void accept(Message message) throws ServerException {
		if (!shutdownInProgress) {
			try {
				String subject = (String) message
						.get(Constants.MESSAGE_SUBJECT);
				LogUtils.debug("FileBackedServer",
						"Received new message on subject " + subject + "\n"
								+ message);
				cache.put(subject, message);
				LogUtils.debug("FileBackedServer",
						"Successfully cached message on subject " + subject);
				fileManager.persist(message);
				LogUtils.debug("FileBackedServer",
						"Successfully queued message for writing to file");
				for (IListener listener : listeners) {
					listener.onMessage(message);
				}
				LogUtils.debug("FileBackedServer",
						"Successfully sent message to all listeners");

			} catch (PersistenceException e) {
				throw new ServerException(e);
			}
		} else {
			throw new ServerException(
					"Shutdown in progress - cannot accept messages");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.server.IServerHandler#retrieve(java.lang.String)
	 */
	public Message retrieve(String subject) throws ServerException {
		if (!shutdownInProgress) {
			try {
				LogUtils.debug("FileBackedServer",
						"Checking message in cache for subject " + subject);
				if (cache.contains(subject)) {
					LogUtils.debug("FileBackedServer",
							"Returning cached message for subject " + subject);
					// if its cached then send it outright
					return cache.get(subject);
				} else {
					LogUtils.debug("FileBackedServer",
							"Trying to retrieve file for for subject "
									+ subject);
					Message message = fileManager.getMessage(subject);
					if (message.isEmpty()) {
						LogUtils.debug("FileBackedServer",
								"No message found, returning empty message for subject "
										+ subject);
						return message;

					} else {
						LogUtils.debug("FileBackedServer",
								"Returning retrieved message for subject "
										+ subject);
						return message;
					}
				}
			} catch (PersistenceException e) {
				throw new ServerException(e);
			}
		} else {
			throw new ServerException(
					"Shutdown in progress - cannot retrieve messages");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.server.IServerHandler#shutdown()
	 */
	public void shutdown() throws ServerException {
		try {
			LogUtils.info("FileBackedServer",
					"Shutdown initiated for FileBackedServer");
			shutdownInProgress = true;
			// wait for a while to ensure everything is closed
			LogUtils.debug("FileBackedServer",
					"Flushing all cached messages to disk");
			Iterator<Message> values = cache.values();
			while (values.hasNext()) {
				fileManager.persist(values.next());
			}
			LogUtils.debug("FileBackedServer",
					"Waiting 30 seconds for messages to be processed in file manager");
			Thread.sleep(30000);
			LogUtils.debug("FileBackedServer", "Flushing all messages in cache");
			cache.flush();
			LogUtils.debug("FileBackedServer",
					"Stopping file persistence manager");
			fileManager.shutdown();
			LogUtils.info("FileBackedServer",
					"Shutdown completed for FileBackedServer");
		} catch (Exception e) {
			throw new ServerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.server.IServerHandler#registerListener(supercoderz
	 * .pushpak.server.IListener)
	 */
	public void registerListener(IListener listener) {
		LogUtils.info(
				"FileBackedServer",
				"Registering listener for subject pattern "
						+ listener.getSubjectPattern());
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.server.IServerHandler#retrieveMessages(java.lang.
	 * String)
	 */

	public List<Message> retrieveMessages(String subjectRegex)
			throws ServerException {
		try {
			if (!shutdownInProgress) {
				LogUtils.debug("FileBackedServer",
						"Trying to retrieve files for for subject pattern "
								+ subjectRegex);

				return fileManager.getMessages(subjectRegex);
			} else {
				return Collections.emptyList();
			}
		} catch (PersistenceException e) {
			throw new ServerException(e);
		}
	}

}

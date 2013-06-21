package supercoderz.pushpak.cache;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public class LRUCachePerftest {

	@Test
	public void test1000() {
		internalCacheTest(1001);
	}

	@Test
	public void test10000() {
		internalCacheTest(10001);
	}

	private void internalCacheTest(int maxEntries) {
		ICache cache = CacheFactory.createLRUCache(maxEntries);
		Map<String, Message> messages = new HashMap<String, Message>();
		Message message = MessageFactory.createEmptyMessage();
		for (int i = 0; i < maxEntries; i++) {
			message.put(Constants.MESSAGE_SUBJECT, String.valueOf(i));
			messages.put(String.valueOf(i), message);
			cache.put(String.valueOf(i), message);
		}
		LogUtils.info("LRUCachePerfTest",
				"Starting cache test by filling to capacity");
		long epoch = System.nanoTime();
		for (int i = 1; i < maxEntries; i++) {
			cache.put(String.valueOf(i), message);
		}
		LogUtils.info("LRUCachePerfTest",
				"Populated cache with to capacity in (milli secs) "
						+ (System.nanoTime() - epoch) / 1000000);
		for (int i = 1; i < maxEntries; i++) {
			epoch = System.nanoTime();
			Message msg = cache.get(String.valueOf(i));
			LogUtils.info(
					"LRUCachePerfTest",
					"Retrieved from cache in (milli secs) "
							+ (System.nanoTime() - epoch) / 1000000);
			assertTrue(msg.equals(messages.get(String.valueOf(i))));
		}
		cache.flush();
	}

}

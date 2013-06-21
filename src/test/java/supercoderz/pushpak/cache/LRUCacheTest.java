package supercoderz.pushpak.cache;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;

public class LRUCacheTest {
	ICache cache;
	
	@Before
	public void setup(){
		cache=CacheFactory.createLRUCache(10);
	}
	
	@After
	public void teardown(){
		cache.flush();
	}

	/**
	 * Test that a value can be cached and then retrieved
	 */
	@Test
	public void testBasicCacheAndGetAndFlush() {
		Message message = MessageFactory.createEmptyMessage();
		message.put("test", "test.subject");
		assertFalse(cache.contains("test.subject"));
		cache.put("test.subject", message);
		assertTrue(cache.contains("test.subject"));
		assertNotNull(cache.get("test.subject"));
		cache.flush();
		assertFalse(cache.contains("test.subject"));
	}
	
	/**
	 * Test that if you put n+1 elements in a 'n' size cache then 0th element is deleted
	 */
	@Test
	public void testBasicOverflow() {
		Message message = MessageFactory.createEmptyMessage();
		message.put("test", "test.subject");
		for(int i=0;i<11;i++){
			cache.put(String.valueOf(i), message);
		}
		assertFalse(cache.contains("0"));
		assertTrue(cache.contains("10"));
		assertTrue(cache.contains("9"));
		assertTrue(cache.contains("8"));
		assertTrue(cache.contains("7"));
		assertTrue(cache.contains("6"));
		assertTrue(cache.contains("5"));
		assertTrue(cache.contains("4"));
		assertTrue(cache.contains("3"));
		assertTrue(cache.contains("2"));
		assertTrue(cache.contains("1"));		
	}

}

package supercoderz.pushpak.cache;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.utils.LogUtils;

public class LRUCache implements ICache {
	
	//this is the map that backs the cache
	Map<String,Message> map;
	
	LRUCache(int maxEntries){
		map= Collections.synchronizedMap(new LRUMap(maxEntries));
		LogUtils.debug("LRUCache", "Created cache backed by LRU map of size "
				+ maxEntries);
	}
	
	/* (non-Javadoc)
	 * @see supercoderz.pushpak.cache.ICache#get(java.lang.String)
	 */
	public Message get(String subject){
		return map.get(subject);
	}
	
	/* (non-Javadoc)
	 * @see supercoderz.pushpak.cache.ICache#contains(java.lang.String)
	 */
	public boolean contains(String subject){
		return map.containsKey(subject);
	}
	
	/* (non-Javadoc)
	 * @see supercoderz.pushpak.cache.ICache#put(java.lang.String, supercoderz.pushpak.message.Message)
	 */
	public void put(String subject,Message message){
		map.put(subject, message);
	}

	/*
	 * (non-Javadoc)
	 * @see supercoderz.pushpak.cache.ICache#flush()
	 */
	public void flush() {
		map.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.cache.ICache#values()
	 */
	public Iterator<Message> values() {
		return map.values().iterator();
	}

}

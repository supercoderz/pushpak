package supercoderz.pushpak.cache;

public class CacheFactory {
	
	/**
	 * Create a LRU cache of the given size
	 * @param maxEntries the cache size
	 * @return an instance of the LRU cache
	 */
	public static ICache createLRUCache(int maxEntries){
		return new LRUCache(maxEntries);
	}

}

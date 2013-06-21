package supercoderz.pushpak.cache;

import java.util.LinkedHashMap;

import supercoderz.pushpak.message.Message;

public class LRUMap extends LinkedHashMap<String, Message> {

	private static final long serialVersionUID = -1363788491591800835L;
	
	int maxEntries;
	
	public LRUMap(int maxEntries){
		super(maxEntries);
		this.maxEntries=maxEntries;
	}
	
	@Override
	protected boolean removeEldestEntry(
			java.util.Map.Entry<String, Message> eldest) {
		return size()>maxEntries;
	}

}

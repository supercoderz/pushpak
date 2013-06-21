package supercoderz.pushpak.bson;

import org.bson.BSON;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;


public class BSONHelper {

	/**
	 * Helper method to encode a message as a BSON byte array
	 * @param message the message to be encoded
	 * @return the byte array representing the message in BSON format
	 */
	public static byte[] toBson(Message message) {
		if(message==null){
			return new byte[0];
		}
		BSONObject b = new BasicBSONObject(message);
		return BSON.encode(b);
	}

	/**
	 * Helper method to decode a BSON byte array back into a message
	 * @param data the byte array with the BSON data
	 * @return the message representation of the data
	 */
	@SuppressWarnings("unchecked")
	public static Message toMapMessage(byte[] data) {
		if(data.length==0){
			return MessageFactory.createEmptyMessage();
		}
		BasicBSONObject b = (BasicBSONObject) BSON.decode(data);
		return MessageFactory.createMessage(b.toMap());
	}
	
}

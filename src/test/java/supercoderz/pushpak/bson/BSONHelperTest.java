package supercoderz.pushpak.bson;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;


import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;

public class BSONHelperTest {
	
	Message message;
	
	@Before
	public void setup(){
		message = MessageFactory.createEmptyMessage();
		message.put("one", new Double(1));
		message.put("one.one", new Double(1.1));
	}

	/**
	 * Test that a non null byte array is returned when data is encoded
	 */
	@Test
	public void testEncodeReturnsByteArray(){
		byte[] res=BSONHelper.toBson(message);
		assertNotNull(res);
		assertTrue(res.length>0);
	}
	
	/**
	 * Test that the data is recovered properly when encoding and decoding back
	 */
	@Test
	public void testEncodeDecode(){
		byte[] res=BSONHelper.toBson(message);
		Message msg = BSONHelper.toMapMessage(res);
		assertTrue(msg.equals(message));
	}
	
	/**
	 * Test that the result of encoding null is an empty array
	 */
	@Test
	public void testEncodeNull(){
		assertTrue(BSONHelper.toBson(null).length==0);
	}
	
	/**
	 * Test that the result of decoding a empty array is a blank MapMessage
	 */
	@Test
	public void testDecodeEmptyArray(){
		assertTrue(BSONHelper.toMapMessage(new byte[0]).size()==0);
	}

}

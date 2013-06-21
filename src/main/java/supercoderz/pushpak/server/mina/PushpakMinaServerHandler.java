package supercoderz.pushpak.server.mina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import supercoderz.pushpak.message.BulkMessageRetrieveRequest;
import supercoderz.pushpak.message.BulkMessageRetrieveResponse;
import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.message.PublishRequest;
import supercoderz.pushpak.message.Request;
import supercoderz.pushpak.message.Response;
import supercoderz.pushpak.message.RetrieveRequest;
import supercoderz.pushpak.server.FileBackedServer;
import supercoderz.pushpak.utils.LogUtils;

public class PushpakMinaServerHandler extends IoHandlerAdapter {

	FileBackedServer server;

	public PushpakMinaServerHandler(String baseDir, int maxCacheEntries) {
		server = new FileBackedServer(baseDir, maxCacheEntries);
	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		super.exceptionCaught(session, cause);
		LogUtils.error("PushpakServerHandler",
				"Error in server " + cause.getMessage());
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof Request) {
			String subjectRegex = ((Request) message).getSubectRegex();
			LogUtils.info("PushpakServerHandler",
					"Received subcription request on " + subjectRegex);
			server.registerListener(new MinaListener(session, subjectRegex));
		} else if (message instanceof PublishRequest) {
			PublishRequest request = ((PublishRequest) message);
			Message msg = request.getMessage();
			String subject = request.getSubject();
			LogUtils.info("PushpakServerHandler",
					"Received published message on " + subject);
			server.accept(msg);
		} else if (message instanceof RetrieveRequest) {
			RetrieveRequest request = ((RetrieveRequest) message);
			String subject = request.getSubject();
			LogUtils.info("PushpakServerHandler",
					"Received request to retrieve message on " + subject);
			Response response = MessageFactory.createResponse(subject,
					server.retrieve(subject));
			session.write(response).await();
		} else if (message instanceof BulkMessageRetrieveRequest) {
			BulkMessageRetrieveRequest request = ((BulkMessageRetrieveRequest) message);
			String subjectRegex = request.getSubjectRegex();
			LogUtils.info("PushpakServerHandler",
					"Received request to retrieve all messages on "
							+ subjectRegex);
			BulkMessageRetrieveResponse response = MessageFactory
					.createBulkMessageRetrieveResponse(subjectRegex,
							server.retrieveMessages(subjectRegex));
			session.write(response);
		}

	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		server.shutdown();
		super.sessionClosed(session);
	}

}

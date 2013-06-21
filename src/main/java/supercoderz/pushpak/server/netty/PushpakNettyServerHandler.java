package supercoderz.pushpak.server.netty;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

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

public class PushpakNettyServerHandler extends SimpleChannelUpstreamHandler {

	FileBackedServer server;

	public PushpakNettyServerHandler(String baseDir, int maxCacheEntries) {
		server = new FileBackedServer(baseDir, maxCacheEntries);
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		if (e.getMessage() instanceof Request) {
			String subjectRegex = ((Request) e.getMessage()).getSubectRegex();
			LogUtils.info("PushpakServerHandler",
					"Received subcription request on " + subjectRegex);
			server.registerListener(new NettyListener(ctx, subjectRegex));
		} else if (e.getMessage() instanceof PublishRequest) {
			PublishRequest request = ((PublishRequest) e.getMessage());
			Message message = request.getMessage();
			String subject = request.getSubject();
			LogUtils.info("PushpakServerHandler",
					"Received published message on " + subject);
			server.accept(message);
		} else if (e.getMessage() instanceof RetrieveRequest) {
			RetrieveRequest request = ((RetrieveRequest) e.getMessage());
			String subject = request.getSubject();
			LogUtils.info("PushpakServerHandler",
					"Received request to retrieve message on " + subject);
			Response response = MessageFactory.createResponse(subject, server.retrieve(subject));
			ctx.getChannel().write(response).await();
		}else if (e.getMessage() instanceof BulkMessageRetrieveRequest) {
			BulkMessageRetrieveRequest request = ((BulkMessageRetrieveRequest) e.getMessage());
			String subjectRegex = request.getSubjectRegex();
			LogUtils.info("PushpakServerHandler",
					"Received request to retrieve all messages on " + subjectRegex);
			BulkMessageRetrieveResponse response = MessageFactory
					.createBulkMessageRetrieveResponse(subjectRegex,
							server.retrieveMessages(subjectRegex));
			ctx.getChannel().write(response).await();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		super.exceptionCaught(ctx, e);
		LogUtils.error("PushpakServerHandler", "Error in server "
				+ e.getCause().getMessage());
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		server.shutdown();
		super.channelClosed(ctx, e);
	}

}

package supercoderz.pushpak.server.netty;

import java.util.regex.Pattern;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;

import supercoderz.pushpak.message.Message;
import supercoderz.pushpak.message.MessageFactory;
import supercoderz.pushpak.server.IListener;
import supercoderz.pushpak.utils.Constants;
import supercoderz.pushpak.utils.LogUtils;

public class NettyListener implements IListener {

	ChannelHandlerContext context;
	String subjectRegex;
	Pattern pattern;

	public NettyListener(ChannelHandlerContext context, String subjectRegex) {
		this.context = context;
		this.subjectRegex = subjectRegex;
		pattern = Pattern.compile(subjectRegex);
		LogUtils.info("NettyListener", "Created listener for subject pattern "
				+ subjectRegex + " and channel context " + context.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * supercoderz.pushpak.server.IListener#onMessage(supercoderz.pushpak.message
	 * .Message)
	 */
	public void onMessage(Message message) {
		String subject = (String) message.get(Constants.MESSAGE_SUBJECT);
		if (pattern.matcher(subject).matches()) {
			LogUtils.info("NettyListener",
					"Forwarding new message on subject pattern " + subjectRegex
							+ " and channel context " + context.getName());
			// write the message back to the requester using the context
			Channels.write(context,
					Channels.succeededFuture(context.getChannel()),
					MessageFactory.createResponse(subjectRegex, message));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see supercoderz.pushpak.server.IListener#getSubjectPattern()
	 */
	public String getSubjectPattern() {
		return subjectRegex;
	}

}

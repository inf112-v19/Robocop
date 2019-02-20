package inf112.skeleton.app.Socket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatLoginInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel arg0) throws Exception {
        //how we want to organize our communication described here
        ChannelPipeline pipeline = arg0.pipeline();

        //no more than 8192 frames
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        pipeline.addLast("handler", new ChatLoginHandler());
    }

}

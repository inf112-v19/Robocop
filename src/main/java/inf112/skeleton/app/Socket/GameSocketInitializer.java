package inf112.skeleton.app.Socket;

import inf112.skeleton.app.RoboRally;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GameSocketInitializer extends ChannelInitializer<SocketChannel> {
    private RoboRally game;

    public GameSocketInitializer(RoboRally game){
        this.game = game;
    }

    /**
     * Set up Channel pipeline
     * @param arg0
     */
    @Override
    protected void initChannel(SocketChannel arg0) {
        ChannelPipeline pipeline = arg0.pipeline();

        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        pipeline.addLast("handler", new GameSocketHandler(game));
    }

}

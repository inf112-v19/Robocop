package inf112.skeleton.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    public final int port;


    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * Server is binded to the and all the handlers are set.
             */
            ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class).childHandler(new ChatServerInitializer());
            System.out.println("Binding to port "+port+"...");
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

package inf112.skeleton.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.graphics.GL20;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.awt.*;

import static org.mockito.Mockito.mock;

public class Server {
    public static final int port = 58008;
    public static GameServerInstance game = new GameServerInstance();

    public static void run() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    MockGraphics mockGraphics = new MockGraphics();
                    Gdx.graphics = mockGraphics;
                    Gdx.gl = mock(GL20.class);
                    HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
                    new HeadlessApplication(game, config);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * Server is binded to the and all the handlers are set.
             */
            ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class).childHandler(new ChatServerInitializer());
            System.out.println("Binding to port " + port + "...");
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
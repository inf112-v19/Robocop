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
    public static GameWorldInstance game = new GameWorldInstance();

    public static void run() {
        // After setting up the socket listener, start an instance of libgdx on the server.
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // Start lobgdx with mockGraphics, to allow us to use all libgdx features on the server.
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

        // Set up event groups
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // Start socket
            ServerBootstrap bootstrap = new ServerBootstrap().group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class).childHandler(new RoboCopServerInitializer());
            System.out.println("Binding to port " + port + "...");
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }

    public static void main(String[] args) {
        run();
    }
}

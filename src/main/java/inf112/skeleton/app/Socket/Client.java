package inf112.skeleton.app.Socket;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.app.RoboRally;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.awt.*;

public class Client {
    static RoboRally game;
    public static EventLoopGroup group;

    public static void main(String[] args) throws InterruptedException {
        game = new RoboRally();
        try {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {

                        Lwjgl3ApplicationConfiguration application = new Lwjgl3ApplicationConfiguration();
                        application.setWindowedMode(RoboRally.WIDTH, RoboRally.HEIGHT);
                        application.setTitle(RoboRally.TITLE);
                        game.nioWorkerGroup = group;
                        new Lwjgl3Application(game, application);
                        game.channel.closeFuture();
                        group.shutdownGracefully();
                        System.out.println("stopped");
                        System.exit(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            group = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class) //use new io sockets
                    .handler(new GameSocketInitializer(game)); //handle all IncomingPacket messages

            ChannelFuture f = bootstrap.connect("localhost", 58008).sync();
            game.channel = f.channel(); // creating a connection with the server
        } finally {
        }
    }
}

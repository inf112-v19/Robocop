package inf112.skeleton.app.Socket;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.app.RoboRally;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    static RoboRally game;
    public static EventLoopGroup group;

    public static void main(String[] args) throws InterruptedException {
        game = new RoboRally();

        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class) //use new io sockets
                .handler(new GameSocketInitializer(game)); //handle all ToServer messages

        ChannelFuture f = bootstrap.connect("localhost", 58008).sync();
        RoboRally.channel = f.channel(); // creating a connection with the server
        Lwjgl3ApplicationConfiguration application = new Lwjgl3ApplicationConfiguration();
        application.setWindowedMode(RoboRally.width, RoboRally.height);
        application.setResizable(false);
        application.setTitle(RoboRally.TITLE);

        game.nioWorkerGroup = group;

        new Lwjgl3Application(game, application);

        RoboRally.channel.closeFuture();
        group.shutdownGracefully();
        System.out.println("stopped");
        System.exit(1);

    }

}

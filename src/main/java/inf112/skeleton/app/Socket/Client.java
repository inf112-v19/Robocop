package inf112.skeleton.app.Socket;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.app.ChatGUI;
import inf112.skeleton.app.RoboRally;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.awt.*;

public class Client {
    static RoboRally game;

    public static void main(String[] args) throws InterruptedException {
        game = new RoboRally();
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {

                    Lwjgl3ApplicationConfiguration application = new Lwjgl3ApplicationConfiguration();
                    application.setWindowedMode(RoboRally.WIDTH,RoboRally.HEIGHT);
                    application.setTitle(RoboRally.TITLE);
                    //cfg.title = RoboRally.TITLE;
                    //cfg.width = RoboRally.WIDTH;
                    //cfg.height = RoboRally.HEIGHT;

                    new Lwjgl3Application(game, application);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class) //use new io sockets
                .handler(new ChatLoginInitializer(game)); //handle all IncomingPacket messages

        game.channel = bootstrap.connect("localhost",58008).sync().channel(); // creating a connection with the server
    }
}

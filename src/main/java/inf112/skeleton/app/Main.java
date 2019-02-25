    package inf112.skeleton.app;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.*;
import com.badlogic.gdx.backends.headless.mock.graphics.MockGraphics;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import inf112.skeleton.server.ChatServer;
import inf112.skeleton.server.ChatServerHandler;
import static org.mockito.Mockito.mock;

import java.io.File;


    public class Main {
    public static void main(String[] args) throws InterruptedException {
        boolean server = false;
        if(args.length > 0){
            if (args[0].equalsIgnoreCase("true")){
                server = true;
            }
        }


        if (!server){
            LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
            cfg.title = "RoboRally";
            cfg.width = 1920;
            cfg.height = 1080;

            new LwjglApplication(new RoboRally(), cfg);
        } else {
            MockGraphics mockGraphics = new MockGraphics();
            Gdx.graphics = mockGraphics;
            Gdx.gl = mock(GL20.class);
            HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
            new HeadlessApplication(new ChatServer(58008), config);
        }

    }
}
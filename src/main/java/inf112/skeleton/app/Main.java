package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.server.Server;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        boolean server = false;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("true")) {
                server = true;
            }
        }


        if (!server) {
            // TODO: Ask Henning why Lwjgl3 is used
            Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
            cfg.setTitle(RoboRally.TITLE);
            //cfg.title = RoboRally.TITLE;
            //cfg.width = RoboRally.WIDTH;
            //cfg.height = RoboRally.HEIGHT;

            new Lwjgl3Application(new RoboRally(), cfg);
        } else {
            Server.run();
        }

    }
}
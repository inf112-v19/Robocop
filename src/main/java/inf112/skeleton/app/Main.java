package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import inf112.skeleton.app.Socket.Client;
import inf112.skeleton.server.Server;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        Client.main(args);
    }
}
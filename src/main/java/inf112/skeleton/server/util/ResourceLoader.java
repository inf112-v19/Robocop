package inf112.skeleton.server.util;

import com.badlogic.gdx.maps.tiled.TmxMapLoader;

public class ResourceLoader {
    public static String loadResource(){
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        return classLoader.getResource("Board.tmx").getPath();

    }
}

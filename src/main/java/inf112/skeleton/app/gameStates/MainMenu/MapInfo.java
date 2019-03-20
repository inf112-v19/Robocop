package inf112.skeleton.app.gameStates.MainMenu;


import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

enum MapDifficulty {
    Easy,
    Intermediate,
    Hard,
    Expert,
}

public class MapInfo {
    String  mapName,
            hostName,
            description;
    int     numPlayers,
            maxPlayers,
            minRecommendedPlayers,
            maxRecommendedPlayers;
    Drawable minimap_preview;
    MapDifficulty difficulty;

    public MapInfo(
            String mapName,
            String hostName,
            String description,
            int numPlayers,
            int maxPlayers,
            int minRecommendedPlayers,
            int maxRecommendedPlayers,
            MapDifficulty difficulty,
            Drawable minimap_preview
    ){
        this.mapName                = mapName;
        this.hostName               = hostName;
        this.description            = description;
        this.numPlayers             = numPlayers;
        this.maxPlayers             = maxPlayers;
        this.minRecommendedPlayers  = minRecommendedPlayers;
        this.maxRecommendedPlayers  = maxRecommendedPlayers;
        this.difficulty             = difficulty;
        this.minimap_preview        = minimap_preview;
    }
}

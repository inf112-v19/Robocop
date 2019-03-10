package inf112.skeleton.app.gameStates.NewMainMenu;


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
    int     maxPlayers,
            minRecommendedPlayers,
            maxRecommendedPlayers;
    Drawable minimap_preview;
}

package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;


public class Flag extends Entity {
    private Animation<TextureRegion> currentAnimation;
    private BitmapFont font = new BitmapFont();
    private float stateTime;
    private int active;
    private int flagNumber;


    public Flag(float x, float y, int flagNumber) {
        super(x, y, EntityType.FLAG);
        this.active = 0;
        this.flagNumber = flagNumber;
    }

    public void disableFlag() {
        this.active = 1;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        currentAnimation = Sprites.animation_flag[active];
        stateTime += Gdx.graphics.getDeltaTime();
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, (pos.x * 64), (pos.y * 64), getWidth(), getHeight());
        renderNumber(batch);
    }

    @Override
    public void renderName(SpriteBatch batch, float scale) {
        return;
    }

    public void renderNumber(SpriteBatch batch) {
        if(active == 0) {
            final GlyphLayout layout = new GlyphLayout(font, "" + flagNumber);
            final float fontX = (pos.x * 64) + 3 + ((64-layout.width) / 2);
            final float fontY = (pos.y * 64) + 26;
            font.setColor(255, 0, 0, 255);
            font.draw(batch, "" + flagNumber, fontX, fontY);
        }
    }
}

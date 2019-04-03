package inf112.skeleton.app.GUI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import inf112.skeleton.app.RoboRally;

public class Timer extends Table {
    private long    endTime;
    private Label   timeLabel;

    private long originalms, ms;
    private int decimals;

    public Timer(String displayText, int ms, int decimals) {
        super();
        this.ms = originalms = (long)ms;
        this.decimals = decimals;

        endTime = 0;

        add(new Label(displayText, RoboRally.graphics.labelStyle_markup_enabled));

        timeLabel = new Label(formatTime(ms),  RoboRally.graphics.labelStyle_markup_enabled);
        add(timeLabel);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (endTime != 0) {
            timeLabel.setText(formatTime(endTime - System.currentTimeMillis()));
        }
        super.draw(batch, parentAlpha);
    }

    private String formatTime(long ms) {
        String format = String.format("%%.%df s", decimals);
        return String.format(format, ms > 0 ? ms/1000f : 0f);
    }

    public void start() {
        endTime = System.currentTimeMillis() + ms;
    }

    public void pause() {
        long currentTime = System.currentTimeMillis();
        ms = originalms - currentTime;
    }

    public void reset() {
        ms = originalms;
        start();
    }
}

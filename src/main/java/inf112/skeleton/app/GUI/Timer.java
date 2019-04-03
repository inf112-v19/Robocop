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

    /**
     * Initialize count-down timer.
     * @param displayText Text to be displayed before timer
     * @param ms number of milliseconds to count down from
     * @param decimals number of decimal points of timer
     */
    public Timer(String displayText, int ms, int decimals) {
        super();
        this.ms = originalms = (long)ms;
        this.decimals = decimals;

        endTime = 0;

        add(new Label(displayText, RoboRally.graphics.labelStyle_markup_enabled));

        timeLabel = new Label(formatTime(ms),  RoboRally.graphics.labelStyle_markup_enabled);
        add(timeLabel);
    }

    /**
     * Updates timer-text and draws timer to screen.
     * @param batch spritebatch
     * @param parentAlpha opacity
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (endTime != 0) {
            timeLabel.setText(formatTime(endTime - System.currentTimeMillis()));
        }
        super.draw(batch, parentAlpha);
    }

    /**
     * Formats milliseconds to seconds with correct number of decimal places.
     * @param ms milliseconds
     * @return String formated with correct number of decimal places.
     */
    private String formatTime(long ms) {
        String format = String.format("%%.%df s", decimals);
        return String.format(format, ms > 0 ? ms/1000f : 0f);
    }

    /**
     * Start/Resume timer.
     */
    public void start() {
        endTime = System.currentTimeMillis() + ms;
    }

    /**
     * Set the time when the timer should end (used for synchronization between clients).
     * @param endTime time till timer should end.
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     * Pauses the timer. May be resumed using start().
     */
    public void pause() {
        long currentTime = System.currentTimeMillis();
        ms = originalms - currentTime;
    }

    /**
     * Reset timer.
     */
    public void reset() {
        ms = originalms;
        start();
    }
}

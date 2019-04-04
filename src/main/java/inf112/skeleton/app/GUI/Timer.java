package inf112.skeleton.app.GUI;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import inf112.skeleton.app.RoboRally;

/**
 * Count-down timer. Run start() to start timer.
 * Note: Fires ChangeEvent upon ending. Check isFinished to be sure timer has ended.
 */
public class Timer extends Table {
    private long    endTime;
    private Label   timeLabel;

    private long originalms, ms;
    private int decimals;

    public boolean isFinished;

    /**
     * Initialize count-down timer.
     * @param displayText Text to be displayed before timer
     * @param ms number of milliseconds to count down from
     * @param decimals number of decimal points of timer
     */
    public Timer(String displayText, int ms, int decimals){
        super();
        this.decimals = decimals;

        originalms = (long)ms;

        setBackground(RoboRally.graphics.messageBox_bg);


        add(new Label(displayText, RoboRally.graphics.labelStyle_markup_enabled));

        timeLabel = new Label(formatTime(ms),  RoboRally.graphics.labelStyle_markup_enabled);
        add(timeLabel);

        reset();
    }

    /**
     * Updates timer-text and draws timer to screen.
     * @param batch spritebatch
     * @param parentAlpha opacity
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        long currentTime = System.currentTimeMillis();

        if (!isFinished) {
            timeLabel.setText(formatTime(endTime - currentTime));

            if (endTime <= currentTime) {
                isFinished = true;
                fire(new ChangeListener.ChangeEvent());
            }
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
        isFinished = false;
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
        isFinished = true;
    }
}

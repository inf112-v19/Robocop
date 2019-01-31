package inf112.skeleton.app.robotTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import inf112.skeleton.app.robot.Directions;
import inf112.skeleton.app.robot.Robot;
import inf112.skeleton.app.card.Card;
import org.junit.Test;

import java.util.Random;

public class robotTest {
    Robot robot;
    {
        robot = new Robot(1, null);
    }

    Random random;
    {
        random = new Random();
    }

    @Test
    public void rotateLeftBaseCase() {
        robot.rotateLeft();
        assertEquals(Directions.WEST, robot.getFacingDirection());
    }

    @Test
    public void rotateLeft180() {
        robot.rotateLeft();
        robot.rotateLeft();
        assertEquals(Directions.SOUTH, robot.getFacingDirection());
    }

    @Test
    public void rotateLeft270() {
        robot.rotateLeft();
        robot.rotateLeft();
        robot.rotateLeft();
        assertEquals(Directions.EAST, robot.getFacingDirection());
    }

    @Test
    public void rotateLeft360() {
        robot.rotateLeft();
        robot.rotateLeft();
        robot.rotateLeft();
        robot.rotateLeft();
        assertEquals(Directions.NORTH, robot.getFacingDirection());
    }

    @Test
    public void rotateRightBaseCase() {
        robot.rotateRight();
        assertEquals(Directions.EAST, robot.getFacingDirection());
    }

    @Test
    public void rotateRight180() {
        robot.rotateRight();
        robot.rotateRight();
        assertEquals(Directions.SOUTH, robot.getFacingDirection());
    }

    @Test
    public void rotateRight270() {
        robot.rotateRight();
        robot.rotateRight();
        robot.rotateRight();
        assertEquals(Directions.WEST, robot.getFacingDirection());
    }

    @Test
    public void rotateRight360() {
        robot.rotateRight();
        robot.rotateRight();
        robot.rotateRight();
        robot.rotateRight();
        assertEquals(Directions.NORTH, robot.getFacingDirection());
    }

    @Test
    public void rotateRightGeneralCase() {
        for (int i = 0; i < 100000; i++) {
            Robot robotRight = new Robot(2, null);
            int r = random.nextInt(1000);
            for (int j = 0; j < r; j++) {
                robotRight.rotateRight();
            }
            switch (r % Directions.values().length) {
                case 0:
                    assertEquals(Directions.NORTH, robotRight.getFacingDirection());
                    break;
                case 1:
                    assertEquals(Directions.EAST, robotRight.getFacingDirection());
                    break;
                case 2:
                    assertEquals(Directions.SOUTH, robotRight.getFacingDirection());
                    break;
                case 3:
                    assertEquals(Directions.WEST, robotRight.getFacingDirection());
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    public void rotateLeftGeneralCase() {
        for (int i = 0; i < 100000; i++) {
            Robot robotLeft = new Robot(3, null);
            int r = random.nextInt(1000);
            for (int j = 0; j < r; j++) {
                robotLeft.rotateLeft();
            }
            switch (r % Directions.values().length) {
                case 0:
                    assertEquals(Directions.NORTH, robotLeft.getFacingDirection());
                    break;
                case 1:
                    assertEquals(Directions.WEST, robotLeft.getFacingDirection());
                    break;
                case 2:
                    assertEquals(Directions.SOUTH, robotLeft.getFacingDirection());
                    break;
                case 3:
                    assertEquals(Directions.EAST, robotLeft.getFacingDirection());
                    break;
                default:
                    break;
            }
        }
    }
}

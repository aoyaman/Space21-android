package aoyaman.github.io;

import org.junit.Test;

import aoyaman.github.io.model.PlayerType;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class GameTest {
    @Test
    public void testCconstructor() {
        boolean isError = false;
        try {
            new Game(null);
        } catch (IllegalArgumentException e) {
            isError = true;
        }
        assertTrue(isError);

        isError = false;
        try {
            PlayerType[] players = new PlayerType[3];
            new Game(players);
        } catch (IllegalArgumentException e) {
            isError = true;
        }
        assertTrue(isError);
    }
}
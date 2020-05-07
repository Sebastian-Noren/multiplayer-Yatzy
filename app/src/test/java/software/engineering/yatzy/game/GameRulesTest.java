package software.engineering.yatzy.game;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class GameRulesTest {
    int[] dice = {1, 6, 6, 1, 1};

    @Test
    public void largeStraight() {
        Arrays.sort(dice);
        boolean isStraight = false;
        if (((dice[0] == 2) &&
                (dice[1] == 3) &&
                (dice[2] == 4) &&
                (dice[3] == 5) &&
                (dice[4] == 6))) {
            isStraight = true;
        }

        assertTrue(isStraight);

    }

    @Test
    public void smallStraight() {
        Arrays.sort(dice);
        boolean isStraight = false;
        if (((dice[0] == 1) &&
                (dice[1] == 2) &&
                (dice[2] == 3) &&
                (dice[3] == 4) &&
                (dice[4] == 5))) {
            isStraight = true;
        }
        assertTrue(isStraight);
    }

    @Test
    public void fullHouse() {
        Arrays.sort(dice);
        boolean isFullHouse = false;
        if ((((dice[0] == dice[1]) && (dice[1] == dice[2])) && (dice[3] == dice[4])
                && (dice[2] != dice[3])) ||
                ((dice[0] == dice[1]) && ((dice[2] == dice[3]) && (dice[3] == dice[4]))
                        && (dice[1] != dice[2]))) {
            isFullHouse = true;
        }
        assertTrue(isFullHouse);
    }

    @Test
    public void yatzy() {
        boolean yatzy = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if ((count > 4)){
                    yatzy = true;
                }
            }
        }
        assertTrue(yatzy);
    }

    @Test
    public void threeOfAKind() {
        boolean threeDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if ((count > 2)){
                    threeDice = true;
                }
            }
        }
        assertTrue(threeDice);
    }

    @Test
    public void fourOfAKind() {
        boolean fourDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if ((count > 3)){
                    fourDice = true;
                }
            }
        }
        assertTrue(fourDice);
    }

    @Test
    public void onePair() {
        boolean isOnePar = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if ((count > 1)){
                    isOnePar = true;
                }
            }
        }
        assertTrue(isOnePar);
    }

    @Test
    public void twoPair() {
        Arrays.sort(dice);
        System.out.println(Arrays.toString(dice));
        boolean isTwoPair = false;
        if (((dice[0] == dice[1]) && (dice[2] == dice[3])) ||
                ((dice[1] == dice[2]) && (dice[3] == dice[4]) && dice[2] !=dice[3]) ||
                ((dice[0] == dice[1]) && (dice[3] == dice[4]))
        ) {
            isTwoPair = true;
        }
        assertTrue(isTwoPair);
    }

    @Test
    public void singelSide() {
        int diceSide = 1;
        boolean isthisSide = false;
        for (int die : dice) {
            if (die == diceSide) {
                isthisSide = true;
            }
        }
        assertTrue(isthisSide);
    }
}
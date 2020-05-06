package software.engineering.yatzy.game;

import java.util.Arrays;

public class GameRules {

    //Stor stege
    public boolean largeStraight(int[] dice) {
        boolean isStraight = false;

        if (((dice[0] == 2) &&
                (dice[1] == 3) &&
                (dice[2] == 4) &&
                (dice[3] == 5) &&
                (dice[4] == 6))) {
            isStraight = true;
        }
        return isStraight;
    }

    // liten stege
    public boolean smallStraight(int[] dice) {
        boolean isStraight = false;

        if (((dice[0] == 1) &&
                (dice[1] == 2) &&
                (dice[2] == 3) &&
                (dice[3] == 4) &&
                (dice[4] == 5))) {
            isStraight = true;
        }
        return isStraight;
    }

    //KÅK
    public boolean fullHouse(int[] dice) {
        boolean isFullHouse = false;
        if ((((dice[0] == dice[1]) && (dice[1] == dice[2])) && (dice[3] == dice[4])
                && (dice[2] != dice[3])) ||
                ((dice[0] == dice[1]) && ((dice[2] == dice[3]) && (dice[3] == dice[4]))
                        && (dice[1] != dice[2]))) {
            isFullHouse = true;
        }
        return isFullHouse;
    }

    public boolean yatzy( int[] dice ) {
        boolean yatzy = false;
        for( int i = 1; i <= 6; i++ ) {
            int count = 0;
            for( int j = 0; j < 5; j++ ) {
                if( dice[j] == i ) {
                    count++;
                }
                yatzy = (count > 4);
            }
        }
        return yatzy;
    }

    // triss
    public boolean threeOfAKind(int[] dice) {
        boolean threeDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                threeDice = (count > 2);
            }
        }
        // System.out.println(count);
        return threeDice;
    }

    // fyrpar
    public boolean fourOfAKind(int[] dice) {
        boolean fourDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                fourDice = (count > 3);
            }
        }
        return fourDice;
    }

    public boolean onePair(int[] dice) {
        boolean isOnePar = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                isOnePar = (count > 1);
            }
        }
        return isOnePar;
    }

    public boolean twoPair(int[] dice) {
        boolean isTwoPair = false;
        if (((dice[0] == dice[1]) && (dice[2] == dice[3])) ||
                ((dice[1] == dice[2]) && (dice[3] == dice[4]))
        ) {
            isTwoPair = true;
        }
        return isTwoPair;
    }

    public boolean singelSide(int diceSide, int[] dice) {
        for (int die : dice) {
            if (die == diceSide) {
                return true;
            }
        }
        return false;
    }

}

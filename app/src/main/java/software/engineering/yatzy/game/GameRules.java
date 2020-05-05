package software.engineering.yatzy.game;

import java.util.Arrays;

public class GameRules {

    //Stor stege
    public boolean largeStraight(int[] i) {
        boolean isStraight = false;
        Arrays.sort(i);

        if (((i[0] == 2) &&
                (i[1] == 3) &&
                (i[2] == 4) &&
                (i[3] == 5) &&
                (i[4] == 6))) {
            isStraight = true;
        }
        return isStraight;
    }
    // leten stege
    public boolean smallStraight(int[] i) {
        boolean isStraight = false;
        Arrays.sort(i);

        if (((i[0] == 1) &&
                (i[1] == 2) &&
                (i[2] == 3) &&
                (i[3] == 4) &&
                (i[4] == 5))) {
            isStraight = true;
        }
        return isStraight;
    }

    //KÅK
    public boolean fullHouse(int[] dice) {
        boolean isFullHouse = false;

        Arrays.sort(dice);
        if ((((dice[0] == dice[1]) && (dice[1] == dice[2])) && (dice[3] == dice[4])
                && (dice[2] != dice[3])) ||
                ((dice[0] == dice[1]) && ((dice[2] == dice[3]) && (dice[3] == dice[4]))
                        && (dice[1] != dice[2]))) {
            isFullHouse = true;
        }
        return isFullHouse;
    }
    // triss
    public boolean threeOfAKind(int[] myDice) {
        boolean threeDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (myDice[j] == i) {
                    count++;
                }
                threeDice = (count > 2) && (count < 4);
            }
        }
        // System.out.println(count);
        return threeDice;
    }

    // fyrpar
    public boolean fourOfAKind(int[] myDice) {
        boolean fourDice = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (myDice[j] == i) {
                    count++;
                }
                fourDice = count > 3 && count < 5;
            }
        }
        return fourDice;
    }

    private static boolean onePair(int[] myDice) {
        boolean isOnePar = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (myDice[j] == i) {
                    count++;
                }
                isOnePar = (count > 1) && (count < 3);
            }
        }
        return isOnePar;
    }

}

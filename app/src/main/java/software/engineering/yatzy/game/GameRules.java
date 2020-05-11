package software.engineering.yatzy.game;

import android.util.Log;

import java.util.Arrays;

public class GameRules {
    private static final String TAG = "Info";

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
        if ((((dice[0] == dice[1]) && (dice[1] == dice[2])) && (dice[3] == dice[4] && (dice[0] != -1) && (dice[3] != -1))
                && (dice[2] != dice[3])) ||
                ((dice[0] == dice[1]) && ((dice[2] == dice[3]) && (dice[3] == dice[4]) && (dice[0] != -1) && (dice[3] != -1))
                        && (dice[1] != dice[2]))) {
            isFullHouse = true;
        }
        return isFullHouse;
    }

    public boolean yatzy(int[] dice) {
        boolean yatzy = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if ((count > 4)) {
                    yatzy = true;
                }
            }
        }
        return yatzy;
    }

    // triss
    public boolean threeOfAKind(int[] dice) {
        boolean threeDice = false;
        boolean check = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if (count == 3) {
                    check = true;
                }
            }
        }
        int safeCheck = 0;
        for (int k = 0; k < 5; k++) {
            if (dice[k] == -1) {
                safeCheck++;
            }
            if (safeCheck == 1) {
                threeDice = false;
            } else if (check && safeCheck >= 2) {
                threeDice = true;
            }
        }
        return threeDice;
    }

    // fyrpar
    public boolean fourOfAKind(int[] dice) {
        boolean fourDice = false;
        boolean check = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i && dice[j] != -1) {
                    count++;
                }
                if ((count == 4)) {
                    check = true;
                }
            }
        }
        int safeCheck = 0;
        for (int k = 0; k < 5; k++) {
            if (dice[k] == -1) {
                safeCheck++;
            }
            if (safeCheck == 0) {
                fourDice = false;
            } else if (check && safeCheck >= 1) {
                fourDice = true;
            }
        }
        return fourDice;
    }

    public boolean onePair(int[] dice) {
        boolean isOnePar = false;
        boolean check = false;
        for (int i = 1; i <= 6; i++) {
            int count = 0;
            for (int j = 0; j < 5; j++) {
                if (dice[j] == i) {
                    count++;
                }
                if (count == 2) {
                    check = true;
                }
            }
        }
        int safeCheck = 0;
        for (int k = 0; k < 5; k++) {
            if (dice[k] == -1) {
                safeCheck++;
            }
            if (safeCheck == 2) {
                isOnePar = false;
            } else if (check && safeCheck >= 2) {
                isOnePar = true;
            }
        }
        return isOnePar;
    }

    public boolean twoPair(int[] dice) {
        boolean isTwoPair = false;
        boolean check = false;
        if (((dice[0] == dice[1]) && (dice[2] == dice[3]) && (dice[0] != -1) && (dice[2] != -1))
                || ((dice[1] == dice[2]) && (dice[3] == dice[4]) && dice[2] != dice[3] && (dice[1] != -1) && (dice[3] != -1))
                || ((dice[0] == dice[1]) && (dice[3] == dice[4] && (dice[0] != -1) && (dice[3] != -1))))
        {
            check = true;
        }
        int safeCheck = 0;
        for (int k = 0; k < 5; k++) {
            if (dice[k] == -1) {
                safeCheck++;
            }
            if (safeCheck == 0) {
                isTwoPair = false;
            } else if (check && safeCheck >= 1) {
                isTwoPair = true;
            }
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

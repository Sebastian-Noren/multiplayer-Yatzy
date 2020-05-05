package software.engineering.yatzy.game;

public class Dice {

    private DiceName diceName;
    private boolean rolling;
    private int diceValue;
    private boolean isSelected;

    public Dice(DiceName diceName, boolean rolling, int diceValue, boolean isSelected) {
        this.diceName = diceName;
        this.rolling = rolling;
        this.diceValue = diceValue;
        this.isSelected = isSelected;
    }

    public DiceName getDiceName() {
        return diceName;
    }

    public boolean isRolling() {
        return rolling;
    }

    public void setRolling(boolean rolling) {
        this.rolling = rolling;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public void setDiceValue(int diceValue) {
        this.diceValue = diceValue;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

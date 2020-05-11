package software.engineering.yatzy.game;

public class Dice {

    private DiceName diceName;
    private int diceValue;
    private boolean isSelected;

    public Dice(DiceName diceName, int diceValue, boolean isSelected) {
        this.diceName = diceName;
        this.diceValue = diceValue;
        this.isSelected = isSelected;
    }

    public DiceName getDiceName() {
        return diceName;
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

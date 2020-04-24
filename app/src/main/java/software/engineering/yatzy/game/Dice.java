package software.engineering.yatzy.game;

public class Dice {

    private String diceName;
    private boolean rolling;
    private int diceValue;
    private boolean isSelected;

    public Dice(String diceName, boolean rolling, int diceValue, boolean isSelected) {
        this.diceName = diceName;
        this.rolling = rolling;
        this.diceValue = diceValue;
        this.isSelected = isSelected;
    }

    public String getDiceName() {
        return diceName;
    }

    public void setDiceName(String diceName) {
        this.diceName = diceName;
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

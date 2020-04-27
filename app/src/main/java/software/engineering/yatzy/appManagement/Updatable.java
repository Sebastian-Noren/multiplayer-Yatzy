package software.engineering.yatzy.appManagement;

public interface Updatable {
    /**
     * @param protocolIndex    Corresponds to application protocol index 0 (what kind of update it is)
     * @param specifier        Optional. Corresponds to application protocol index 1. (E.g. to specify which game has been updated.) -1 if not used
     * @param exceptionMessage Optional. To carry exception message for display in current fragment (combined with protocolIndex: 40). null if not used
     *                         <p>
     *                         Ex: "update(18, 5, null);" = Updated turnState (18) in game with gameID 5. No exception message
     */

    void update(int protocolIndex, int specifier, String exceptionMessage);
}

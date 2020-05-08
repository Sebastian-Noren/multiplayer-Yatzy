package software.engineering.yatzy.appManagement;

/**
 * To accommodate for app being put in background during connection phase (where the UI is not updatable).
 * - To guide the UI properly when regaining focus after connection phase
 * - Avoid getting stuck in e.g. Setup fragment despite successful login.
 */

public enum NetworkState {
    UNDEFINED, // is just started
    LOGIN,     // is in Login Fragment
    ALLOWED,   // login was successful (authorized to enter main menu)
    ENTERED    // has entered the system (main menu)
}

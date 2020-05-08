package software.engineering.yatzy.overview;

public class Room {
    private String title;
    private String description;
    private String Status;
    private int roomID;

    public Room(String title, String description, String status, int roomID) {
        this.title = title;
        this.description = description;
       this.Status = status;
       this.roomID = roomID;

    }

    public Room(String title, String description, String status) {
        this.title = title;
        this.description = description;
        this.Status = status;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    @Override
    public String toString() {
        return "Room{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", Status='" + Status + '\'' +
                ", roomID=" + roomID +
                '}';
    }
}

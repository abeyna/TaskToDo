package funix.prm.databasesqlite;

public class Task {
    private int idTask;
    private String nameTask;

    public Task(int idTask, String nameTask) {
        this.idTask = idTask;
        this.nameTask = nameTask;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }
}

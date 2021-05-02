package funix.prm.databasesqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Database database;
    private ListView lvTasks;
    private ArrayList<Task> arrayTasks;
    private TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvTasks = (ListView) findViewById(R.id.activity_main_lv_tasks);
        arrayTasks = new ArrayList<>();
        adapter = new TaskAdapter(this, R.layout.task_to_do, arrayTasks);
        lvTasks.setAdapter(adapter);

        // Tạo database cho ghi chú
        database = new Database(this, "notes.sql", null, 1);

        // Tạo bảng công việc
        database.QueryData("CREATE TABLE IF NOT EXISTS TaskToDo(Id INTEGER PRIMARY KEY AUTOINCREMENT, Task VARCHAR(200))");

        // Insert data

        // Select data
        getTaskData();
    }

    public void dialogDeleteTask(String task, final int id) {
        AlertDialog.Builder dialogDelete = new AlertDialog.Builder(this);
        dialogDelete.setMessage("'" + task + "' will be removed?");
        dialogDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                database.QueryData("DELETE FROM TaskToDo WHERE Id = '"+id+"'");
                Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                getTaskData();
            }
        });
        dialogDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogDelete.show();
    }

    public void dialogEditTask(String task, final int id) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_edit_task);

        final EditText etTask = (EditText) dialog.findViewById(R.id.dialog_edit_task_et);
        Button btnAccept = (Button) dialog.findViewById(R.id.dialog_edit_task_btn_accept);
        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_edit_task_btn_cancel);

        etTask.setText(task);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editedTask = etTask.getText().toString().trim();
                database.QueryData("UPDATE TaskToDo SET Task = '"+editedTask+"' WHERE Id = '"+id+"'");
                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                getTaskData();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void getTaskData() {
        Cursor dataTask = database.GetData("SELECT * FROM TaskToDo");
        arrayTasks.clear();
        while (dataTask.moveToNext()) {
            String name = dataTask.getString(1);
            int id = dataTask.getInt(0);
            arrayTasks.add(new Task(id, name));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.import_task, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.import_task_btn_add) {
            DialogNewTask();
        };
        return super.onOptionsItemSelected(item);
    }

    private void DialogNewTask() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_import_task);

        EditText etTask = (EditText) dialog.findViewById(R.id.dialog_import_task_et);
        Button btnAdd = (Button) dialog.findViewById(R.id.dialog_import_task_btn_add);
        Button btnCancel = (Button) dialog.findViewById(R.id.dialog_import_task_btn_cancel);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newTask = etTask.getText().toString();
                if (newTask.equals("")) {
                    Toast.makeText(MainActivity.this, "Please import your task", Toast.LENGTH_SHORT).show();
                } else {
                    database.QueryData("INSERT INTO TaskToDo VALUES(null, '"+ newTask +"')");
                    Toast.makeText(MainActivity.this, "Import successfully!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    getTaskData();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
}
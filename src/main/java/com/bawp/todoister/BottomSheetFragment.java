package com.bawp.todoister;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bawp.todoister.model.Priority;
import com.bawp.todoister.model.SharedViewModel;
import com.bawp.todoister.model.Task;
import com.bawp.todoister.model.TaskViewModel;
import com.bawp.todoister.util.AlarmBroadcastReceiver;
import com.bawp.todoister.util.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.Group;
import androidx.lifecycle.ViewModelProvider;

public class BottomSheetFragment extends BottomSheetDialogFragment{
    Calendar calendar = Calendar.getInstance();
    private EditText enterTodo;
    private EditText taskTime;
    private ImageButton calendarButton;
    private ImageButton priorityButton;
    private RadioGroup priorityRadioGroup;
    private RadioButton selectedRadioButton;
    private int selectedButtonId;
    private ImageButton saveButton;
    private CalendarView calendarView;
    private Group calendarGroup;
    private Date dueDate;
    private SharedViewModel sharedViewModel;
    private boolean isEdit;
    private Priority priority;

    AlarmManager alarmManager;

    int mHour, mMinute;

    TimePickerDialog timePickerDialog;

    int dd;

    Context context;

    public BottomSheetFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        calendarGroup = view.findViewById(R.id.calendar_group);
        calendarView = view.findViewById(R.id.calendar_view);
        calendarButton = view.findViewById(R.id.today_calendar_button);
        enterTodo = view.findViewById(R.id.enter_todo_et);
        taskTime = view.findViewById(R.id.task_time);
        saveButton = view.findViewById(R.id.save_todo_button);
        priorityButton = view.findViewById(R.id.priority_todo_button);
        priorityRadioGroup = view.findViewById(R.id.radioGroup_priority);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sharedViewModel.getSelectedItem().getValue() != null) {
            isEdit = sharedViewModel.getIsEdit();
            Task task = sharedViewModel.getSelectedItem().getValue();
            enterTodo.setText(task.getTask());
            Log.d("MY", "onViewCreated: " + isEdit + " " + task.getTask());
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity())
                .get(SharedViewModel.class);

        calendarButton.setOnClickListener(view12 -> {
            calendarGroup.setVisibility(calendarGroup.getVisibility() == View.GONE ?
                    View.VISIBLE : View.GONE);
            Utils.hideSoftKeyboard(view12);

        });
        calendarView.setOnDateChangeListener((calendarView, year, month, dayOfMoth) -> {
            calendar.clear();
            dd = dayOfMoth;
            calendar.set(year, month, dayOfMoth);
            dueDate = calendar.getTime();

        });
        priorityButton.setOnClickListener(view13 -> {
            Utils.hideSoftKeyboard(view13);
            priorityRadioGroup.setVisibility(
                    priorityRadioGroup.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
            priorityRadioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                 if (priorityRadioGroup.getVisibility() == View.VISIBLE) {
                      selectedButtonId = checkedId;
                      selectedRadioButton = view.findViewById(selectedButtonId);
                      if (selectedRadioButton.getId() == R.id.radioButton_high) {
                          priority = Priority.HIGH;
                      } else if (selectedRadioButton.getId() == R.id.radioButton_med) {
                           priority = Priority.MEDIUM;
                      }else if (selectedRadioButton.getId() == R.id.radioButton_low) {
                          priority = Priority.LOW;
                      }else {
                          priority = Priority.LOW;
                      }
                 }else{
                     priority = Priority.LOW;
                 }
            });
        });

        saveButton.setOnClickListener(view1 -> {
            String task = enterTodo.getText().toString().trim();
            String time = taskTime.getText().toString().trim();

            if (!TextUtils.isEmpty(task) && dueDate != null && priority != null && !TextUtils.isEmpty(time)) {
                Task myTask = new Task(task, priority,
                        dueDate, Calendar.getInstance().getTime()
                        ,time
                        ,false);
                if (isEdit) {
                    Task updateTask = sharedViewModel.getSelectedItem().getValue();

                    updateTask.setTask(task);
                    updateTask.setDateCreated(Calendar.getInstance().getTime());
                    updateTask.setTaskTime(time);
                    updateTask.setPriority(priority);
                    updateTask.setDueDate(dueDate);
                    TaskViewModel.update(updateTask);
                    sharedViewModel.setIsEdit(false);
                    createAnAlarm(task);

                } else {
                    TaskViewModel.insert(myTask);
                    createAnAlarm(task);
                }
                enterTodo.setText("");
                if (this.isVisible()) {
                    this.dismiss();
                }

            }else {
                Snackbar.make(saveButton, R.string.empty_field, Snackbar.LENGTH_LONG)
                        .show();
            }
        });

        taskTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    // Get Current Time
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);

                    // Launch Time Picker Dialog
                    timePickerDialog = new TimePickerDialog(getActivity(),
                            (view12, hourOfDay, minute) -> {
                                taskTime.setText(hourOfDay + ":" + minute);
                                timePickerDialog.dismiss();
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
                return false;
            }
        });

    }

    public void createAnAlarm(String task) {
        try {


            String[] itemTime = taskTime.getText().toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

            Calendar cur_cal = new GregorianCalendar();
            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = new GregorianCalendar();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
            cal.set(Calendar.MINUTE, Integer.parseInt(min));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, dd);
            System.out.println(cal);


            Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", task);
            alarmIntent.putExtra("TIME", taskTime.getText().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
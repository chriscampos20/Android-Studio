package proyecto_pastilla.Activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import proyecto_pastilla.Adapters.ReminderAdapter;
import proyecto_pastilla.Adapters.SliderAdapters;
import proyecto_pastilla.Domain.Reminder;
import proyecto_pastilla.Domain.SliderItems;
import com.example.proyecto_pastilla.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 medicationRemindersSlider;
    private RecyclerView remindersRecyclerView, takenRemindersRecyclerView;
    private ReminderAdapter reminderAdapter, takenReminderAdapter;
    private List<Reminder> reminderList = new ArrayList<>(), takenReminders = new ArrayList<>();
    private final Handler slideHandler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver reminderReceiver;

    public static final String ACTION_REMINDER_TAKEN = "ACTION_REMINDER_TAKEN";
    public static final String ACTION_UPDATE_REMINDER_STATUS = "proyecto_pastilla.UPDATE_REMINDER_STATUS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setupSlider();
        setupCreateReminderButton();
        setupRemindersRecyclerView();
        handleReminderBroadcast();

        IntentFilter updateFilter = new IntentFilter(ACTION_UPDATE_REMINDER_STATUS);
        registerReceiver(updateStatusReceiver, updateFilter);

        addSampleTakenReminders();
        setupShowHistoryButton();
    }

    public void setupShowHistoryButton() {
        Button showHistoryButton = findViewById(R.id.ShowHistory);
        showHistoryButton.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.dialog_taken_reminders);

            RecyclerView takenRemindersRecyclerView = dialog.findViewById(R.id.takenRemindersRecyclerView);
            takenRemindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            takenRemindersRecyclerView.setAdapter(takenReminderAdapter);

            dialog.show();
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addSampleTakenReminders() {
        Reminder takenReminder1 = new Reminder("Tapsin Dia", "Tomar después del desayuno", "2024-03-10", "08:00", true);
        Reminder takenReminder2 = new Reminder("Tapsin Noche", "Tomar antes de dormir", "2024-03-10", "22:00", true);
        Reminder takenReminder3 = new Reminder("Tapsin Dia", "Tomar después del desayuno", "2024-04-10", "08:00", true);
        Reminder takenReminder4 = new Reminder("Tapsin Noche", "Tomar antes de dormir", "2024-04-10", "22:00", true);

        takenReminders.add(takenReminder1);
        takenReminders.add(takenReminder2);
        takenReminders.add(takenReminder3);
        takenReminders.add(takenReminder4);

        takenReminderAdapter.notifyDataSetChanged();
    }


    private void initView() {
        medicationRemindersSlider = findViewById(R.id.viewpagerSlider);
        remindersRecyclerView = findViewById(R.id.view1);
        takenRemindersRecyclerView = findViewById(R.id.view2);

        reminderAdapter = new ReminderAdapter(reminderList);
        takenReminderAdapter = new ReminderAdapter(takenReminders);

        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        takenRemindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        remindersRecyclerView.setAdapter(reminderAdapter);
        takenRemindersRecyclerView.setAdapter(takenReminderAdapter);
    }

    private void setupSlider() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.wide1));
        sliderItems.add(new SliderItems(R.drawable.wide2));
        sliderItems.add(new SliderItems(R.drawable.wide3));

        medicationRemindersSlider.setAdapter(new SliderAdapters(sliderItems, medicationRemindersSlider));
        medicationRemindersSlider.setOffscreenPageLimit(3);
    }

    private void setupCreateReminderButton() {
        Button createReminderButton = findViewById(R.id.CreateReminder);
        createReminderButton.setOnClickListener(v -> showCreateReminderDialog());
    }

    private void handleReminderBroadcast() {
        reminderReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Logic to update UI based on reminder broadcast received
            }
        };

        IntentFilter filter = new IntentFilter(ACTION_REMINDER_TAKEN);
        registerReceiver(reminderReceiver, filter);
    }

    private void moveReminderToTaken(String title) {
        for (int i = 0; i < reminderList.size(); i++) {
            Reminder reminder = reminderList.get(i);
            if (reminder.getTitle().equals(title)) {
                reminderList.remove(i);
                reminder.setTaken(true);
                takenReminders.add(reminder);
                reminderAdapter.notifyDataSetChanged();
                takenReminderAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private BroadcastReceiver updateStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_UPDATE_REMINDER_STATUS.equals(intent.getAction())) {
                String title = intent.getStringExtra("title");
                moveReminderToTaken(title);
            }
        }
    };

    private void showCreateReminderDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_reminder);

        EditText titleEditText = dialog.findViewById(R.id.editTextReminderTitle);
        EditText descriptionEditText = dialog.findViewById(R.id.editTextReminderDescription);
        EditText dateEditText = dialog.findViewById(R.id.editTextDate);
        EditText timeEditText = dialog.findViewById(R.id.editTextTime);
        Button saveButton = dialog.findViewById(R.id.buttonSaveReminder);
        ProgressBar loadingIndicator = findViewById(R.id.progressBar1);

        final Calendar calendar = Calendar.getInstance();
        setupDatePicker(dateEditText, calendar);
        setupTimePicker(timeEditText, calendar);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString();
            String description = descriptionEditText.getText().toString();
            String date = dateEditText.getText().toString();
            String time = timeEditText.getText().toString();

            Reminder newReminder = new Reminder(title, description, date, time, true);
            reminderList.add(newReminder);
            reminderAdapter.notifyItemInserted(reminderList.size() - 1);
            scheduleNotification(calendar, title, description); // Schedule notification

            loadingIndicator.setVisibility(View.GONE);

            Toast.makeText(MainActivity.this, "Reminder saved!", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        });

        dialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(Calendar calendar, String title, String description) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("description", description);

        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void setupDatePicker(EditText dateEditText, Calendar calendar) {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            @SuppressLint("DefaultLocale") String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
            dateEditText.setText(selectedDate);
        };

        dateEditText.setOnClickListener(view -> new DatePickerDialog(
                MainActivity.this, dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void setupTimePicker(EditText timeEditText, Calendar calendar) {
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            @SuppressLint("DefaultLocale") String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            timeEditText.setText(selectedTime);
        };

        timeEditText.setOnClickListener(view -> new TimePickerDialog(
                MainActivity.this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                false).show());
    }

    private void setupRemindersRecyclerView() {
        remindersRecyclerView = findViewById(R.id.view1);
        takenRemindersRecyclerView = findViewById(R.id.view2);

        reminderAdapter = new ReminderAdapter(reminderList);
        takenReminderAdapter = new ReminderAdapter(takenReminders);

        remindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        takenRemindersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        remindersRecyclerView.setAdapter(reminderAdapter);
        takenRemindersRecyclerView.setAdapter(takenReminderAdapter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateStatusReceiver);
        if (reminderReceiver != null) {
            unregisterReceiver(reminderReceiver);
        }
    }



    private final Runnable slideRunnable = () -> medicationRemindersSlider.setCurrentItem(medicationRemindersSlider.getCurrentItem() + 1);
}

package proyecto_pastilla.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import proyecto_pastilla.Domain.Reminder;
import com.example.proyecto_pastilla.R;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {
    private List<Reminder> reminders;

    public ReminderAdapter(List<Reminder> reminders) {
        this.reminders = reminders;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminders.get(position);
        holder.bind(reminder);
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        private TextView dateAndTimeText;
        private TextView titleAndDescriptionText;

        ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateAndTimeText = itemView.findViewById(R.id.dateAndTimeText);
            titleAndDescriptionText = itemView.findViewById(R.id.titleAndDescriptionText);
        }

        void bind(Reminder reminder) {
            String dateAndTime = reminder.getDate() + " | " + reminder.getTime();
            String titleAndDescription = reminder.getTitle() + " | " + reminder.getDescription();

            dateAndTimeText.setText(dateAndTime);
            titleAndDescriptionText.setText(titleAndDescription);
        }
    }
}

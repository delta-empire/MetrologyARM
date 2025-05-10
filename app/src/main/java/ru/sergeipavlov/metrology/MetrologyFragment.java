package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Arrays;
import java.util.List;

public class MetrologyFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_metrology, container, false);

        // Список метрологического оборудования
        List<String> equipmentList = Arrays.asList(
                "Эталонные манометры",
                "Калибраторы температуры",
                "Поверочные установки",
                "Эталонные весы",
                "Измерительные мосты"
        );

        // Настройка RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.metrology_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new MetrologyAdapter(equipmentList, position -> {
            // Обработка выбора оборудования
            String selectedItem = equipmentList.get(position);
            showEquipmentDetails(selectedItem);
        }));

        return view;
    }

    private void showEquipmentDetails(String equipmentName) {
        // Здесь можно открыть детальный фрагмент или Activity
        Toast.makeText(getContext(), "Выбрано: " + equipmentName, Toast.LENGTH_SHORT).show();

        // Пример перехода на детальный фрагмент:
        /*
        EquipmentDetailFragment fragment = EquipmentDetailFragment.newInstance(equipmentName);
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
        */
    }

    // Адаптер для списка оборудования
    private static class MetrologyAdapter extends RecyclerView.Adapter<MetrologyAdapter.ViewHolder> {

        private final List<String> items;
        private final OnItemClickListener listener;

        interface OnItemClickListener {
            void onItemClick(int position);
        }

        MetrologyAdapter(List<String> items, OnItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_metrology, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position), position, listener);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.item_title);
            }

            void bind(String item, int position, OnItemClickListener listener) {
                textView.setText(item);
                itemView.setOnClickListener(v -> listener.onItemClick(position));
            }
        }
    }
}

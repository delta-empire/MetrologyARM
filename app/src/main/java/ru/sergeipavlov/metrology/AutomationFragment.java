package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AutomationFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_automation, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.automation_list);

        List<String> items = Arrays.asList(
                "Шкала-сигнал",
                "Датчики давления",
                "Датчики расхода",
                "Датчики уровня"
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AutomationAdapter(items, position -> {
            // Заменяем текущий фрагмент
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ScaleSignalFragment())
                    .addToBackStack("scale_signal")  // Добавляем в back stack
                    .commit();
        }));

        return view;
    }
}

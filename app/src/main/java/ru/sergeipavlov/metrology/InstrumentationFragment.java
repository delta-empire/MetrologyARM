package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class InstrumentationFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instrumentation, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.sensors_list);
        List<String> categories = Arrays.asList(
                "Датчики температуры",
                "Датчики давления",
                "Датчики расхода",
                "Датчики уровня"
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SensorCategoryAdapter(categories));

        return view;
    }
}

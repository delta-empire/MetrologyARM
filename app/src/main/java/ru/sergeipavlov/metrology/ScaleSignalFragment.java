package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class ScaleSignalFragment extends Fragment {

        private Spinner spScaleType;
        private EditText etPhysicalValueStart, etPhysicalValueEnd;
        private EditText etUnifiedSignalStart, etUnifiedSignalEnd;
        private EditText etPhysicalValue, etUnifiedSignal;
        private boolean isCalculationInProgress = false;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_scale_signal, container, false);

            // Инициализация UI элементов
            initViews(view);
            setupSpinner();
            setupTextWatchers();

            return view;
        }

        private void initViews(View view) {
            spScaleType = view.findViewById(R.id.spScaleType);
            etPhysicalValueStart = view.findViewById(R.id.etPhysicalValueStart);
            etPhysicalValueEnd = view.findViewById(R.id.etPhysicalValueEnd);
            etUnifiedSignalStart = view.findViewById(R.id.etUnifiedSignalStart);
            etUnifiedSignalEnd = view.findViewById(R.id.etUnifiedSignalEnd);
            etPhysicalValue = view.findViewById(R.id.etPhysicalValue);
            etUnifiedSignal = view.findViewById(R.id.etUnifiedSignal);
            setDefaultValues();
        }

        private void setupSpinner() {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.scale_types,
                    android.R.layout.simple_spinner_item
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spScaleType.setAdapter(adapter);

            spScaleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    calculate();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        }

        private void setupTextWatchers() {
            TextWatcher textWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!isCalculationInProgress) {
                        calculate();
                    }
                }
            };

            etPhysicalValueStart.addTextChangedListener(textWatcher);
            etPhysicalValueEnd.addTextChangedListener(textWatcher);
            etUnifiedSignalStart.addTextChangedListener(textWatcher);
            etUnifiedSignalEnd.addTextChangedListener(textWatcher);
            etUnifiedSignal.addTextChangedListener(textWatcher);
            etPhysicalValue.addTextChangedListener(textWatcher);
        }

        private void setDefaultValues() {
            etPhysicalValueStart.setText("0");
            etPhysicalValueEnd.setText("100");
            etUnifiedSignalStart.setText("4");
            etUnifiedSignalEnd.setText("20");
            etPhysicalValue.setText("50");
            etUnifiedSignal.setText("12");
        }

        private void calculate() {
            isCalculationInProgress = true;

            try {
                // Получаем значения из полей ввода
                double physStart = parseDouble(etPhysicalValueStart.getText().toString());
                double physEnd = parseDouble(etPhysicalValueEnd.getText().toString());
                double signalStart = parseDouble(etUnifiedSignalStart.getText().toString());
                double signalEnd = parseDouble(etUnifiedSignalEnd.getText().toString());
                double physSignal = parseDouble(etPhysicalValue.getText().toString());
                double ubifiedSignal = parseDouble(etUnifiedSignal.getText().toString());
                String scaleType = spScaleType.getSelectedItem().toString();

                // Валидация значений
                if (physStart >= physEnd) {
                    Toast.makeText(getContext(), "Ошибка: начальное ≥ конечного", Toast.LENGTH_LONG).show();
                    return;
                }

                if (signalStart >= signalEnd) {
                    Toast.makeText(getContext(), "Ошибка: начальное ≥ конечного", Toast.LENGTH_LONG).show();
                    return;
                }

                if (etUnifiedSignal.hasFocus() | etPhysicalValueStart.hasFocus() | etPhysicalValueEnd.hasFocus() |
                etUnifiedSignalStart.hasFocus() | etUnifiedSignalEnd.hasFocus()) {
                    double result = calculateForPhys(
                            scaleType, physStart, physEnd, signalStart, signalEnd, ubifiedSignal);
                    String resultText = String.format(Locale.getDefault(),
                            "%.3f", result);
                    etPhysicalValue.setText(resultText);
                } else if (etPhysicalValue.hasFocus()) {
                    double result = calculateForUnified(
                            scaleType, physStart, physEnd, signalStart, signalEnd, physSignal);
                    String resultText = String.format(Locale.getDefault(),
                            "%.3f", result);
                    etUnifiedSignal.setText(resultText);
                }

            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Введите корректные числа", Toast.LENGTH_LONG).show();
            } finally {
                isCalculationInProgress = false;
            }
        }

        private double calculateForPhys(String scaleType,
                                             double scs, double sce,
                                             double sgs, double sge, double sgv) {
            switch (scaleType) {
                case "Линейная":
                    return ((sgv - sgs) / (sge - sgs)) * (sce - scs) + scs;

                case "Линейная-убывающая":
                    return ((sgv - sge) / (sgs - sge)) * (sce - scs) + scs;

                case "Квадратичная":
                    return Math.sqrt(((sgv - sgs) /  (sge - sgs)) * (sce - scs) + scs);

                case "Квадратичная-убывающая":
                    return Math.sqrt(((sgv - sge) /  (sgs - sge)) * (sce - scs) + scs);

                case "Корневая":
                    return Math.pow(((sgv - sgs) / (sge - sgs)),2) * (sce - scs) + scs;

                case "Корневая-убывающая":
                    return Math.pow(((sgv - sge) / (sgs - sge)),2) * (sce - scs) + scs;

                default:
                    return 0;
            }
        }

    private double calculateForUnified(String scaleType,
                                    double scs, double sce,
                                    double sgs, double sge, double scv) {
        switch (scaleType) {
            case "Линейная":
                return ((scv - scs) / (sce - scs)) * (sge - sgs) + sgs;

            case "Линейная-убывающая":
                return ((scv - scs) / (sce - scs)) * (sgs - sge) + sge;

            case "Квадратичная":
                return Math.pow(((scv - scs) / (sce - scs)), 2) * (sge - sgs) + sgs;

            case "Квадратичная-убывающая":
                return Math.pow(((scv - scs) / (sce - scs)), 2) * (sgs - sge) + sge;

            case "Корневая":
                return Math.sqrt(((scv - scs) / (sce - scs))) * (sge - sgs) + sgs;

            case "Корневая-убывающая":
                return Math.sqrt(((scv - scs) / (sce - scs))) * (sgs - sge) + sge;

            default:
                return 0;
        }
    }

        private double parseDouble(String value) throws NumberFormatException {
            if (value.isEmpty()) return 0;
            return Double.parseDouble(value);
        }
    }

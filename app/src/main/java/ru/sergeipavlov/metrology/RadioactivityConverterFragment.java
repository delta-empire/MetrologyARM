package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class RadioactivityConverterFragment extends Fragment {
    private EditText[] editTexts = new EditText[10];
    private boolean isUserInput = true;
    private boolean isFieldEmpty = false;

    private final double[] multipliers = {
            1,          // Бк
            1e3,        // кБк
            1e6,        // МБк
            1e9,        // ГБк
            3.7e4,      // мкКи
            3.7e7,      // мКи
            3.7e10,     // Ки
            1e4,        // rd
            1,          // dps
            60          // dpm
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radioactivity_converter, container, false);
        initViews(view);
        setupTextWatchers();
        return view;
    }

    private void initViews(View view) {
        editTexts[0] = view.findViewById(R.id.et_bq);
        editTexts[1] = view.findViewById(R.id.et_kbq);
        editTexts[2] = view.findViewById(R.id.et_mbq);
        editTexts[3] = view.findViewById(R.id.et_gbq);
        editTexts[4] = view.findViewById(R.id.et_mci);
        editTexts[5] = view.findViewById(R.id.et_mmc);
        editTexts[6] = view.findViewById(R.id.et_ci);
        editTexts[7] = view.findViewById(R.id.et_rd);
        editTexts[8] = view.findViewById(R.id.et_dps);
        editTexts[9] = view.findViewById(R.id.et_dpm);
    }

    private void setupTextWatchers() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUserInput || isFieldEmpty) return;

                // Проверка на пустое поле
                if (s.toString().trim().isEmpty()) {
                    isFieldEmpty = true;
                    clearAllFieldsExcept(-1); // -1 означает очистить все
                    isFieldEmpty = false;
                    return;
                }

                // Поиск активного поля
                int activeFieldIndex = getFocusedFieldIndex();
                if (activeFieldIndex == -1) return;

                try {
                    double value = Double.parseDouble(s.toString());
                    if (value < 0) {
                        showError("Значение не может быть отрицательным");
                        return;
                    }
                    convertRadioactivity(activeFieldIndex, value);
                } catch (NumberFormatException e) {
                    showError("Некорректное значение");
                }
            }
        };

        for (EditText editText : editTexts) {
            editText.addTextChangedListener(watcher);
        }
    }

    private int getFocusedFieldIndex() {
        for (int i = 0; i < editTexts.length; i++) {
            if (editTexts[i].isFocused()) {
                return i;
            }
        }
        return -1;
    }

    private void convertRadioactivity(int sourceIndex, double value) {
        isUserInput = false;

        double bq = value * multipliers[sourceIndex];

        // Проверка на переполнение
        if (Double.isInfinite(bq) || Double.isNaN(bq)) {
            showError("Слишком большое значение");
            isUserInput = true;
            return;
        }

        for (int i = 0; i < editTexts.length; i++) {
            if (i != sourceIndex) {
                double convertedValue = bq / multipliers[i];
                editTexts[i].setText(formatValue(convertedValue));
            }
        }

        isUserInput = true;
    }

    private void clearAllFieldsExcept(int excludedIndex) {
        isUserInput = false;
        for (int i = 0; i < editTexts.length; i++) {
            if (i != excludedIndex) {
                editTexts[i].setText("");
            }
        }
        isUserInput = true;
    }

    private String formatValue(double value) {
        // Форматирование с учетом порядка величины
        if (value >= 1e9 || (value > 0 && value <= 1e-9)) {
            return String.format(Locale.getDefault(), "%.4e", value);
        } else {
            return String.format(Locale.getDefault(), "%.6g", value);
        }
    }

    private void showError(String message) {
        // Здесь можно реализовать показ ошибки пользователю
        // Например, через Toast или TextInputLayout.setError()
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }

        // Очищаем все поля при ошибке
        isUserInput = false;
        clearAllFieldsExcept(-1);
        isUserInput = true;
    }
}
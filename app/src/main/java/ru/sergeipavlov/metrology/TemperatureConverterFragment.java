package ru.sergeipavlov.metrology;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import java.util.Locale;

public class TemperatureConverterFragment extends Fragment {
    private EditText etCelsius, etKelvin, etFahrenheit, etReaumur, etRankine;
    private boolean isUserInput = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_converter, container, false);

        // Инициализация всех полей ввода
        initViews(view);

        // Установка слушателей изменений
        setupTextWatchers();

        // Установка начального значения (0°C)
        setInitialTemperature(0.0);

        return view;
    }

    private void initViews(View view) {
        etCelsius = view.findViewById(R.id.et_celsius);
        etKelvin = view.findViewById(R.id.et_kelvin);
        etFahrenheit = view.findViewById(R.id.et_fahrenheit);
        etReaumur = view.findViewById(R.id.et_reaumur);
        etRankine = view.findViewById(R.id.et_rankine);
    }

    private void setupTextWatchers() {
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUserInput) return;

                // Определяем, какое поле было изменено
                EditText source = (EditText) getView().findFocus();
                if (source == null) return;

                try {
                    double inputValue = Double.parseDouble(source.getText().toString());
                    convertTemperature(source.getId(), inputValue);
                } catch (NumberFormatException e) {
                    // Очищаем все поля при некорректном вводе
                    if (s.length() > 0) {
                        clearAllFieldsExcept(source.getId());
                    }
                }
            }
        };

        etCelsius.addTextChangedListener(textWatcher);
        etKelvin.addTextChangedListener(textWatcher);
        etFahrenheit.addTextChangedListener(textWatcher);
        etReaumur.addTextChangedListener(textWatcher);
        etRankine.addTextChangedListener(textWatcher);
    }

    private void convertTemperature(int sourceId, double value) {
        isUserInput = false;

        double celsius = 0;
        int id = sourceId;

        if (id == R.id.et_celsius) {
            celsius = value;
        }
        else if (id == R.id.et_kelvin) {
            celsius = kelvinToCelsius(value);
        }
        else if (id == R.id.et_fahrenheit) {
            celsius = fahrenheitToCelsius(value);
        }
        else if (id == R.id.et_reaumur) {
            celsius = reaumurToCelsius(value);
        }
        else if (id == R.id.et_rankine) {
            celsius = rankineToCelsius(value);
        }

        updateAllFields(celsius);
        isUserInput = true;
    }

    private void updateAllFields(double celsius) {
        if (etCelsius.getText().length() == 0 || etCelsius != getView().findFocus()) {
            etCelsius.setText(formatTemperature(celsius));
        }
        etKelvin.setText(formatTemperature(celsiusToKelvin(celsius)));
        etFahrenheit.setText(formatTemperature(celsiusToFahrenheit(celsius)));
        etReaumur.setText(formatTemperature(celsiusToReaumur(celsius)));
        etRankine.setText(formatTemperature(celsiusToRankine(celsius)));
    }

    private void clearAllFieldsExcept(int excludedId) {
        if (excludedId != R.id.et_celsius) etCelsius.setText("");
        if (excludedId != R.id.et_kelvin) etKelvin.setText("");
        if (excludedId != R.id.et_fahrenheit) etFahrenheit.setText("");
        if (excludedId != R.id.et_reaumur) etReaumur.setText("");
        if (excludedId != R.id.et_rankine) etRankine.setText("");
    }

    private void setInitialTemperature(double celsius) {
        etCelsius.setText(formatTemperature(celsius));
        etKelvin.setText(formatTemperature(celsiusToKelvin(celsius)));
        etFahrenheit.setText(formatTemperature(celsiusToFahrenheit(celsius)));
        etReaumur.setText(formatTemperature(celsiusToReaumur(celsius)));
        etRankine.setText(formatTemperature(celsiusToRankine(celsius)));
    }

    // Методы конвертации
    private double celsiusToKelvin(double celsius) { return celsius + 273.15; }
    private double kelvinToCelsius(double kelvin) { return kelvin - 273.15; }

    private double celsiusToFahrenheit(double celsius) { return celsius * 9/5 + 32; }
    private double fahrenheitToCelsius(double fahrenheit) { return (fahrenheit - 32) * 5/9; }

    private double celsiusToReaumur(double celsius) { return celsius * 4/5; }
    private double reaumurToCelsius(double reaumur) { return reaumur * 5/4; }

    private double celsiusToRankine(double celsius) { return (celsius + 273.15) * 9/5; }
    private double rankineToCelsius(double rankine) { return (rankine - 491.67) * 5/9; }

    private String formatTemperature(double value) {
        return String.format(Locale.getDefault(), "%.2f", value);
    }
}

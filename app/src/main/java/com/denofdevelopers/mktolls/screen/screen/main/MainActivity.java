package com.denofdevelopers.mktolls.screen.screen.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.denofdevelopers.mktolls.R;
import com.denofdevelopers.mktolls.application.App;
import com.denofdevelopers.mktolls.common.BaseActivity;
import com.denofdevelopers.mktolls.model.CurrentLocation;
import com.denofdevelopers.mktolls.screen.screen.result.ResultActivity;
import com.denofdevelopers.mktolls.util.NetworkUtil;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends BaseActivity implements MainContract.View {

    @BindView(R.id.fromLocationEditText)
    EditText fromLocationEditText;
    @BindView(R.id.toLocationEditText)
    EditText toLocationEditText;
    @BindView(R.id.fromLocationCheckBox)
    CheckBox fromLocationCheckBox;
    @BindView(R.id.toLocationCheckBox)
    CheckBox toLocationCheckBox;
    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @Inject
    MainPresenter mainPresenter;

    public static void start(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSpinner();
        configureCheckBox();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void setupActivityComponent() {
        App.get(this).getAppComponent().plus(new MainModule(this)).inject(this);
    }

    private void configureCheckBox() {
        fromLocationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                getLocation(fromLocationCheckBox);
                toLocationCheckBox.setChecked(false);
            } else {
                fromLocationEditText.setText("");
                fromLocationEditText.setEnabled(true);
            }
        });

        toLocationCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                getLocation(toLocationCheckBox);
                fromLocationCheckBox.setChecked(false);
            } else {
                toLocationEditText.setText("");
                toLocationEditText.setEnabled(true);
            }
        });
    }

    private void getLocation(CheckBox checkBox) {
        if (!mainPresenter.hasPlayServices(true)) {
            return;
        }
        CurrentLocation currentLocation = mainPresenter.getLocation();
        if (currentLocation != null) {
            if (checkBox.getId() == fromLocationCheckBox.getId()) {
                fromLocationEditText.setText(currentLocation.getName());
                fromLocationEditText.setEnabled(false);
                toLocationEditText.setEnabled(true);
                toLocationCheckBox.setChecked(false);
            } else {
                toLocationEditText.setText(currentLocation.getName());
                toLocationEditText.setEnabled(false);
                fromLocationEditText.setEnabled(true);
                fromLocationCheckBox.setChecked(false);
            }
        } else {
            mainPresenter.checkPermissions();
            showAlertMessage(getString(R.string.check_if_location_is_enabled));
        }
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.category_array, R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        spinner.setAdapter(adapter);
    }

    @Override
    public void showAlertMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startNextActivity(String fromLocation, String toLocation) {
        ResultActivity.start(this, fromLocation, toLocation, spinner.getSelectedItem().toString());
    }

    @Override
    protected void onStop() {
        mainPresenter.dropView();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.connectApiClient();
        hideProgress();
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.calculate)
    public void calculateOnClick() {
        showProgress();
        if (!NetworkUtil.isConnected(this)) {
            showAlertMessage(getString(R.string.no_internet_connection));
            hideProgress();
        } else {
            String startLoc = fromLocationEditText.getText().toString();
            String endLoc = toLocationEditText.getText().toString();
            if (!TextUtils.isEmpty(startLoc) && !TextUtils.isEmpty(endLoc)) {
                mainPresenter.shouldStartNextActivity(fromLocationEditText.getText().toString(), toLocationEditText.getText().toString());
            } else {
                showAlertMessage(getString(R.string.start_end_filled));
                hideProgress();
            }
        }
    }
}

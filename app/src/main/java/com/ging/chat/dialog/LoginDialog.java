package com.ging.chat.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.ging.chat.R;
import com.ging.chat.api.APIService;
import com.ging.chat.api.ApiUtils;
import com.ging.chat.api.response.LoginResponse;
import com.ging.chat.model.AppModel;
import com.ging.chat.model.User;
import com.ging.chat.utils.ModelUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginDialog extends DialogFragment implements View.OnClickListener {

    private EditText edtAccount, edtPassword;

    private APIService apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiService = ApiUtils.getAPIService();
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtAccount = view.findViewById(R.id.edt_account);
        edtPassword = view.findViewById(R.id.edt_password);

        view.findViewById(R.id.btn_login).setOnClickListener(this);
        view.findViewById(R.id.btn_register).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;

            case R.id.btn_register:
                showRegisterDialog();
                break;
        }
    }

    private void login() {
        String account = edtAccount.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        apiService.login(account, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                LoginResponse loginResponse = response.body();
                if(loginResponse != null) {
                    if(loginResponse.getStatus() == 1) {
                        User user = loginResponse.getData();
                        ModelUtils.ofApp().get(AppModel.class).getUser().setValue(user);
                        dismiss();
                    }
                    else {
                        Toast.makeText(getContext(), loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void showRegisterDialog() {
        new RegisterDialog().show(getFragmentManager(), "RegisterDialog");
        dismiss();
    }
}

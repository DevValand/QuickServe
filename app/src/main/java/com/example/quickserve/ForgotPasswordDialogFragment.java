package com.example.quickserve;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_forgot_password, null);
        EditText emailEdit = view.findViewById(R.id.et_reset_email);

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Send", (dialog, which) -> {
                    String email = emailEdit.getText().toString().trim();
                    if (TextUtils.isEmpty(email)) {
                        Toast.makeText(requireContext(), "Enter email", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnSuccessListener(aVoid -> Toast.makeText(requireContext(), "Reset email sent.", Toast.LENGTH_LONG).show())
                            .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .setNegativeButton("Cancel", null)
                .create();
    }
}


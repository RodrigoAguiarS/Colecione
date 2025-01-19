package com.rodrigo.colecione.util;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {

    public static void deleteImage(String imageUrl, Context context, OnSuccessListener<Void> onSuccessListener) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            photoRef.delete().addOnSuccessListener(aVoid -> {
                Toast.makeText(context, "Imagem deletada com sucesso", Toast.LENGTH_SHORT).show();
                onSuccessListener.onSuccess(aVoid);
            }).addOnFailureListener(e -> Toast.makeText(context, "Erro ao deletar a imagem", Toast.LENGTH_SHORT).show());
        } else {
            onSuccessListener.onSuccess(null);
        }
    }
}
package com.example.storeandroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.storeandroid.category.CategoriesAdapter;
import com.example.storeandroid.category.CategoryCreateActivity;
import com.example.storeandroid.dto.category.CategoryItemDTO;
import com.example.storeandroid.service.ApplicationNetwork;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    CategoriesAdapter categoriesAdapter;
    RecyclerView rcSholom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rcSholom=findViewById(R.id.rcSholom);
        rcSholom.setHasFixedSize(true);
        rcSholom.setLayoutManager(new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false));
        rcSholom.setAdapter(new CategoriesAdapter(new ArrayList<>()));
        loadList();
    }

    private void loadList() {
        Log.d("Jon", "Hello");
        ApplicationNetwork
                .getInstance()
                .getCategoriesApi()
                .list()
                .enqueue(new Callback<List<CategoryItemDTO>>() {
                    @Override
                    public void onResponse(Call<List<CategoryItemDTO>> call, Response<List<CategoryItemDTO>> response) {
                        if(response.isSuccessful()) {
                            List<CategoryItemDTO> data = response.body();
                            categoriesAdapter = new CategoriesAdapter(data);
                            rcSholom.setAdapter(categoriesAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryItemDTO>> call, Throwable t) {
                    }
                });
    }
}
package com.example.storeandroid.category;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.storeandroid.BaseActivity;
import com.example.storeandroid.MainActivity;
import com.example.storeandroid.R;
import com.example.storeandroid.application.HomeApplication;
import com.example.storeandroid.dto.category.CategoryCreateDTO;
import com.example.storeandroid.dto.category.CategoryItemDTO;
import com.example.storeandroid.service.ApplicationNetwork;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryCreateActivity extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String filePath;
    private ImageView ivSelectImage;
    TextInputLayout tlCategoryName;
    TextInputLayout tlCategoryDescription;

    private final String TAG="Hello";
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_create);
        Log.d("Permision", Boolean.toString(isStoragePermissionGranted()));


        tlCategoryName = findViewById(R.id.tlCategoryName);
        tlCategoryDescription = findViewById(R.id.tlCategoryDescription);

        ivSelectImage = findViewById(R.id.ivSelectImage);
        String url = "https://cdn.pixabay.com/photo/2016/01/03/00/43/upload-1118929_1280.png";
        Glide
                .with(HomeApplication.getAppContext())
                .load(url)
                .apply(new RequestOptions().override(300))
                .into(ivSelectImage);
    }

    public void openGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    RequestBody toRequestBody (String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body ;
    }

    public void onClickCreateCategory(View view) {
        String name = tlCategoryName.getEditText().getText().toString().trim();
        String description = tlCategoryDescription.getEditText().getText().toString().trim();

        Map<String, RequestBody> map = new HashMap<>();
        map.put("name", toRequestBody(name));
        map.put("description", toRequestBody(description));
        MultipartBody.Part imagePart=null;
        if (filePath != null) {
            //File file = new File(filePath);
            //RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
            //map.put("image\"; filename=\"pp.png\"", fileBody);
            File imageFile = new File(filePath);
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
            //map.put("image", imagePart);
        }
//        Log.d("salo", "---Name---"+name);
//        Log.d("salo", "---Image---"+image);
//        Log.d("salo", "---Description---"+description);
        try {
            ApplicationNetwork.getInstance()
                    .getCategoriesApi()
                    .create(map, imagePart)
                    .enqueue(new Callback<CategoryItemDTO>() {
                        @Override
                        public void onResponse(Call<CategoryItemDTO> call, Response<CategoryItemDTO> response) {
                            if (response.isSuccessful()) {
                                Intent intent = new Intent(CategoryCreateActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                try {
                                    String error = response.errorBody().string();
                                    int l = 12;
                                }catch (Exception ex) {
                                    Log.d("Error request server", "error");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CategoryItemDTO> call, Throwable t) {
                            Log.d("00000", "Jeck");
                        }
                    });
        } catch (Exception ex) {
            Log.d("Hello---->", ex.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the URI of the selected image
            Uri uri = data.getData();

            Glide
                    .with(HomeApplication.getAppContext())
                    .load(uri)
                    .apply(new RequestOptions().override(300))
                    .into(ivSelectImage);

            // If you want to get the file path from the URI, you can use the following code:
            filePath = getPathFromURI(uri);
        }
    }

    // This method converts the image URI to the direct file system path of the image file
    private String getPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }
}
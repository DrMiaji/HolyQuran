package com.efortshub.holyquran.activities.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.efortshub.holyquran.R;
import com.efortshub.holyquran.databases.HbSqliteOpenHelper;
import com.efortshub.holyquran.databinding.ActivityDownloadLocationBinding;
import com.efortshub.holyquran.models.DownloadPathDetails;
import com.efortshub.holyquran.utils.HbConst;
import com.efortshub.holyquran.utils.HbUtils;

import java.io.File;
public class DownloadLocationActivity extends AppCompatActivity {

    private static final String TAG = "hhhh";
    ActivityDownloadLocationBinding binding;

    RequestQueue queue = null;


    int oldTheme = R.style.Theme_HBWhiteLight;

    @Override
    protected void onResume() {
        super.onResume();
        if (oldTheme != HbUtils.getSavedTheme(this)) {
            recreate();
        }else {



        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        oldTheme = HbUtils.getSavedTheme(this);
        setTheme(oldTheme);
        super.onCreate(savedInstanceState);
        binding = ActivityDownloadLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.includeTitle.tvTitle.setText(getString(R.string.txt_download_path));
        binding.includeTitle.btnGoBack.setOnClickListener(view -> onBackPressed());

        loadDefaultPath();
       // loadPermissionRequest();
        initListener();


    }
/*

    private boolean loadPermissionRequest() {
        if (ContextCompat.checkSelfPermission(DownloadLocationActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(DownloadLocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ) {

            binding.llSecPermissionWarning.setVisibility(View.VISIBLE);
            ActivityCompat.requestPermissions(DownloadLocationActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    HbConst.REQUEST_CODE_SELECT_STORAGE_PERMISSION);
            return false;
        } else {
            binding.llSecPermissionWarning.setVisibility(View.GONE);
            return true;
        }
    }
*/

    private void initListener() {
        binding.btnSetAnotherPath.setOnClickListener(view -> {
     /*       if (loadPermissionRequest()) {
            }
*/
            Intent intent;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                intent.putExtra("android.content.extra.FANCY", true);
                intent.putExtra("android.content.extra.SHOW_FILESIZE", true);
                ActivityCompat.startActivityForResult(DownloadLocationActivity.this, intent, HbConst.REQUEST_CODE_SELECT_DOWNLOAD_PATH, null);
            }



        });
    }

    private void loadDefaultPath() {
        DownloadPathDetails dpd = HbUtils.getSavedDownloadPathDetails(DownloadLocationActivity.this);

        String path;
        if (dpd.isSystemAllocated()){
            File file = HbUtils.getSystemAllocatedDownloadDir(DownloadLocationActivity.this);
             path = file.getAbsolutePath();

        }else {
            path = dpd.getDocumentMainPathURi().toString();
        }

        binding.includeDefaultPath.tvLanguageName.setText(R.string.txt_current_path);

        if (path.contains("document/")){
            path = path.split("document/")[1];
        }

        binding.includeDefaultPath.tvTranslationName.setText(path);
        binding.includeDefaultPath.ivDownloadStatus.setImageResource(R.drawable.ic_baseline_snippet_folder_24);




    }
/*

    private void loadCustomPath() {
        DownloadPathDetails downloadPathDetails = HbUtils.getSavedDownloadPathDetails(this);

        if (!downloadPathDetails.isSystemAllocated()){

            binding.includeDefaultPath.tvLanguageName.setText(R.string.txt_current_path);
            binding.includeDefaultPath.tvTranslationName.setText(downloadPathDetails.getDocumentMainPathURi().getPath());
            binding.includeDefaultPath.ivDownloadStatus.setImageResource(R.drawable.ic_baseline_snippet_folder_24);







        }



    }
*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == HbConst.REQUEST_CODE_SELECT_DOWNLOAD_PATH) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    Log.d(TAG, "onActivityResult: " + uri);

                    if (uri != null) {
                        try {

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                getContentResolver().takePersistableUriPermission(uri,
                                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                            }

                            DocumentFile mainPath = HbUtils.getIntentDocumentDownloadDir(this, uri);

                            HbUtils.setSavedDownloadPathDetails(this, mainPath.getUri());

                            loadDefaultPath();





                        } catch (Exception e) {
                            e.printStackTrace();
                            new AlertDialog.Builder(DownloadLocationActivity.this)
                                    .setTitle(getString(R.string.txt_warning))
                                    .setMessage(e.getMessage())
                                    .setPositiveButton(R.string.txt_choose_another, (dialogInterface, i) -> {

                                        Intent intent;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                                            intent.putExtra("android.content.extra.SHOW_ADVANCED", true);
                                            intent.putExtra("android.content.extra.FANCY", true);
                                            intent.putExtra("android.content.extra.SHOW_FILESIZE", true);
                                            ActivityCompat.startActivityForResult(DownloadLocationActivity.this, intent, HbConst.REQUEST_CODE_SELECT_DOWNLOAD_PATH, null);
                                        }


                                    })
                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                    .create().show();
                        }
                    }

                }

            } else {
                Toast.makeText(DownloadLocationActivity.this, "Cancelled by user.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == HbConst.REQUEST_CODE_SELECT_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
               // binding.llSecPermissionWarning.setVisibility(View.GONE);


            } else {
                new AlertDialog.Builder(DownloadLocationActivity.this)
                        .setTitle(getString(R.string.txt_warning))
                        .setMessage(getString(R.string.txt_storage_permission_warning_desc))
                        .setPositiveButton("Enable Permission", (dialogInterface, i) -> {

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);

                        }).create().show();

            }
        }
    }

}
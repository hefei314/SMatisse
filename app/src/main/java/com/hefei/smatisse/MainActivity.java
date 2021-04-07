package com.hefei.smatisse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.Logger;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_CHOOSE = 101;

    @BindView(R.id.rv_multimedia)
    RecyclerView rvMultimedia;

    private MultimediaListAdapter multimediaListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkPermissions();

        multimediaListAdapter = new MultimediaListAdapter(this);
        rvMultimedia.setLayoutManager(new GridLayoutManager(this, 2));
        rvMultimedia.setAdapter(multimediaListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Logger.e("Uris: " + Matisse.obtainResult(data));
            Logger.e("Paths: " + Matisse.obtainPathResult(data));
            Logger.e("Use the selected photos with original: " + Matisse.obtainOriginalState(data));

            List<String> paths = Matisse.obtainPathResult(data);

            List<MultimediaListAdapter.MultimediaListBean> multimediaListBeanList = new ArrayList<>();
            for (String item : paths) {
                multimediaListBeanList.add(new MultimediaListAdapter.MultimediaListBean(getType(item), item));
            }
            multimediaListAdapter.setData(multimediaListBeanList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0) {
                for (int grantCode : grantResults) {
                    if (grantCode == PackageManager.PERMISSION_GRANTED) {

                    } else {
                        // 弹窗说明需要权限
                    }
                }
            }
        }
    }

    @OnClick({R.id.tv_only_image, R.id.tv_only_video, R.id.tv_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_only_image:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofImage())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, "com.hefei.smatisse.fileprovider", ""))
                        .imageEngine(new GlideEngine())
                        .theme(R.style.Matisse_Dracula)
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.tv_only_video:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofVideo())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, "com.hefei.smatisse.fileprovider", ""))
                        .imageEngine(new GlideEngine())
                        .theme(R.style.Matisse_Dracula)
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
            case R.id.tv_all:
                Matisse.from(MainActivity.this)
                        .choose(MimeType.ofAll())
                        .showSingleMediaType(true)
                        .countable(true)
                        .maxSelectable(9)
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                        .thumbnailScale(0.85f)
                        .capture(true)
                        .captureStrategy(new CaptureStrategy(true, "com.hefei.smatisse.fileprovider", ""))
                        .imageEngine(new GlideEngine())
                        .theme(R.style.Matisse_Dracula)
                        .forResult(REQUEST_CODE_CHOOSE);
                break;
        }
    }

    private int getType(String path) {
        return path.contains("mp4") ? 2 : 1;
    }

    /**
     * 权限申请
     */
    private void checkPermissions() {
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        List<String> mPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }

        if (mPermissionList.size() > 0) {
            ActivityCompat.requestPermissions(this, mPermissionList.toArray(new String[mPermissionList.size()]), 101);
        }
    }
}
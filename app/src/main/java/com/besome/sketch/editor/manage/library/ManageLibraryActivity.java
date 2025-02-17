package com.besome.sketch.editor.manage.library;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.besome.sketch.beans.ProjectLibraryBean;
import com.besome.sketch.editor.manage.library.admob.AdmobActivity;
import com.besome.sketch.editor.manage.library.admob.ManageAdmobActivity;
import com.besome.sketch.editor.manage.library.compat.ManageCompatActivity;
import com.besome.sketch.editor.manage.library.firebase.ManageFirebaseActivity;
import com.besome.sketch.editor.manage.library.googlemap.ManageGoogleMapActivity;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.sketchware.remod.R;

import java.lang.ref.WeakReference;

import a.a.a.MA;
import a.a.a.aB;
import a.a.a.jC;
import a.a.a.mB;
import mod.hey.studios.util.Helper;

public class ManageLibraryActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ADMOB_ACTIVITY = 234;
    private static final int REQUEST_CODE_APPCOMPAT_ACTIVITY = 231;
    private static final int REQUEST_CODE_FIREBASE_ACTIVITY = 230;
    private static final int REQUEST_CODE_GOOGLE_MAPS_ACTIVITY = 241;

    private String sc_id;
    private LinearLayout libraryItemLayout;

    private ProjectLibraryBean firebaseLibraryBean;
    private ProjectLibraryBean compatLibraryBean;
    private ProjectLibraryBean admobLibraryBean;
    private ProjectLibraryBean googleMapLibraryBean;

    private String originalFirebaseUseYn = "N";
    private String originalCompatUseYn = "N";
    private String originalAdmobUseYn = "N";
    private String originalGoogleMapUseYn = "N";

    private void addLibraryItem(ProjectLibraryBean libraryBean) {
        LibraryItemView libraryItemView = new LibraryItemView(this);
        libraryItemView.a(R.layout.manage_library_common_item);
        libraryItemView.setTag(libraryBean.libType);
        libraryItemView.setData(libraryBean);
        libraryItemView.setOnClickListener(this);
        libraryItemLayout.addView(libraryItemView);
    }

    private void toCompatActivity(ProjectLibraryBean compatLibraryBean, ProjectLibraryBean firebaseLibraryBean) {
        Intent intent = new Intent(getApplicationContext(), ManageCompatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("compat", compatLibraryBean);
        intent.putExtra("firebase", firebaseLibraryBean);
        startActivityForResult(intent, REQUEST_CODE_APPCOMPAT_ACTIVITY);
    }

    private void initializeLibrary(ProjectLibraryBean libraryBean) {
        switch (libraryBean.libType) {
            case ProjectLibraryBean.PROJECT_LIB_TYPE_FIREBASE:
                firebaseLibraryBean = libraryBean;
                break;

            case ProjectLibraryBean.PROJECT_LIB_TYPE_COMPAT:
                compatLibraryBean = libraryBean;
                break;

            case ProjectLibraryBean.PROJECT_LIB_TYPE_ADMOB:
                admobLibraryBean = libraryBean;
                break;

            case ProjectLibraryBean.PROJECT_LIB_TYPE_GOOGLE_MAP:
                googleMapLibraryBean = libraryBean;
                break;
        }

        for (int i = 0; i < libraryItemLayout.getChildCount(); i++) {
            LibraryItemView libraryItemView = (LibraryItemView) libraryItemLayout.getChildAt(i);
            if (libraryBean.libType == (Integer) libraryItemView.getTag()) {
                libraryItemView.setData(libraryBean);
            }
        }
    }

    private void toAdmobActivity(ProjectLibraryBean libraryBean) {
        Intent intent;
        if (!libraryBean.reserved1.isEmpty()) {
            intent = new Intent(getApplicationContext(), ManageAdmobActivity.class);
        } else {
            intent = new Intent(getApplicationContext(), AdmobActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("admob", libraryBean);
        startActivityForResult(intent, REQUEST_CODE_ADMOB_ACTIVITY);
    }

    private void toFirebaseActivity(ProjectLibraryBean libraryBean) {
        Intent intent = new Intent(getApplicationContext(), ManageFirebaseActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("firebase", libraryBean);
        startActivityForResult(intent, REQUEST_CODE_FIREBASE_ACTIVITY);
    }

    private void toGoogleMapActivity(ProjectLibraryBean libraryBean) {
        Intent intent = new Intent(getApplicationContext(), ManageGoogleMapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("google_map", libraryBean);
        startActivityForResult(intent, REQUEST_CODE_GOOGLE_MAPS_ACTIVITY);
    }

    private void saveLibraryConfiguration() {
        jC.c(sc_id).b(compatLibraryBean);
        jC.c(sc_id).c(firebaseLibraryBean);
        jC.c(sc_id).a(admobLibraryBean);
        jC.c(sc_id).d(googleMapLibraryBean);
        jC.c(sc_id).k();
        jC.b(sc_id).a(jC.c(sc_id));
        jC.a(sc_id).a(jC.b(sc_id));
        jC.a(sc_id).a(firebaseLibraryBean);
        jC.a(sc_id).a(admobLibraryBean, jC.b(sc_id));
        jC.a(sc_id).b(googleMapLibraryBean, jC.b(sc_id));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FIREBASE_ACTIVITY:
                    ProjectLibraryBean libraryBean = data.getParcelableExtra("firebase");
                    initializeLibrary(libraryBean);
                    if (libraryBean.useYn.equals("Y") && !compatLibraryBean.useYn.equals("Y")) {
                        libraryBean = compatLibraryBean;
                        libraryBean.useYn = "Y";
                        initializeLibrary(libraryBean);
                        showFirebaseNeedCompatDialog();
                    }
                    break;

                case REQUEST_CODE_APPCOMPAT_ACTIVITY:
                    initializeLibrary(data.getParcelableExtra("compat"));
                    break;

                case REQUEST_CODE_ADMOB_ACTIVITY:
                    initializeLibrary(data.getParcelableExtra("admob"));
                    break;

                case REQUEST_CODE_GOOGLE_MAPS_ACTIVITY:
                    initializeLibrary(data.getParcelableExtra("google_map"));
                    break;

                default:
            }
        }
    }

    @Override
    public void onBackPressed() {
        k();
        try {
            new Handler().postDelayed(() -> new SaveLibraryTask(this).execute(), 500L);
        } catch (Exception e) {
            e.printStackTrace();
            h();
        }
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            int vTag = (Integer) v.getTag();
            switch (vTag) {
                case ProjectLibraryBean.PROJECT_LIB_TYPE_FIREBASE:
                    toFirebaseActivity(firebaseLibraryBean);
                    break;

                case ProjectLibraryBean.PROJECT_LIB_TYPE_COMPAT:
                    toCompatActivity(compatLibraryBean, firebaseLibraryBean);
                    break;

                case ProjectLibraryBean.PROJECT_LIB_TYPE_ADMOB:
                    toAdmobActivity(admobLibraryBean);
                    break;

                case ProjectLibraryBean.PROJECT_LIB_TYPE_GOOGLE_MAP:
                    toGoogleMapActivity(googleMapLibraryBean);
                    break;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!super.j()) {
            finish();
        }

        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }

        setContentView(R.layout.manage_library);
        Toolbar toolbar = findViewById(R.id.toolbar);
        a(toolbar);
        findViewById(R.id.layout_main_logo).setVisibility(View.GONE);
        d().a(Helper.getResString(R.string.design_actionbar_title_library));
        d().e(true);
        d().d(true);
        toolbar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));
        libraryItemLayout = findViewById(R.id.contents);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (savedInstanceState == null) {
            compatLibraryBean = jC.c(sc_id).c();
            if (compatLibraryBean == null) {
                compatLibraryBean = new ProjectLibraryBean(ProjectLibraryBean.PROJECT_LIB_TYPE_COMPAT);
            }
            originalCompatUseYn = compatLibraryBean.useYn;

            firebaseLibraryBean = jC.c(sc_id).d();
            if (firebaseLibraryBean == null) {
                firebaseLibraryBean = new ProjectLibraryBean(ProjectLibraryBean.PROJECT_LIB_TYPE_FIREBASE);
            }
            originalFirebaseUseYn = firebaseLibraryBean.useYn;

            admobLibraryBean = jC.c(sc_id).b();
            if (admobLibraryBean == null) {
                admobLibraryBean = new ProjectLibraryBean(ProjectLibraryBean.PROJECT_LIB_TYPE_ADMOB);
            }
            originalAdmobUseYn = admobLibraryBean.useYn;

            googleMapLibraryBean = jC.c(sc_id).e();
            if (googleMapLibraryBean == null) {
                googleMapLibraryBean = new ProjectLibraryBean(ProjectLibraryBean.PROJECT_LIB_TYPE_GOOGLE_MAP);
            }
            originalGoogleMapUseYn = googleMapLibraryBean.useYn;
        } else {
            firebaseLibraryBean = savedInstanceState.getParcelable("firebase");
            originalFirebaseUseYn = savedInstanceState.getString("originalFirebaseUseYn");
            compatLibraryBean = savedInstanceState.getParcelable("compat");
            originalCompatUseYn = savedInstanceState.getString("originalCompatUseYn");
            admobLibraryBean = savedInstanceState.getParcelable("admob");
            originalAdmobUseYn = savedInstanceState.getString("originalAdmobUseYn");
            googleMapLibraryBean = savedInstanceState.getParcelable("google_map");
            originalGoogleMapUseYn = savedInstanceState.getString("originalGoogleMapUseYn");
        }

        addLibraryItem(compatLibraryBean);
        addLibraryItem(firebaseLibraryBean);
        addLibraryItem(admobLibraryBean);
        addLibraryItem(googleMapLibraryBean);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!super.j()) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        outState.putParcelable("firebase", firebaseLibraryBean);
        outState.putParcelable("compat", compatLibraryBean);
        outState.putParcelable("admob", admobLibraryBean);
        outState.putParcelable("google_map", googleMapLibraryBean);
        outState.putString("originalFirebaseUseYn", originalFirebaseUseYn);
        outState.putString("originalCompatUseYn", originalCompatUseYn);
        outState.putString("originalAdmobUseYn", originalAdmobUseYn);
        outState.putString("originalGoogleMapUseYn", originalGoogleMapUseYn);
        super.onSaveInstanceState(outState);
    }

    private void showFirebaseNeedCompatDialog() {
        aB dialog = new aB(this);
        dialog.a(R.drawable.widget_firebase);
        dialog.b(Helper.getResString(R.string.common_word_warning));
        dialog.a(Helper.getResString(R.string.design_library_firebase_message_need_compat));
        dialog.b(Helper.getResString(R.string.common_word_ok), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private static class SaveLibraryTask extends MA {

        private final WeakReference<ManageLibraryActivity> activity;

        public SaveLibraryTask(ManageLibraryActivity activity) {
            super(activity);
            this.activity = new WeakReference<>(activity);
            activity.a(this);
        }

        @Override
        public void a() {
            activity.get().h();
            Intent intent = new Intent();
            intent.putExtra("sc_id", activity.get().sc_id);
            intent.putExtra("firebase", activity.get().firebaseLibraryBean);
            intent.putExtra("compat", activity.get().compatLibraryBean);
            intent.putExtra("admob", activity.get().admobLibraryBean);
            intent.putExtra("google_map", activity.get().googleMapLibraryBean);
            activity.get().setResult(RESULT_OK, intent);
            activity.get().finish();
        }

        @Override
        public void a(String idk) {
            activity.get().h();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return a(voids);
        }

        @Override
        public void b() {
            try {
                activity.get().saveLibraryConfiguration();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package com.appsan.santhoshadigau.transf_it;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.appsan.santhoshadigau.transf_it.receiver.TRANSFItReceiverActivity;
import com.appsan.santhoshadigau.transf_it.sender.TRANSFItActivity;
import com.appsan.santhoshadigau.transf_it.sender.TRANSFItService;
import com.appsan.santhoshadigau.transf_it.utils.Utils;
import com.appsan.santhoshadigau.transf_it.utils.HotspotControl;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    FilePickerDialog dialog;
    Button about;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        about =(Button) findViewById(R.id.btn_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("TRANSF-It");

        about.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                aboutDialog();
            }
        });

        }

    public void sendFiles(View view) {
        if (Utils.isShareServiceRunning(getApplicationContext())) {
            startActivity(new Intent(getApplicationContext(), TRANSFItActivity.class));
            return;
        }
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.MULTI_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(DialogConfigs.DEFAULT_DIR);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = null;

        dialog = new FilePickerDialog(this, properties);
        dialog.setTitle("Select files to share");

        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                if (null == files || files.length == 0) {
                    Toast.makeText(MainActivity.this, "Select at least one file to start Share Mode", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), TRANSFItActivity.class);
                intent.putExtra(TRANSFItService.EXTRA_FILE_PATHS, files);
                intent.putExtra(TRANSFItService.EXTRA_PORT, 52287);
                intent.putExtra(TRANSFItService.EXTRA_SENDER_NAME, "Sri");
                startActivity(intent);
            }
        });
        dialog.show();
    }

    public void receiveFiles(View view) {
        HotspotControl hotspotControl = HotspotControl.getInstance(getApplicationContext());
        if (null != hotspotControl && hotspotControl.isEnabled()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Sender(Hotspot) mode is active. Please disable it to proceed with Receiver mode");
            builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.show();
            return;
        }
        startActivity(new Intent(getApplicationContext(), TRANSFItReceiverActivity.class));
    }

    //Add this method to show Dialog when the required permission has been granted to the app.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (dialog != null) {   //Show dialog if the read permission has been granted.
                        dialog.show();
                    }
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this, "Permission is Required for getting list of files", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void aboutDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("About");
        WebView webView = new WebView(this);
        String about = "<p>1.0.0</p>" +
                "<p> Developed by AppSan Technologies</p>" +
                "<p>A lightweight, File Transfer app.</p>" +
                "<p>Developer <a href='mailto:sadiga80@gmail.com'>Santhosh Adiga U</a></p>" ;
        TypedArray ta = obtainStyledAttributes(new int[]{android.R.attr.textColorPrimary, R.attr.colorPrimaryDark});
        String textColor = String.format("#%06X", (0xFFFFFF & ta.getColor(0, Color.BLACK)));
        String accentColor = String.format("#%06X", (0xFFFFFF & ta.getColor(1, Color.BLUE)));
        ta.recycle();
        about = "<style media=\"screen\" type=\"text/css\">" +
                "body {\n" +
                "    color:" + textColor + ";\n" +
                "}\n" +
                "a:link {color:" + accentColor + "}\n" +
                "</style>" +
                about;
        webView.setBackgroundColor(Color.WHITE);
        webView.loadData(about, "text/html", "UTF-8");
        alert.setView(webView);
        alert.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }


}

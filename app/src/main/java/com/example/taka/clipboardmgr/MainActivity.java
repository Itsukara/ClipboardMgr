package com.example.taka.clipboardmgr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private int[] buttonPasteIDs = {R.id.buttonPaste01, R.id.buttonPaste02, R.id.buttonPaste03,
            R.id.buttonPaste04, R.id.buttonPaste05, R.id.buttonPaste06,
            R.id.buttonPaste07, R.id.buttonPaste08, R.id.buttonPaste09,
            R.id.buttonPaste10, R.id.buttonPaste11, R.id.buttonPaste12,
            R.id.buttonPaste13, R.id.buttonPaste14, R.id.buttonPaste15 };

    private int[] buttonCopyIDs = {R.id.buttonCopy01, R.id.buttonCopy02, R.id.buttonCopy03,
            R.id.buttonCopy04, R.id.buttonCopy05, R.id.buttonCopy06,
            R.id.buttonCopy07, R.id.buttonCopy08, R.id.buttonCopy09,
            R.id.buttonCopy10, R.id.buttonCopy11, R.id.buttonCopy12,
            R.id.buttonCopy13, R.id.buttonCopy14, R.id.buttonCopy15 };

    private int[] editTextIDs = {R.id.editText01, R.id.editText02, R.id.editText03,
            R.id.editText04, R.id.editText05, R.id.editText06,
            R.id.editText07, R.id.editText08, R.id.editText09,
            R.id.editText10, R.id.editText11, R.id.editText12,
            R.id.editText13, R.id.editText14, R.id.editText15 };

    private ClipboardManager clipboard;
    SharedPreferences pref;
    EditText editTextClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextClipboard = (EditText)findViewById(R.id.editTextClipboard);
        if (editTextClipboard == null) {
            Log.d("Error", "editTextClipboard == null");
            return;
        }
        clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        for (int i = 0; i < buttonPasteIDs.length; i++) {
            Button buttonPaste = (Button)findViewById(buttonPasteIDs[i]);
            Button buttonCopy  = (Button)findViewById(buttonCopyIDs[i]);
            EditText editText = (EditText)findViewById(editTextIDs[i]);
            if (buttonPaste == null || buttonCopy == null || editText == null) {
                Log.d("Error", "buttonPaste == null || buttonCopy == null || editText == null");
                continue;
            }
            buttonPaste.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = buttonIndex(buttonPasteIDs, (Button)v);
                    if (index < 0) {
                        Log.d("Error", "index < 0");
                        return;
                    }
                    EditText editText = (EditText)findViewById(editTextIDs[index]);
                    if (editText == null) {
                        Log.d("Error", "editText == null");
                        return;
                    }
                    ClipData clip = clipboard.getPrimaryClip();
                    String pasteString = "";
                    if (clip != null) {
                        ClipData.Item item = clip.getItemAt(0);
                        pasteString = item.getText().toString();
                    }
                    editText.setText(pasteString);
                }
            });

            buttonCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = buttonIndex(buttonCopyIDs, (Button)v);
                    if (index < 0) {
                        Log.d("Error", "index < 0");
                        return;
                    }
                    EditText editText = (EditText)findViewById(editTextIDs[index]);
                    if (editText == null) {
                        Log.d("Error", "editText == null");
                        return;
                    }
                    String copyString = editText.getText().toString();
                    ClipData clip = ClipData.newPlainText("copied_text", copyString);
                    clipboard.setPrimaryClip(clip);
                    editTextClipboard.setText(copyString);
                }
            });

        }
    }

    private int buttonIndex(int[] buttonIDs, Button button) {
        if (buttonIDs == null || button == null) {
            return -1;
        }
        int buttonID = button.getId();
        if (buttonID <= 0) {
            return -1;
        }
        for (int i = 0; i < buttonIDs.length; i++) {
            if (buttonID == buttonIDs[i]) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor editor = pref.edit();
        for (int i = 0; i < editTextIDs.length; i++) {
            EditText editText = (EditText)findViewById(editTextIDs[i]);
            if (editText == null) {
                Log.d("Error", "editText == null");
                continue;
            }
            String clipString = editText.getText().toString();
            editor.putString("clip"+String.format("%02d", i), clipString);
        }
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        pref = getSharedPreferences("clipboard_strings", Context.MODE_PRIVATE);
        for (int i = 0; i < editTextIDs.length; i++) {
            EditText editText = (EditText) findViewById(editTextIDs[i]);
            if (editText == null) {
                Log.d("Error", "editText == null");
                continue;
            }
            String clipString = pref.getString("clip" + String.format("%02d", i), "");
            editText.setText(clipString);
        }

        ClipData clip = clipboard.getPrimaryClip();
        String pasteString = "";
        if (clip != null) {
            ClipData.Item item = clip.getItemAt(0);
            pasteString = item.getText().toString();
        }
        editTextClipboard.setText(pasteString);
    }
}

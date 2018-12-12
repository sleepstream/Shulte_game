package georgearistov.shulte_game;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.Objects;

public class ConnectWithDev extends AppCompatActivity {

    private static final int MIN_MESSAGE_LENGTH = 30;
    private static final int PICK_INVOICE_FROM_IMAGE = 1000;
    public ImageButton sendButton;
    public TextInputLayout message_text_layout;
    public EditText message_text;
    private Context context;
    public Spinner spinner;
    public ImageButton attacheImage;
    public String attachment ="";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE= 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_connect_with_dev);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.contact_with_dev_themes_list));

        namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(namesAdapter);

        sendButton = findViewById(R.id.sendButton);
        message_text_layout = findViewById(R.id.message_text_layout);
        message_text = findViewById(R.id.message_text);
        attacheImage = findViewById(R.id.attacheImage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = message_text.getText().toString();
                String theme = spinner.getSelectedItem().toString();
                if(message.length()<MIN_MESSAGE_LENGTH) {
                    message_text_layout.setError(context.getString(R.string.message_send_error) + " " + MIN_MESSAGE_LENGTH + " " + context.getString(R.string.symbols));
                    message_text_layout.setErrorEnabled(true);
                }
                else
                {
                    message_text_layout.setError(null);
                    message_text_layout.setErrorEnabled(false);

                    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                    emailIntent.setType("text/plain");
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{  "aristovga@gmail.com"});

                    Log.d("test", attachment);
                    if(attachment.length()>0) {
                        File filelocation = new File(attachment);
                        Uri path = Uri.fromFile(filelocation);
                        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    }
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, theme + " " + getPackageName());
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
                    emailIntent.setType("message/rfc822");

                    try {
                        startActivity(Intent.createChooser(emailIntent,
                                context.getString(R.string.select_app_for_email)));
                        finish();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context,
                                "No email clients installed.",
                                Toast.LENGTH_SHORT).show();
                    }

                }



            }
        });

        attacheImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(ConnectWithDev.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                    // app-defined int constant that should be quite unique

                    return;
                }

                Intent intent = new Intent();
                intent.setType("*/*");
                String[] mimetypes = {"image/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                intent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_INVOICE_FROM_IMAGE);
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case PICK_INVOICE_FROM_IMAGE:

                if (data != null) {
                    attachment = getRealPathFromURI_API19(context, data.getData());
                }
                break;
        }
    }

    public static String getRealPathFromURI_API19(Context context, Uri uri) {
        if (isExternalStorageDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            if ("primary".equalsIgnoreCase(type)) {
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            }


        }
        // DownloadsProvider
        else if (isDownloadsDocument(uri)) {

            final String id = DocumentsContract.getDocumentId(uri);
            final Uri contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            return getDataColumn(context, contentUri, null, null);
        }
        // MediaProvider
        else if (isMediaDocument(uri)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] split = docId.split(":");
            final String type = split[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            } else if ("audio".equals(type)) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[]{
                    split[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        else
        {

            return getDataColumn(context, uri, null, null);
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}

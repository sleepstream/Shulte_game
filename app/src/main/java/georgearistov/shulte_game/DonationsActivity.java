package georgearistov.shulte_game;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v4.app.*;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.billingclient.api.*;
import georgearistov.shulte_game.billing.row.SkuRowData;
import georgearistov.shulte_game.billing.row.SpinnerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonationsActivity extends AppCompatActivity implements  PurchasesUpdatedListener, ConsumeResponseListener {

    /**
     * Google
     */
    private static final String GOOGLE_PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApyYjwbwVyrWPoiVQ6bxvdCtHLzcf5tO/o0Ar6w4m5Rv04KxqJGV4H1gNO5y0jKnkxSj8VllGZiX6/Wtc+f+itz81JCmVlDnhxtiNTPEMdCRpcoEIPJ+6/l9yCV7VO02K0KgRH+E4BFIwOF6WEz7hEfBv4A3N2WuzT5dMPabFt9rPFL1r0ObtZLnxBpzQyHULp/Xh2eYH2ME7GFnPtd0DCn/080UINab7uPxLDreHWP4z6qdwr/LxUUXJEgtHvP5PMiRHR/Dt58EoWOKwLGQNe1DdmzA7GM+K+bUaZM/Kc7UcO4MOpbHSzdGWdcL13piBeGkqbt3T8N/oYP70H4BNWwIDAQAB";
    private static final String[] GOOGLE_CATALOG = new String[]{"donate"};
    public static final String LOG_TAG = "DonationsActivity" ;
    private static final String DIALOG_TAG = "Dialog_TAg";
    private int MY_PERMISSIONS_REQUEST_INTERNET = 1000;

    private View mScreenWait;
    private Spinner spinner;
    private List<SkuDetails> listForSpinner = new ArrayList<SkuDetails>();
    private Context context;
    private SpinnerAdapter adapter;
    private BillingClient mBillingClient;
    private boolean mIsServiceConnected =false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;

        if (ContextCompat.checkSelfPermission(DonationsActivity.this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSIONS_REQUEST_INTERNET);

        }
        setContentView(R.layout.donations_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        // Create and initialize BillingManager which talks to BillingLibrary

        mScreenWait = findViewById(R.id.screen_wait);
        spinner = findViewById(R.id.spinner);
        adapter = new SpinnerAdapter(this, android.R.layout.simple_spinner_item, listForSpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    // The billing client is ready. You can query purchases here.
                    mIsServiceConnected = true;
                    querySkuDetails();
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });


        findViewById(R.id.button_purchase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(spinner.getSelectedItemPosition()>-1) {
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSku(adapter.objects.get(spinner.getSelectedItemPosition()).getSku())
                            .setType(BillingClient.SkuType.INAPP) // SkuType.SUB for subscription
                            .build();
                    int responseCode = mBillingClient.launchBillingFlow((Activity) context, flowParams);
                }
            }
        });


        findViewById(R.id.button_purchase).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int color = ContextCompat.getColor(context, R.color.red_press);
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:{
                        ImageButton v = (ImageButton) view;
                        v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);//line changed
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().clearColorFilter();//line changed
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton v = (ImageButton) view;
                        v.getBackground().clearColorFilter();//line changed
                        v.invalidate();
                        break;
                    }
            }
                return false;
            }
        });
    }

    /**
     * Needed for Google Play In-app Billing. It uses startIntentSenderForResult(). The result is not propagated to
     * the Fragment like in startActivityForResult(). Thus we need to propagate manually to our Fragment.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.

    }






    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "Destroying helper.");

        super.onDestroy();
    }



    private void querySkuDetails() {
        long startTime = System.currentTimeMillis();

        Log.d(LOG_TAG, "querySkuDetails() got subscriptions and inApp SKU details lists for: "
                + (System.currentTimeMillis() - startTime) + "ms");

        if (context != null && !((Activity)context).isFinishing()) {
            final List<SkuRowData> dataList = new ArrayList<>();
            // Filling the list with all the data to render subscription rows
            final List<String> subscriptionsSkus = new ArrayList<>();
            subscriptionsSkus.add("donate");
            subscriptionsSkus.add("donate100");
            subscriptionsSkus.add("donate1000");
            final SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
            params.setSkusList(subscriptionsSkus).setType(BillingClient.SkuType.INAPP);
            mBillingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(int responseCode, List<SkuDetails> skuDetailsList) {

                            if (responseCode != BillingClient.BillingResponse.OK) {
                                Log.w(LOG_TAG, "Unsuccessful query for type: "  + ". Error code: " + responseCode);
                            } else if (skuDetailsList != null && skuDetailsList.size() > 0) {

                                for (SkuDetails details : skuDetailsList) {
                                    Log.i(LOG_TAG, "Adding sku: " + details);
                                    listForSpinner.add(details);
                                }
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        }
                    });
        }
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

    private void addSkuRows(final List<SkuRowData> inList, List<String> skusList,
                            final @BillingClient.SkuType String billingType, final Runnable executeWhenFinished) {


    }


    /**
     * Implement this method to get notifications for purchases updates. Both purchases initiated by
     * your app and the ones initiated by Play Store will be reported here.
     *
     * @param responseCode Response code of the update.
     * @param purchases    List of updated purchases if present.
     */
    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK
                && purchases != null) {
            String purchaseToken = "inapp:" + getPackageName() + ":android.test.purchased";
            mBillingClient.consumeAsync(purchaseToken, this);

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage(getString(R.string.thanks_to_user))
                    .setCancelable(false)
                    .setPositiveButton(context.getString(R.string.btnOk), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            android.app.AlertDialog alert = builder.create();
            alert.show();
        }
        else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
        }
        else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED)
        {}
        else {
            // Handle any other error codes.
        }
    }

    @Override
    public void onConsumeResponse(int responseCode, String purchaseToken) {

    }
}

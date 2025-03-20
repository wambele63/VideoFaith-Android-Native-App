package videos.religious.platform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class InAppBilling extends AppCompatActivity implements PurchasesUpdatedListener{
    private static final String TAG = "InAppBilling";
    static final String ITEM_SKU_ADREMOVAL = "premium_upgrade";

    private Button mBuyButton;
    private String mAdRemovalPrice;
    private SharedPreferences mSharedPreferences;
    private SkuDetails skuDetails;
    private BillingClient mBillingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);

        //creating billing client
        mBillingClient = BillingClient.newBuilder(InAppBilling.this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(final BillingResult billingResult) {
                int resultcode = billingResult.getResponseCode();
                if (resultcode == BillingClient.BillingResponseCode.OK) {
                    List<String> skuList = new ArrayList<>();
                    skuList.add(ITEM_SKU_ADREMOVAL);
                    SkuDetailsParams params = SkuDetailsParams.newBuilder().setSkusList(skuList)
                            .setType(BillingClient.SkuType.INAPP).build();

                    mBillingClient.querySkuDetailsAsync(params,
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult1, List skuDetailsList) {
                                    // Process the result.
                                    int responseCode = billingResult.getResponseCode();
                                    if (responseCode == BillingClient.BillingResponseCode.OK
                                            && skuDetailsList.size() != 0) {
                                            skuDetails = (SkuDetails) skuDetailsList.get(0);
                                            String sku = skuDetails.getSku();
                                            String price = skuDetails.getPrice();
                                            if (ITEM_SKU_ADREMOVAL.equals(sku)) {
                                                mAdRemovalPrice = price;
                                            }
                                        }
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                mBillingClient.startConnection(this);
            }
        });
        mBuyButton = findViewById(R.id.buttonupgrade);
            mBuyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetails)
                            .build();
                    int l= mBillingClient.launchBillingFlow(InAppBilling.this, flowParams).getResponseCode();
                    if(l==7){
                        Constants.PRO_USER = true;
                        SharedPreferences preferences = getSharedPreferences("channelInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor preeditor = preferences.edit();
                        preeditor.putBoolean("PRO_USER",true);
                        FirebaseFirestore.getInstance().collection("channels").document(Constants.myId)
                                .update("PRO USER",true);
                        }
                }
            });
    }
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> list) {

    }
    public void initiatePurchaseFlow(final SkuDetails skuDetails) {
         Runnable s = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Launching in-app purchase flow.");
                BillingFlowParams purchaseParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build();
                mBillingClient.launchBillingFlow(InAppBilling.this, purchaseParams);
            }
        };
         s.run();
    }
}
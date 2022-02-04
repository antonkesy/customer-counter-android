package com.antonkesy.customercounter.application.billing

import android.app.Activity
import android.util.Log
import com.android.billingclient.api.*

class BillingManager(private val activity: Activity) : PurchasesUpdatedListener, IDonateManager {

    private var billingClient: BillingClient? = null
    private var skuDetails: SkuDetails? = null

    init {
        setUpBillingClient()
    }

    private fun setUpBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(this)
            .enablePendingPurchases()
            .build()
        startConnection()
    }

    private fun startConnection() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.e("INAPP", "Setup Billing Done")
                    queryAvailableProducts()
                }
            }

            override fun onBillingServiceDisconnected() {}
        })
    }

    private fun queryAvailableProducts() {
        val skuList = ArrayList<String>()
        skuList.add("donate")
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            Log.e("INAPP", "billing client start")
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {
                for (skuDetails in skuDetailsList) {
                    this.skuDetails = skuDetails
                }
            }
        }
    }

    private fun donate() {
        skuDetails?.let {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient?.launchBillingFlow(activity, billingFlowParams)?.responseCode
        }
    }


    override fun onPurchasesUpdated(p0: BillingResult, p1: MutableList<Purchase>?) {
        //nothing
    }

    override fun startDonatePurchasePrompt() {
        donate()
    }
}
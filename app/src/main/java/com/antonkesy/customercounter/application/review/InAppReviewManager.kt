package com.antonkesy.customercounter.application.review

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory

class InAppReviewManager(private val activity: Activity) : IReviewManager {
    private val manager: ReviewManager = ReviewManagerFactory.create(activity)

    override fun requestReview() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                manager.launchReviewFlow(activity, task.result)
            }
        }
    }

}
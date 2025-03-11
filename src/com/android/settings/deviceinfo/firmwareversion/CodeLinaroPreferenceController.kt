/*
 * Copyright (C) 2025 FortuneOS
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.deviceinfo.firmwareversion

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.os.SystemProperties
import androidx.preference.Preference
import com.android.settings.core.BasePreferenceController
import com.android.settingslib.DeviceInfoUtils

class CodeLinaroPreferenceController(
    context: Context,
    key: String
) : BasePreferenceController(context, key) {

    companion object {
        private const val CLO_TAG = "org.fortune.clo.revision"
        private const val TAG = "SecurityPatchCtrl"
        private val INTENT_URI_DATA: Uri = Uri.parse("https://www.codelinaro.org/")
    }

    private val packageManager: PackageManager = mContext.packageManager
    private val currentPatch: String? = DeviceInfoUtils.getSecurityPatch()

    override fun getAvailabilityStatus(): Int {
        return if (!currentPatch.isNullOrEmpty()) AVAILABLE else CONDITIONALLY_UNAVAILABLE
    }

    override fun getSummary(): CharSequence {
        return SystemProperties.get(CLO_TAG, "")
    }

    override fun handlePreferenceTreeClick(preference: Preference): Boolean {
        if (preference.key != preferenceKey) {
            return false
        }

        val intent = Intent(Intent.ACTION_VIEW, INTENT_URI_DATA)
        if (packageManager.queryIntentActivities(intent, 0).isEmpty()) {
            Log.w(TAG, "queryIntentActivities() returns empty")
            return true
        }

        mContext.startActivity(intent)
        return true
    }
}

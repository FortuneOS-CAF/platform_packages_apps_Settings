/*
 * Copyright (C) 2024 FortuneOS
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.settings.deviceinfo;

import android.content.Context;
import android.os.SystemProperties;

import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;

public class FortuneVersionPreferenceController extends BasePreferenceController {

    private static final String FORTUNE_VERSION_PROP = "org.fortune.revision";
    private static final String FORTUNE_CODENAME_PROP = "org.fortune.codename";
    private static final String FORTUNE_DEVICE_PROP = "org.fortune.device";
    private static final String FORTUNE_BUILDTYPE_PROP = "org.fortune.build.type";

    private final Context mContext;

    public FortuneVersionPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE_UNSEARCHABLE;
    }

    @Override
    public CharSequence getSummary() {
        String version = SystemProperties.get(FORTUNE_VERSION_PROP,
                mContext.getString(R.string.device_info_default));
        String codename = SystemProperties.get(FORTUNE_CODENAME_PROP,
                mContext.getString(R.string.device_info_default));
        String device = SystemProperties.get(FORTUNE_DEVICE_PROP,
                mContext.getString(R.string.device_info_default));
        String buildType = SystemProperties.get(FORTUNE_BUILDTYPE_PROP,
                mContext.getString(R.string.device_info_default));

        // Example: 1.0 | Ulysses | topaz | COMMUNITY
        return version + " | " + codename + " | " + device + " | " + buildType;
    }
}

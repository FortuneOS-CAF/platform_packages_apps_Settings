/*
 * SPDX-FileCopyrightText: 2024 Paranoid Android
 * SPDX-License-Identifier: Apache-2.0
 */

package org.fortune.settings.display;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreferenceCompat;

import com.android.settings.R;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import com.android.settingslib.widget.TopIntroPreference;

import java.util.List;
import java.util.stream.Collectors;

/** Preference fragment used for switching refresh rate */
@SearchIndexable
public class RefreshRateSettings extends RadioButtonPickerFragment {

    private static final String TAG = "RefreshRateSettings";
    private static final String KEY_VRR_PREF = "refresh_rate_vrr";

    private Context mContext;
    private RefreshRateUtils mUtils;
    private SwitchPreferenceCompat mVrrSwitchPref;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mUtils = new RefreshRateUtils(context);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.refresh_rate_settings;
    }

    @Override
    protected void addStaticPreferences(PreferenceScreen screen) {
        mVrrSwitchPref = new SwitchPreferenceCompat(screen.getContext());
        mVrrSwitchPref.setKey(KEY_VRR_PREF);
        mVrrSwitchPref.setTitle(R.string.refresh_rate_vrr_title);
        mVrrSwitchPref.setSummary(R.string.refresh_rate_vrr_summary);
        mVrrSwitchPref.setOnPreferenceChangeListener((pref, newValue) -> {
            mUtils.setVrrEnabled((Boolean) newValue);
            return true;
        });
        screen.addPreference(mVrrSwitchPref);
        updateVrrPref();

        final FooterPreference footerPreference = new FooterPreference(screen.getContext());
        footerPreference.setTitle(R.string.refresh_rate_footer);
        footerPreference.setSelectable(false);
        footerPreference.setLayoutResource(
            com.android.settingslib.widget.preference.footer.R.layout.preference_footer);
        screen.addPreference(footerPreference);
    }

    @Override
    protected List<? extends CandidateInfo> getCandidates() {
        return mUtils.getRefreshRates().stream()
                .filter(r -> r >= RefreshRateUtils.DEFAULT_REFRESH_RATE)
                .map(RefreshRateCandidateInfo::new)
                .collect(Collectors.toList());
    }

    private void updateVrrPref() {
        if (mVrrSwitchPref == null) return;
        mVrrSwitchPref.setEnabled(mUtils.isVrrPossible());
        mVrrSwitchPref.setChecked(mUtils.isVrrEnabled());
    }

    @Override
    protected String getDefaultKey() {
        return String.valueOf(mUtils.getCurrentRefreshRate());
    }

    @Override
    protected boolean setDefaultKey(final String key) {
        final int refreshRate = Integer.parseInt(key);
        mUtils.setCurrentRefreshRate(refreshRate);
        updateVrrPref();
        return true;
    }

    @Override
    public int getMetricsCategory() {
        return -1;
    }

    private class RefreshRateCandidateInfo extends CandidateInfo {
        private final CharSequence mLabel;
        private final String mKey;

        RefreshRateCandidateInfo(Integer refreshRate) {
            super(true);
            mLabel = String.format("%d Hz", refreshRate.intValue());
            mKey = refreshRate.toString();
        }

        @Override
        public CharSequence loadLabel() {
            return mLabel;
        }

        @Override
        public Drawable loadIcon() {
            return null;
        }

        @Override
        public String getKey() {
            return mKey;
        }
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.refresh_rate_settings) {
                @Override
                protected boolean isPageSearchEnabled(Context context) {
                    return new RefreshRateUtils(context).isHighRefreshRateAvailable();
                }
            };
}

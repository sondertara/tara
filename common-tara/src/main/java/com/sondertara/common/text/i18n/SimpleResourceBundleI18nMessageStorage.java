package com.sondertara.common.text.i18n;

import com.sondertara.common.text.StringUtils;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class SimpleResourceBundleI18nMessageStorage extends AbstractResourceBundleI18nMessageStorage {
    private ResourceBundle bundle;

    public void setBundle(ResourceBundle bundle) {
        Objects.requireNonNull(bundle);
        this.bundle = bundle;
        setLocale(bundle.getLocale());
    }

    @Override
    protected String getMessageInternal(@Nullable final String basename, @Nullable final Locale locale, final @Nullable ClassLoader classLoader, @NonNull String key, Object... args) {
        String message = ResourceBundles.getString(bundle, key, args);
        if (StringUtils.isBlank(message) && getParent() != null) {
            message = getParent().getMessage(locale, classLoader, key, args);
        }
        return message;
    }

}

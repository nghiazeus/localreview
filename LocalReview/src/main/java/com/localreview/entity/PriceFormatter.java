package com.localreview.entity;

import java.text.DecimalFormat;
import java.math.BigDecimal;

public class PriceFormatter {
    public static String formatPrice(BigDecimal price) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price);
    }
}


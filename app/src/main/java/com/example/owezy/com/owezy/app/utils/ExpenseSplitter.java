package com.owezy.app.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ExpenseSplitter {

    /**
     * Split an amount equally among participants
     * 
     * @param amount           Total amount to split
     * @param participantCount Number of participants
     * @return List of individual shares (may have slight variations due to
     *         rounding)
     */
    public static List<Double> splitEqually(double amount, int participantCount) {
        List<Double> shares = new ArrayList<>();
        if (participantCount <= 0) {
            return shares;
        }

        // Use BigDecimal for precise calculation
        BigDecimal totalAmount = BigDecimal.valueOf(amount);
        BigDecimal count = BigDecimal.valueOf(participantCount);
        BigDecimal baseShare = totalAmount.divide(count, 2, RoundingMode.DOWN);

        // Calculate base shares
        BigDecimal distributedAmount = BigDecimal.ZERO;
        for (int i = 0; i < participantCount; i++) {
            shares.add(baseShare.doubleValue());
            distributedAmount = distributedAmount.add(baseShare);
        }

        // Distribute remaining cents to first participants
        BigDecimal remainder = totalAmount.subtract(distributedAmount);
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            shares.set(0, shares.get(0) + remainder.doubleValue());
        }

        return shares;
    }

    /**
     * Validate that custom splits sum to the total amount
     * 
     * @param amount       Total amount
     * @param customShares Custom share amounts
     * @return True if splits are valid
     */
    public static boolean validateCustomSplit(double amount, List<Double> customShares) {
        if (customShares == null || customShares.isEmpty()) {
            return false;
        }

        double sum = 0.0;
        for (Double share : customShares) {
            if (share == null || share < 0) {
                return false;
            }
            sum += share;
        }

        // Allow 0.01 tolerance for rounding errors
        return Math.abs(sum - amount) < 0.01;
    }

    /**
     * Round amount to 2 decimal places
     */
    public static double roundAmount(double amount) {
        return BigDecimal.valueOf(amount)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}

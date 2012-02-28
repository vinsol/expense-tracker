/**
 * Copyright (c) 2012 Vinayak Solutions Private Limited 
 * See the file license.txt for copying permission.
*/     


package com.vinsol.expensetracker.helpers;


public class StringProcessing {
	public String getStringDoubleDecimal(String totalAmountString) {
		if(totalAmountString == null || totalAmountString.equals("")) {
			return "?";
		}
		if (totalAmountString.contains("?")&& totalAmountString.length() > 1) {
			String temp = totalAmountString.substring(0,totalAmountString.length() - 2);
			Double mAmount = Double.parseDouble(temp);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);
			if (mAmount.toString().contains(".")) {
				if (mAmount.toString().charAt(mAmount.toString().length() - 3) == '.') {
					totalAmountString = mAmount.toString() + " ?";
				} else if (mAmount.toString().charAt(mAmount.toString().length() - 2) == '.') {
					totalAmountString = mAmount.toString() + "0 ?";
				}

			} else {
				totalAmountString = mAmount.toString() + ".00 ?";
			}
		} else if (!totalAmountString.contains("?")) {
			String temp = totalAmountString.substring(0,totalAmountString.length());
			Double mAmount = Double.parseDouble(temp);
			mAmount = (double) ((int) ((mAmount + 0.005) * 100.0) / 100.0);

			if (mAmount.toString().contains(".")) {
				if (mAmount.toString().charAt(mAmount.toString().length() - 3) == '.') {
					totalAmountString = mAmount.toString() + "";
				} else if (mAmount.toString().charAt(mAmount.toString().length() - 2) == '.') {
					totalAmountString = mAmount.toString() + "0";
				}

			} else {
				totalAmountString = mAmount.toString() + ".00";
			}
		}
		
		return totalAmountString;
	}
	
	public Double getAmount(String amount) {
		if(amount.contains("?")) {
			amount = (String) amount.subSequence(0, amount.length()-1);
			if(amount.trim().length() < 1) {
				return 0.0;
			}
		}
		return Double.parseDouble(amount);
	}
}

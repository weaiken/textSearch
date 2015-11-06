package com.weaiken.textSearch.util;

import android.text.TextUtils;

public class StringUtil {

	public static boolean isAllEnglish(String str){
		if(TextUtils.isEmpty(str)){
			return false;
		}
		return str.matches("^[a-zA-Z]{4,18}$");
	}
	
	/**
	 * 是否合法的手机号
	 * @description  
	 * @param qq
	 * @return
	 */
	public static boolean isLegalPhoneNum(String phoneNum){
		if(TextUtils.isEmpty(phoneNum)){
			return false;
		}
		
		return phoneNum.matches("^[1][3|4|5|7|8]+\\d{9}$");
	}
	
	
}

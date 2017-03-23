/*
 * Copyright 2011 爱知世元
 * Website:http://www.azsy.cn/
 * Email:info＠azsy.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package com.android.wako.net.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
 

public class Utils {
	/**
	 * 参数编码
	 * 
	 * @param
	 * @return
	 */
	public static String encode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20")
					.replace("*", "%2A").replace("%7E", "~")
					.replace("#", "%23");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * 参数反编码
	 * 
	 * @param s
	 * @return
	 */
	public static String decode(String s) {
		if (s == null) {
			return "";
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static String getCurrentTime(Date date,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
		String currentTime = sdf.format(date);
		return currentTime;
	}

	public static String getCurrentTime(long time,String format) {
		Date date = new Date(time);
		return getCurrentTime(date,format);
	}
	
	public static String getCurrentTime() {
		return getCurrentTime(new Date(),"yyyy-MM-dd  HH:mm:ss");
	}

	public static String parseTime(long time) {
		return getCurrentTime(time,"yyyy-MM-dd  HH:mm:ss");
	}
	
}


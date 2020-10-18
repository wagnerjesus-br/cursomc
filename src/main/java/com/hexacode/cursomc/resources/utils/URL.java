package com.hexacode.cursomc.resources.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class URL {

	public static String decodeUriParam(String param) {
		try {
			return URLDecoder.decode(param, "UTF-8");
			
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}
	
	public static List<Integer> decodeIntList(String text) {
		String[] vet = text.split(",");
		
		List<Integer> list = new ArrayList<>();
		
		for(int item = 0; item < vet.length; item++) {
			list.add(Integer.parseInt(vet[item]));
		}
		return list;
	}
	
}

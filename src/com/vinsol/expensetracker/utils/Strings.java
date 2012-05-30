package com.vinsol.expensetracker.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Strings {
	public static final String EMPTY = "";
	
	public static String InputStreamToString(InputStream inputStream){
    	InputStreamReader isr = new InputStreamReader(inputStream);  	// Input stream that translates bytes to characters
    	BufferedReader br = new BufferedReader(isr); 					// Buffered input character stream
    	String str;
    	StringBuilder output = new StringBuilder();
    	try {
			while((str = br.readLine())!= null){ output.append(str); }
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return output.toString();
	}

    public static String toString( final Object o ) {
        return toString(o,"");
    }

    public static String toString( final Object o, final String def ) {
        return o==null ? def : o.toString();
    }

    public static boolean isEmpty( final String s ) {
        return s==null || s.length()==0;
    }

    public static boolean notEmpty( final String s ) {
        return s!=null && s.length()!=0;
    }

    public static boolean equal( final String a, final String b ) {
        return a==b || ( a!=null && b!=null && a.equals(b) );
    }
}

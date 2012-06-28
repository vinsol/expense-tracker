package com.vinsol.confconnect.gson;

import java.lang.reflect.Field;

import com.google.gson.FieldNamingStrategy;
import com.vinsol.expensetracker.utils.Strings;

public class ExpenseTrackerFieldNamingPolicy implements FieldNamingStrategy{
	
	@Override
	public String translateName(Field f) {
		return getConvention(f.getName());
	}
	
	private String getConvention(String name) {
		if(Strings.equal(name, "id")) { return "_id";}
		if(Strings.equal(name, "favId")) { return "fav_id";}
		if(Strings.equal(name, "myHash")) { return "my_hash";}
		if(Strings.equal(name, "timeInMillis")) { return "time_milis_utc";}
		if(Strings.equal(name, "idFromServer")) { return "id";}
		if(Strings.equal(name, "type")) { return "expense_type";}
		if(Strings.equal(name, "updatedAt")) { return "updated_at";}
		if(Strings.equal(name, "fileToDownload")) { return "file_to_download";}
		if(Strings.equal(name, "deleted")) { return "delete_bit";}
		if(Strings.equal(name, "syncBit")) { return "sync_bit";}
		if(Strings.equal(name, "fileUploaded")) { return "file_uploaded";}
		if(Strings.equal(name, "fileUpdatedAt")) { return "file_updated_at";}
		if(Strings.equal(name, "encrypted_token")) { return "token";}
		return name;
	}

}

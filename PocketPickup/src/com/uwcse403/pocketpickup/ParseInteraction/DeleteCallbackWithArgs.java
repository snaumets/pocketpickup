package com.uwcse403.pocketpickup.ParseInteraction;

import java.util.List;

import com.parse.DeleteCallback;
import com.parse.ParseObject;

public abstract class DeleteCallbackWithArgs extends DeleteCallback {
	private List<ParseObject> parseObjectList;
	
	public DeleteCallbackWithArgs(List<ParseObject> parseObjectList) {
		this.parseObjectList = parseObjectList;
	}
	
	public List<ParseObject> getList() {
		return parseObjectList;
	}
}

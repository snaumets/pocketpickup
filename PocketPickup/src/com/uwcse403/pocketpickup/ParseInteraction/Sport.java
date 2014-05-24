package com.uwcse403.pocketpickup.ParseInteraction;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Sport")
public class Sport extends ParseObject {
	
	public Sport() { }

	/**
	 * Parse store additional data with the ParseObjects returned by queries
	 * so it is necessary to override hashCode() so that the comparison is 
	 * based only on data we can see. ParseObject.objectId is unique for 
	 * each object so simply hashing it is sufficient
	 */
	@Override
	public int hashCode() {
		return super.getObjectId().hashCode();
	}
	/**
	 * Two Sport objects are equal iff they have the same objectId.
	 */
	@Override
	public boolean equals(Object obj) {
		 if (obj == null)
	            return false;
	        if (obj == this)
	            return true;
	        if (!(obj instanceof Sport))
	            return false;
	        
	        Sport other = (Sport) obj;
	        return other.getObjectId().equals(super.getObjectId());
	}
	
}
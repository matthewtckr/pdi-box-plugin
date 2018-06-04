package org.pentaho.di.trans.steps.box;

import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField.Attribute;

import com.box.sdk.BoxUser;

public class BoxUsersInputData extends BoxData {

  public Object getValue( BoxUser.Info user, Attribute attribute ) {
    if ( user == null || attribute == null ) {
      return null;
    }
    switch ( attribute ) {
      case NAME:
        return user.getName();
      case LOGIN:
    	return user.getLogin();
      case STATUS:
    	return user.getStatus().toString();
      default:
        throw new RuntimeException( "Unknown Box User Attribute" );
    }
  }
}

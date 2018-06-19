package org.pentaho.di.trans.steps.box;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField.Attribute;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;

public class BoxUsersInputMetaTest {

  @Test
  public void testRoundTrip() throws KettleException {
    List<String> attributes =
      Arrays.asList( "ConfigFile", "UserFields" );

    Map<String, String> getters = Collections.emptyMap();
    Map<String, String> setters = Collections.emptyMap();
    Map<String, FieldLoadSaveValidator<?>> attributeValidators = Collections.emptyMap();
    Map<String, FieldLoadSaveValidator<?>> typeValidators = new HashMap<>();
    typeValidators.put( UserField[].class.getCanonicalName(), new ArrayLoadSaveValidator<UserField>( new UserFieldLoadSaveValidator(), 25 ) );
    LoadSaveTester tester =
      new LoadSaveTester( BoxUsersInputMeta.class, attributes, getters, setters, attributeValidators, typeValidators );
    tester.testXmlRoundTrip();
    tester.testRepoRoundTrip();
  }

  public static class UserFieldLoadSaveValidator implements FieldLoadSaveValidator<UserField> {

    @Override
    public UserField getTestObject() {
      Attribute[] values = Attribute.values();
      return new UserField(
        UUID.randomUUID().toString(),
        values[new Random().nextInt( values.length )] );
    }

    @Override
    public boolean validateTestObject(UserField testObject, Object actual ) {
      if ( !( actual instanceof UserField ) ) {
        return false;
      }
      UserField actualObject = (UserField) actual;
      return testObject.getName().equals( actualObject.getName() ) &&
        testObject.getType() == actualObject.getType();
    }
  }
}

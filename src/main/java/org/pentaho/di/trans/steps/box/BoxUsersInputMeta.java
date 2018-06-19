package org.pentaho.di.trans.steps.box;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField.Attribute;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(
    id = "BoxUsersInputMeta",
    image = "org/pentaho/di/trans/steps/box/box.png",
    i18nPackageName = "org.pentaho.di.trans.steps.box",
    name = "BoxUsersInputMeta.Name",
    description = "BoxUsersInputMeta.TooltipDesc",
    categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Input"
  )
public class BoxUsersInputMeta extends BoxMeta {

  private UserField[] userFields;

  public UserField[] getUserFields() {
    return userFields;
  }

  public void setUserFields( UserField[] userFields ) {
    this.userFields = userFields;
  }

  public void allocate ( int count ) {
    this.userFields = new UserField[count];
  }

  @Override
  public void setDefault() {
    super.setDefault();
    allocate( 0 );
  }

  @Override
  public void getFields( RowMetaInterface inputRowMeta, String name, RowMetaInterface[] info, StepMeta nextStep,
      VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    super.getFields( inputRowMeta, name, info, nextStep, space, repository, metaStore );
    if ( userFields != null ) {
      for ( int i = 0; i < userFields.length; i++ ) {
        addFieldToRow( inputRowMeta,
          space.environmentSubstitute( userFields[i].getName() ),
          userFields[i].getType().getValueMetaType() );
      }
    }
  }

  @Override
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
      TransMeta transMeta, Trans trans ) {
    return new BoxUsersInput( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public StepDataInterface getStepData() {
    return new BoxUsersInputData();
  }

  @Override
  public String getXML() throws KettleException {
    StringBuilder builder = new StringBuilder( super.getXML() );
    builder.append( "    " ).append( XMLHandler.openTag( "fields" ) ).append( Const.CR );
    for ( UserField field : userFields ) {
      builder.append( "      " ).append( XMLHandler.openTag( "field" ) ).append( Const.CR );
      builder.append( "        " ).append( XMLHandler.addTagValue( "name", field.getName() ) );
      builder.append( "        " ).append( XMLHandler.addTagValue( "type", field.getType().name() ) );
      builder.append( "      " ).append( XMLHandler.closeTag( "field" ) ).append( Const.CR );
    }
    builder.append( "    " ).append( XMLHandler.closeTag( "fields" ) ).append( Const.CR );
    return builder.toString();
  }

  @Override
  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    super.loadXML( stepnode, databases, metaStore );
    Node fieldsNode = XMLHandler.getSubNode( stepnode, "fields" );
    if ( fieldsNode != null ) {
      int fieldCount = XMLHandler.countNodes( fieldsNode, "field" );
      allocate( fieldCount );
      for ( int i = 0; i < fieldCount; i++ ) {
        Node fieldNode = XMLHandler.getSubNodeByNr( fieldsNode, "field", i );
        String name = XMLHandler.getTagValue( fieldNode, "name" );
        UserField.Attribute type = UserField.Attribute.valueOf( XMLHandler.getTagValue( fieldNode, "type" ) );
        if ( type != null ) {
          userFields[i] = new UserField( name, type );
        }
      }
    }
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    super.readRep( rep, metaStore, id_step, databases );
    int fieldCount = rep.countNrStepAttributes( id_step, "fieldName" );
    allocate( fieldCount );
    for ( int i = 0; i < fieldCount; i++ ) {
      String name = rep.getStepAttributeString( id_step, i, "fieldName" );
      Attribute type = Attribute.valueOf( rep.getStepAttributeString( id_step, i, "fieldType" ) );
      userFields[i] = new UserField( name, type );
    }
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    super.saveRep( rep, metaStore, id_transformation, id_step );
    for ( int i = 0; i < userFields.length; i++ ) {
      UserField field = userFields[i];
      rep.saveStepAttribute( id_transformation, id_step, i, "fieldName", field.getName() );
      rep.saveStepAttribute( id_transformation, id_step, i, "fieldType", field.getType().name() );
    }
  }

  public static class UserField {
    private String name;
    private Attribute type;

    public UserField( String name, Attribute type ) {
      this.name = name;
      this.type = type;
    }

    public String getName() {
      return name;
    }

    public void setName( String name ) {
      this.name = name;
    }

    public Attribute getType() {
      return type;
    }

    public void setType( Attribute type ) {
      this.type = type;
    }

    public enum Attribute {
      NAME( ValueMetaInterface.TYPE_STRING ) {
        @Override
        public String toString() {
          return "Name";
        }
      },
      LOGIN( ValueMetaInterface.TYPE_STRING ) {
        @Override
        public String toString() {
          return "Login";
        }
      },
      STATUS( ValueMetaInterface.TYPE_STRING ) {
        @Override
        public String toString() {
          return "Status";
        }
      };

      private final int dataType;

      private Attribute( int type ) {
        this.dataType = type;
      }

      public int getValueMetaType() {
        return this.dataType;
      }

      public static Attribute getEnumFromValue( String name ) {
        if ( Const.isEmpty( name ) ) {
          return null;
        }
        for ( Attribute value : Attribute.values() ) {
          if ( value.toString().equals( name ) ) {
            return value;
          }
        }
        return null;
      }
    };
  }
}

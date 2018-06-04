package org.pentaho.di.trans.steps.box;

import java.util.List;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

public abstract class BoxMeta extends BaseStepMeta implements StepMetaInterface {

  protected static Class<BoxMeta> PKG = BoxMeta.class;

  private String configFile;

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile( String configFile ) {
    this.configFile = configFile;
  }

  @Override
  public void setDefault() {
    this.configFile = "";
  }

  protected void addFieldToRow( RowMetaInterface row, String fieldName, int type ) throws KettleStepException {
    if ( !Const.isEmpty( fieldName ) ) {
      try {
        ValueMetaInterface value = ValueMetaFactory.createValueMeta( fieldName, type );
        value.setOrigin( getName() );
        row.addValueMeta( value );
      } catch ( KettlePluginException e ) {
        throw new KettleStepException( BaseMessages.getString( PKG,
          "TransExecutorMeta.ValueMetaInterfaceCreation", fieldName ), e );
      }
    }
  }

  @Override
  public String getXML() throws KettleException {
    StringBuilder builder = new StringBuilder();
    builder.append( "    " ).append( XMLHandler.addTagValue( "configFile", getConfigFile() ) );
    return builder.toString();
  }

  @Override
  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    setConfigFile( XMLHandler.getTagValue( stepnode, "configFile" ) );
  }

  @Override
  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases )
    throws KettleException {
    setConfigFile( rep.getStepAttributeString( id_step, "configFile" ) );
  }

  @Override
  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    rep.saveStepAttribute( id_transformation, id_step, "configFile", getConfigFile() );
  }
}

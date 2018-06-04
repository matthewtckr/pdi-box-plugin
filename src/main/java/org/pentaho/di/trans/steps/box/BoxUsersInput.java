package org.pentaho.di.trans.steps.box;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.box.sdk.BoxAPIException;
import com.box.sdk.BoxUser;

public class BoxUsersInput extends BoxStep {

  public BoxUsersInput( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
	      Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  BoxUsersInputMeta meta;
  BoxUsersInputData data;

  @Override
  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    if ( !super.init( smi, sdi ) ) {
      return false;
    }
    meta = (BoxUsersInputMeta) smi;
    data = (BoxUsersInputData) sdi;
    return true;
  }

  @Override
  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    if ( first ) {
      data.rowMeta = new RowMeta();
      meta.getFields( data.rowMeta, getStepname(), null, null, this, repository, metaStore );
    }
    Iterable<BoxUser.Info> users = null;
    try {
      users = data.conn.getUsers();
    } catch ( BoxAPIException e ) {
      logError( BaseMessages.getString( PKG, "BoxInput.Error.Generic", e ) );
      setErrors( 1L );
      setOutputDone();
      return false;
    }

    for ( BoxUser.Info user : users ) {
      Object[] outputRow = RowDataUtil.allocateRowData( data.rowMeta.size() );
      for ( int i = 0; i < meta.getUserFields().length; i++ ) {
        outputRow[i] = data.getValue( user, meta.getUserFields()[i].getType() );
      }
      putRow( data.rowMeta, outputRow );
      if ( isStopped() ) {
        break;
      }
    }

    setOutputDone();
    return false;
  }
}

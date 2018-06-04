package org.pentaho.di.trans.steps.box;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleFileException;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class BoxStep extends BaseStep implements StepInterface {

  public static Class<?> PKG = BoxStep.class;
  
  private BoxMeta meta;
  private BoxData data;

  public BoxStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
      Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  @Override
  public boolean init( StepMetaInterface smi, StepDataInterface sdi ) {
    if ( !super.init( smi, sdi ) ) {
      return false;
    }
    meta = (BoxMeta) smi;
    data = (BoxData) sdi;

    String configFile = environmentSubstitute( meta.getConfigFile() );

    if ( Const.isEmpty( configFile ) ) {
      logError( BaseMessages.getString( PKG, "BoxStep.Error.MissingConfigFile" ) );
      return false;
    }
    FileObject configObject = null;
	try {
		configObject = KettleVFS.getFileObject( configFile );
	} catch (KettleFileException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    Reader configReader = null;
	try {
		configReader = new InputStreamReader( configObject.getContent().getInputStream() );
	} catch (FileSystemException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    data.conn = new BoxFacade( this );
    try {
		data.conn.connect( configReader );
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    return data.conn.isConnected();
  }
}

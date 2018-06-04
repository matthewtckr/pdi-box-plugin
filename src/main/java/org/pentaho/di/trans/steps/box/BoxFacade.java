package org.pentaho.di.trans.steps.box;

import java.io.IOException;
import java.io.Reader;

import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.BaseStep;

import com.box.sdk.BoxConfig;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxUser;

public class BoxFacade {

  final protected LogChannelInterface log;
  final protected Class<?> PKG = BoxFacade.class;
  protected BoxDeveloperEditionAPIConnection client;

  public BoxFacade() {
    this( null );
  }

  public BoxFacade( BaseStep parent ) {
    if ( parent != null ) {
      log = parent.getLogChannel();
    } else {
      log = null;
    }
  }

  public void connect( Reader config ) throws IOException {
    if ( client == null ) {
      BoxConfig boxConfig = BoxConfig.readFrom( config );
      if ( log.isDebug() ) {
        log.logDebug( BaseMessages.getString( PKG, "BoxFacade.ConnectionStarting", boxConfig.getEnterpriseId() ) );
      }
      client = BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig);
      if ( log.isDetailed() ) {
        log.logDetailed( BaseMessages.getString( PKG, "BoxFacade.ConnectionStarted", boxConfig.getEnterpriseId() ) );
      }
    }
  }

  public boolean isConnected() {
    if ( client == null ) {
      return false;
    }
    if ( client.getLastRefresh() > 0 ) {
      return true;
    }
    return false;
  }

  public void close() {
    if ( client != null ) {
      if ( log.isDebug() ) {
        log.logDebug( BaseMessages.getString( PKG, "BoxFacade.ConnectionClosing" ) );
      }
      client.revokeToken();
      client = null;
      if ( log.isDetailed() ) {
        log.logDetailed( BaseMessages.getString( PKG, "BoxFacade.ConnectionClosed" ) );
      }
    }
  }

  public Iterable<BoxUser.Info> getUsers() {
	return BoxUser.getAllEnterpriseUsers( client );
  }
}

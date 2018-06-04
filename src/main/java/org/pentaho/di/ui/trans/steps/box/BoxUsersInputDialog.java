package org.pentaho.di.ui.trans.steps.box;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField;
import org.pentaho.di.trans.steps.box.BoxUsersInputMeta.UserField.Attribute;
import org.pentaho.di.ui.core.widget.ColumnInfo;
import org.pentaho.di.ui.core.widget.LabelTextVar;
import org.pentaho.di.ui.core.widget.TableView;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

public class BoxUsersInputDialog extends BaseStepDialog implements StepDialogInterface {

	 private static Class<?> PKG = BoxUsersInputMeta.class; // for i18n purposes, needed by Translator2!!
	 private BoxUsersInputMeta input;

	 private CTabFolder wTabFolder;
	 private CTabItem wGeneralTab, wFieldsTab;
	 private Composite wGeneralComp, wFieldsComp;

	 private LabelTextVar wConfigFile;

	 private TableView wFields;

	 public BoxUsersInputDialog( Shell parent, Object in, TransMeta tr, String sname ) {
	   super( parent, (StepMetaInterface) in, tr, sname );
	   input = (BoxUsersInputMeta) in;
	 }

	 public String open() {
	   Shell parent = getParent();
	   Display display = parent.getDisplay();

	   shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
	   props.setLook( shell );
	   setShellImage( shell, input );

	   ModifyListener lsMod = new ModifyListener() {
	     public void modifyText( ModifyEvent e ) {
	       input.setChanged();
	     }
	   };
	   changed = input.hasChanged();

	   FormLayout formLayout = new FormLayout();
	   formLayout.marginWidth = Const.FORM_MARGIN;
	   formLayout.marginHeight = Const.FORM_MARGIN;

	   shell.setLayout( formLayout );
	   shell.setText( BaseMessages.getString( PKG, "BoxUsersInputDialog.Shell.Title" ) );

	   int middle = props.getMiddlePct();
	   int margin = Const.MARGIN;

	   // Stepname line
	   wlStepname = new Label( shell, SWT.RIGHT );
	   wlStepname.setText( BaseMessages.getString( PKG, "BoxInputDialog.Stepname.Label" ) );
	   props.setLook( wlStepname );
	   fdlStepname = new FormData();
	   fdlStepname.left = new FormAttachment( 0, 0 );
	   fdlStepname.right = new FormAttachment( middle, -margin );
	   fdlStepname.top = new FormAttachment( 0, margin );
	   wlStepname.setLayoutData( fdlStepname );
	   wStepname = new Text( shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
	   wStepname.setText( stepname );
	   props.setLook( wStepname );
	   wStepname.addModifyListener( lsMod );
	   fdStepname = new FormData();
	   fdStepname.left = new FormAttachment( middle, 0 );
	   fdStepname.top = new FormAttachment( 0, margin );
	   fdStepname.right = new FormAttachment( 100, 0 );
	   wStepname.setLayoutData( fdStepname );

	   // The Tab Folders
	   wTabFolder = new CTabFolder( shell, SWT.BORDER );
	   props.setLook(  wTabFolder, Props.WIDGET_STYLE_TAB );

	   // ///////////////////////
	   // START OF GENERAL TAB //
	   // ///////////////////////

	   wGeneralTab = new CTabItem( wTabFolder, SWT.NONE );
	   wGeneralTab.setText( BaseMessages.getString( PKG, "BoxInputDialog.GeneralTab.TabItem" ) );

	   wGeneralComp = new Composite( wTabFolder, SWT.NONE );
	   props.setLook( wGeneralComp );

	   FormLayout generalLayout = new FormLayout();
	   generalLayout.marginWidth = margin;
	   generalLayout.marginHeight = margin;
	   wGeneralComp.setLayout( generalLayout );

	   // Config file
	   wConfigFile = new LabelTextVar( transMeta, wGeneralComp,
	     BaseMessages.getString( PKG, "BoxInputDialog.ConfigFile.Label" ),
	     BaseMessages.getString( PKG, "BoxInputDialog.ConfigFile.Tooltip" ) );
	   props.setLook( wConfigFile );
	   wConfigFile.addModifyListener( lsMod );
	   FormData fdConfigFile = new FormData();
	   fdConfigFile.left = new FormAttachment( 0, -margin );
	   fdConfigFile.top = new FormAttachment( wStepname, 2 * margin );
	   fdConfigFile.right = new FormAttachment( 100, -margin );
	   wConfigFile.setLayoutData( fdConfigFile );

	   FormData fdGeneralComp = new FormData();
	   fdGeneralComp.left = new FormAttachment( 0, 0 );
	   fdGeneralComp.top = new FormAttachment( 0, 0 );
	   fdGeneralComp.right = new FormAttachment( 100, 0 );
	   fdGeneralComp.bottom = new FormAttachment( 100, 0 );
	   wGeneralComp.setLayoutData( fdGeneralComp );

	   wGeneralComp.layout();
	   wGeneralTab.setControl( wGeneralComp );

	   // /////////////////////
	   // END OF GENERAL TAB //
	   // /////////////////////

	   // //////////////////////
	   // START OF SECTIONS TAB //
	   // //////////////////////

	   wFieldsTab = new CTabItem( wTabFolder, SWT.NONE );
	   wFieldsTab.setText( BaseMessages.getString( PKG, "BoxInputDialog.FieldsTab.TabItem" ) );

	   wFieldsComp = new Composite( wTabFolder, SWT.NONE );
	   props.setLook( wFieldsComp );

	   FormLayout fieldsLayout = new FormLayout();
	   fieldsLayout.marginWidth = margin;
	   fieldsLayout.marginHeight = margin;
	   wFieldsComp.setLayout( fieldsLayout );
	   
	   wGet = new Button( wFieldsComp, SWT.PUSH );
	   wGet.setText( BaseMessages.getString( PKG, "BoxInputDialog.GetFields.Button" ) );
	   fdGet = new FormData();
	   fdGet.left = new FormAttachment( 50, 0 );
	   fdGet.bottom = new FormAttachment( 100, 0 );
	   wGet.setLayoutData( fdGet );
	   
	   final int currentRows = input.getUserFields().length;
	   
	   ColumnInfo[] colinf =
	     new ColumnInfo[] {
	       new ColumnInfo(
	         BaseMessages.getString( PKG, "BoxInputDialog.FieldsTable.Name" ),
	         ColumnInfo.COLUMN_TYPE_TEXT, false ),
	       new ColumnInfo(
	         BaseMessages.getString( PKG, "BoxInputDialog.FieldsTable.Element" ),
	         ColumnInfo.COLUMN_TYPE_CCOMBO, getElementNames(), false ),
	     };
	   colinf[0].setUsingVariables( true );
	   colinf[0].setToolTip(
	     BaseMessages.getString( PKG, "BoxInputDialog.FieldsTable.Name.Tooltip" ) );
	   colinf[1].setToolTip(
	     BaseMessages.getString( PKG, "BoxInputDialog.FieldsTable.Element.Tooltip" ) );

	   wFields = new TableView( transMeta, wFieldsComp, SWT.FULL_SELECTION | SWT.MULTI, colinf, currentRows, lsMod, props );

	   FormData fdFields = new FormData();
	   fdFields.left = new FormAttachment( 0, 0 );
	   fdFields.top = new FormAttachment( 0, 0 );
	   fdFields.right = new FormAttachment( 100, 0 );
	   fdFields.bottom = new FormAttachment( wGet, -margin );
	   wFields.setLayoutData( fdFields );

	   FormData fdFieldsComp = new FormData();
	   fdFieldsComp.left = new FormAttachment( 0, 0 );
	   fdFieldsComp.top = new FormAttachment( 0, 0 );
	   fdFieldsComp.right = new FormAttachment( 100, 0 );
	   fdFieldsComp.bottom = new FormAttachment( 100, 0 );
	   wFieldsComp.setLayoutData( fdFieldsComp );

	   wFieldsComp.layout();
	   wFieldsTab.setControl( wFieldsComp );

	   // ////////////////////
	   // END OF GROUPS TAB //
	   // ////////////////////

	   FormData fdTabFolder = new FormData();
	   fdTabFolder.left = new FormAttachment( 0, 0 );
	   fdTabFolder.top = new FormAttachment( wStepname, margin );
	   fdTabFolder.right = new FormAttachment( 100, 0 );
	   fdTabFolder.bottom = new FormAttachment( 100, -50 );
	   wTabFolder.setLayoutData( fdTabFolder );

	   wTabFolder.setSelection( 0 );

	   // ////////////////////
	   // END OF TAB FOLDER //
	   // ////////////////////

	   // Some buttons
	   wOK = new Button( shell, SWT.PUSH );
	   wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
	   wCancel = new Button( shell, SWT.PUSH );
	   wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );

	   setButtonPositions( new Button[] { wOK, wCancel }, margin, wTabFolder );

	   // Add listeners
	   lsCancel = new Listener() {
	     public void handleEvent( Event e ) {
	       cancel();
	     }
	   };
	   lsOK = new Listener() {
	     public void handleEvent( Event e ) {
	       ok();
	     }
	   };
	   lsGet = new Listener() {
	     public void handleEvent( Event e ) {
	       getFields();
	     }
	   };

	   wCancel.addListener( SWT.Selection, lsCancel );
	   wOK.addListener( SWT.Selection, lsOK );
	   wGet.addListener( SWT.Selection, lsGet );

	   lsDef = new SelectionAdapter() {
	     public void widgetDefaultSelected( SelectionEvent e ) {
	       ok();
	     }
	   };

	   wStepname.addSelectionListener( lsDef );

	   // Detect X or ALT-F4 or something that kills this window...
	   shell.addShellListener( new ShellAdapter() {
	     public void shellClosed( ShellEvent e ) {
	       cancel();
	     }
	   } );

	   // Set the shell size, based upon previous time...
	   setSize();

	   getData();
	   input.setChanged( changed );
	   wFields.optWidth( true );

	   shell.open();
	   while ( !shell.isDisposed() ) {
	     if ( !display.readAndDispatch() ) {
	       display.sleep();
	     }
	   }
	   return stepname;
	 }

	 /**
	  * Copy information from the meta-data input to the dialog fields.
	  */
	 public void getData() {
	   wConfigFile.setText( Const.NVL( input.getConfigFile(), "" ) );

	   for ( int i = 0; i < input.getUserFields().length; i++ ) {
	     TableItem item = wFields.table.getItem( i );
	     item.setText( 1, Const.NVL( input.getUserFields()[i].getName(), "") );
	     item.setText( 2, Const.NVL( input.getUserFields()[i].getType().toString(), "" ) );
	   }
	   wFields.removeEmptyRows();
	   wFields.setRowNums();

	   wStepname.selectAll();
	   wStepname.setFocus();
	 }

	  public void getFields() {
	    int clearFields = SWT.NO;
	    int nrInputFields = wFields.nrNonEmpty();
	    if ( nrInputFields > 0 ) {
	      MessageBox mb = new MessageBox( shell, SWT.YES | SWT.NO | SWT.ICON_QUESTION );
	      mb.setMessage( BaseMessages.getString( PKG, "BoxInputDialog.ClearFieldList.DialogMessage" ) );
	      mb.setText( BaseMessages.getString( PKG, "BoxInputDialog.ClearFieldList.DialogTitle" ) );
	      clearFields = mb.open();
	    }

	    if ( clearFields == SWT.YES ) {
	      // Clear Fields Grid
	      wFields.table.removeAll();
	    }

	    for ( Attribute value : Attribute.values() ) {
	      TableItem item = new TableItem( wFields.table, SWT.NONE );
	      item.setText( 1, value.toString() );
	      item.setText( 2, value.toString() );
	    }

	    wFields.removeEmptyRows();
	    wFields.setRowNums();
	    wFields.optWidth( true );
	    input.setChanged();
	  }

	  private String[] getElementNames() {
	    Attribute[] values = Attribute.values();
	    String[] valueNames = new String[values.length];
	    for ( int i = 0; i < values.length; i++ ) {
	      valueNames[i] = values[i].toString();
	    }
	    return valueNames;
	  }

	 private void cancel() {
	   stepname = null;
	   input.setChanged( changed );
	   dispose();
	 }

	 private void ok() {

	   if ( Const.isEmpty( wStepname.getText() ) ) {
	     return;
	   }

	   // Get the information for the dialog into the input structure.
	   getInfo( input );

	   dispose();
	 }

	  private void getInfo( BoxUsersInputMeta inf ) {
	    inf.setConfigFile( wConfigFile.getText() );

	    int nrFields = wFields.nrNonEmpty();
	    List<UserField> fields = new ArrayList<>();
	    for ( int i = 0; i < nrFields; i++ ) {
	      TableItem item = wFields.getNonEmpty( i );
	      String colName = item.getText( 1 );
	      Attribute colType = Attribute.getEnumFromValue( item.getText( 2 ) );
	      if ( colType != null && !Const.isEmpty( colName ) ) {
	        fields.add( new UserField( colName, colType ) );
	      }
	    }
	    inf.setUserFields( fields.toArray( new UserField[]{} ) );

	    stepname = wStepname.getText(); // return value
	  }
}

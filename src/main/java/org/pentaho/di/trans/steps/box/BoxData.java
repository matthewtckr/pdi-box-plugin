package org.pentaho.di.trans.steps.box;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class BoxData extends BaseStepData implements StepDataInterface {

  BoxFacade conn;
  RowMetaInterface rowMeta;
}

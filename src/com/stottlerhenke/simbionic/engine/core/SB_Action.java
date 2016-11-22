package com.stottlerhenke.simbionic.engine.core;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Action;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;

/**
 * Container for {@link Action} model in the engine layer.
 *
 */
public class SB_Action extends SB_Method {
   
  public SB_Action() {
  }

  public SB_Action(Action actionModel){
    super(actionModel.getName());
    _params = new SB_ParamList();
    for (Parameter param : actionModel.getParameters()) {
       _params.add(new SB_Parameter(param.getName(), param.getType()));
    }
  }

  public SB_Action(String name, SB_ParamList params)
  {
    super(name);
    _params = params;
  }

}
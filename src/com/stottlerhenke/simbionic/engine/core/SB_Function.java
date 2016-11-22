package com.stottlerhenke.simbionic.engine.core;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate;

/**
 * Represents a predicate data model in the engine. 
 */
public class SB_Function extends SB_Method {
  public SB_Function() {
  }

  public SB_Function(String name, SB_ParamList params)
  {
    super(name);
    _params = params;
  }
  
  public SB_Function(Predicate predicateModel){
     super(predicateModel.getName());
     _params = new SB_ParamList();
     for (Parameter param : predicateModel.getParameters()) {
        _params.add(new SB_Parameter(param.getName(), param.getType()));
     }
   }

 }
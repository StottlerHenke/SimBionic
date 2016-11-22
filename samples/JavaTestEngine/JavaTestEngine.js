
function Say(msg) {
  // println("Say : " + msg);
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(msg);
}

function SayP(msg, result) {
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(msg);
   return result;
}


function IsNull(obj) {
   //println("IsNull? " + (obj == null));
   return obj == null;
}

function ActionParamIn(p1, p2, p3, p4, p5) {
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p1 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p2 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p3 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p4 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p5 + ",");
}

function ActionStringAsAny(str) {
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(str.toString());
}

function PredParamIn(p1, p2, p3, p4, p5, p6) {
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p1 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p2 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p3 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p4 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p5 + ",");
   com.stottlerhenke.simbionic.test.engine.TestEngine.buffer.append(p6 + ",");
   return true;
}


function ExceptionAction() {
   throw new com.stottlerhenke.simbionic.api.SB_Exception("ExceptionAction failed!");
}

function ExceptionPredicate() {
   throw new com.stottlerhenke.simbionic.api.SB_Exception("ExceptionPredicate failed!");
}
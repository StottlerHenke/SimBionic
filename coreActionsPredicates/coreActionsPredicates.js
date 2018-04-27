function None() {
   // required for actions that don't have any expression.
}

function CreateEntity(entityName, behavior, params, updateFrequency, updatePriority) {
   //println("entityName = " + entityName);
   //println("params= " + params);
   //println("updateFrequency= " + updateFrequency);
   //println("updatePriority= " + updatePriority);
   var entityId = com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().CreateEntity(entityName, behavior, params, updateFrequency, updatePriority);
   //println("Entity Id: " + entityId);
   return entityId;
}


function DestroyEntity(entityId) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().DestroyEntity(entityId);
}


function GetEntityName() {
   return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetEntityName();
}

function GetCurrentEntity() {
   return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetCurrentEntity();
}

function PushBehavior(entityId, behavior, params) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().PushBehavior(entityId, behavior, params); 
}

function Resume() {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().Resume();
}

function Rethrow() {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().Rethrow();
}

function Retry() {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().Retry();
}

function SetBehavior(entityId, behavior, params) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().SetBehavior(entityId, behavior, params); 
}

function SetEntityGlobal(entityId, varName, value) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().SetEntityGlobal(entityId, varName, value);
}

function SetUpdateFrequency(newFreq, entityId) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().SetUpdateFrequency(newFreq, entityId);
}

function SetUpdatePriority(newPriority, entityId) {
   com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().SetUpdatePriority(newPriority, entityId);
}


function GetEntityID() {
   var entityId = com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetEntityID();
   return entityId;
}

function If(condition, value1, value2) {
   return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().If(condition, value1, value2);
}


function IsDone() {
   return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().IsDone();
}


function IsEntityFinished(entityId) {
   var isEntityFinished = com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().IsEntityFinished(entityId);
   return isEntityFinished;
}

function DestroyGroup(name) {
	com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().DestroyGroup(name);
}

function JoinGroup(name) {
	com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().JoinGroup(name);
}

function NextMsg() {
	com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().NextMsg();
}

function QuitGroup(name) {
	com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().QuitGroup(name);
}

function SendGroupMsg(group, type, msg) {
	com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().SendGroupMsg(group, type, msg);
}

function GetMsgData() {
	return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetMsgData();
}

function GetMsgSender() {
	return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetMsgSender();
}

function GetMsgType() {
	return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().GetMsgType();
}

function HasMsg() {
	return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().HasMsg();
}

function NumMembers(name) {
	return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().NumMembers(name);
}

//2016-07-26 Blackboard items below

function CreateBBoard(name) {
    com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().CreateBBoard(name);
}

function DestroyBBoard(name) {
    com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().DestroyBBoard(name);
}

function PostBBoard(boardName, key, value) {
    com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().PostBBoard(boardName, key, value);
}

function IsBBoard(name) {
    return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().IsBBoard(name);
}

function ReadBBoard(boardName, key) {
    return com.stottlerhenke.simbionic.engine.ActionPredicateAPI.getInstance().ReadBBoard(boardName, key);
}

function chooseDS(choicePointName) {
	return choicePointName
}

package com.stottlerhenke.simbionic.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.stottlerhenke.simbionic.common.xmlConverters.XMLObjectConverter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Action;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Category;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Constant;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Descriptor;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Global;
import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Local;
import com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Parameter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Predicate;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.PredicateFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;

/**
 * Tests the writing and reading of sample SimBionicJava object.
 * 
 * any changes to create* methods requires changes to verify* methods
 * 
 *
 */
public class SimBionicJavaReadWriteTest extends TestCase {

   static SimBionicJava _model = new SimBionicJava();

   protected void setUp() throws Exception {
      _model = new SimBionicJava();
      
      _model.setVersion(3);
      _model.setIpAddress("10.0.0.1");
      _model.setLoopBack(true);
      _model.setMain("testMain");
      
      createActions();
      createPredicates();
      createConstants();
      createCategories();
      createBehaviors();
      createGlobals();
      createJavaScript();
    
   }

   public void testWriteModel() throws Exception{
      XMLObjectConverter.getInstance().saveZippedXML(_model, "simBionicJava.zip");
   }

   public void testReadModel() throws Exception{
      SimBionicJava actual = XMLObjectConverter.getInstance().zippedXMLToObject(new File("simBionicJava.zip"));
      
      // verify project settings
      assertTrue(actual.getVersion() == 3);
      assertEquals("10.0.0.1", actual.getIpAddress());
      assertTrue(actual.isLoopBack());
      assertEquals("testMain", actual.getMain());
      
      
      // verify 
      verifyActions(actual);
      verifyPredicates(actual);
      verifyConstants(actual);
      verifyCategories(actual);
      verifyBehaviors(actual);
      verifyGlobals(actual);
      verifyJavaScript(actual);
   }
   
   private void createActions() {
      //   actions
      //       testing action 1
      //       testing action 2
      //       folder1
      //           action in folder1
      //           subfolder1
      //                action in subfolder1
      //       folder2
      ActionFolderGroup group = new ActionFolderGroup();
      
      Action testingAction1 = createAction("testing action 1", "testing action 1 des.", true,
            createParameter("pa1", "java.lang.String"));
      addAction(group, testingAction1);
      
      Action testingAction2 = createAction("testing action 2", "testing action 2 des.", false,
            createParameter("p1", "java.lang.Integer"));
      addAction(group, testingAction2);
      
      ActionFolder actionFolder1 = new ActionFolder();
      actionFolder1.setName("folder1");
      
      Action actionInFolder1 = createAction("action in folder1", "action in folder 1 des.", true);
      ActionFolderGroup childrenGroupForFolder1 = new ActionFolderGroup();
      addAction(childrenGroupForFolder1, actionInFolder1);
      addToFolder(actionFolder1, childrenGroupForFolder1);
      
      // subfolder1
      ActionFolder subfolder1 = new ActionFolder();
      subfolder1.setName("subfolder1");
      Action actionInSubfolder1 = createAction("action in subfolder1", "action in subfolder 1 desc.", false);
      
      ActionFolderGroup childrenGroupForSubfolder1 = new ActionFolderGroup();
      addAction(childrenGroupForSubfolder1, actionInSubfolder1);
      addToFolder(subfolder1, childrenGroupForSubfolder1);
      // add subfolder1 to its parent
      addFolder(childrenGroupForFolder1, subfolder1);

      addFolder(group, actionFolder1);
      
      ActionFolder actionFolder2 = new ActionFolder();
      actionFolder2.setName("folder2");
      addFolder(group, actionFolder2);
      
      _model.setActions(group);
   }
   
   private void createPredicates() {
      //   predicates
      //       predicate 1
      //       predicate 2
      //       folder1
      //           subfolder1
      //                predicate in subfolder1
      //           subfolder2
      //                predicate in subfolder2
      //       folder2
      PredicateFolderGroup group = new PredicateFolderGroup();
      
      Predicate predicate1 = createPredicate("predicate 1", "predicate 1 des.", true, "java.lang.Boolean", 
            createParameter("pa1", "java.lang.String"));
      addPredicate(group, predicate1);
      
      Predicate predicate2 = createPredicate("predicate 2", "predicate 2 des.", true, "java.lang.Object", 
            createParameter("p1", "java.lang.Integer"), createParameter("p2", "java.lang.Integer"));
      addPredicate(group, predicate2);
      
      PredicateFolder folder1 = new PredicateFolder();
      folder1.setName("folder1");
      
      PredicateFolderGroup childrenGroupForFolder1 = new PredicateFolderGroup();
      addToFolder(folder1, childrenGroupForFolder1);
      
      // subfolder1
      PredicateFolder subfolder1 = new PredicateFolder();
      subfolder1.setName("subfolder1");
      Predicate predicateInSubfolder1 = createPredicate("predicate in subfolder1", "predicate in subfolder1 des.", 
            false, "java.lang.Boolean"); 
      
      PredicateFolderGroup childrenGroupForSubfolder1 = new PredicateFolderGroup();
      addPredicate(childrenGroupForSubfolder1, predicateInSubfolder1);
      addToFolder(subfolder1, childrenGroupForSubfolder1);
      // add subfolder1 to its parent
      addFolder(childrenGroupForFolder1, subfolder1);
      
     // subfolder2
      PredicateFolder subfolder2 = new PredicateFolder();
      subfolder2.setName("subfolder2");
      Predicate predicateInSubfolder2 = createPredicate("predicate in subfolder2", "predicate in subfolder2 des.", 
            false, "java.lang.Boolean"); 
      
      PredicateFolderGroup childrenGroupForSubfolder2 = new PredicateFolderGroup();
      addPredicate(childrenGroupForSubfolder2, predicateInSubfolder2);
      addToFolder(subfolder2, childrenGroupForSubfolder2);
      // add subfolder2 to its parent
      addFolder(childrenGroupForFolder1, subfolder2);

      // add folder1 to the group
      addFolder(group, folder1);
      
      PredicateFolder folder2 = new PredicateFolder();
      folder2.setName("folder2");
      addFolder(group, folder2);
      
      _model.setPredicates(group);
   }
   
   private void createConstants() {
      Constant c1 = new Constant();
      c1.setName("c1");
      c1.setType("java.lang.String");
      c1.setValue("c1 constant");
      
      Constant c2 = new Constant();
      c2.setName("c2");
      c2.setType("java.lang.String");
      c2.setValue("c2 constant");
      
      _model.getConstants().add(c1);
      _model.getConstants().add(c2);
   }
   
   private void createCategories() {
      Category c1 = new Category();
      c1.setName("category 1");
      Descriptor d1 = new Descriptor();
      d1.setName("descriptor1");
      Descriptor child1 = new Descriptor();
      child1.setName("child1");
      Descriptor child2 = new Descriptor();
      child2.setName("child2");
      d1.addDescriptor(child1);
      d1.addDescriptor(child2);
      c1.getDescriptors().add(d1);
      
      Category c2 = new Category();
      c2.setName("category 2");
      Descriptor d2 = new Descriptor();
      d2.setName("descriptor2");
      c2.getDescriptors().add(d2);
      
      _model.getCategories().add(c1);
      _model.getCategories().add(c2);
   }
   
   private void createBehaviors() {
      // behaviors
      //    behavior 1
      //    folder1
      //       behavior1 in folder1
      //       behavior2 in folder1
      //       subfolder1
      //          behavior in subfolder1
      //    folder2
      BehaviorFolderGroup group = new BehaviorFolderGroup();
      
      // behavior 1
      Behavior behavior1 = createBehaviorOne();
      addBehavior(group, behavior1);
      
      // folder1
      BehaviorFolder folder1 = new BehaviorFolder();
      folder1.setName("folder1");

      BehaviorFolderGroup childrenGroupForFolder1 = new BehaviorFolderGroup();
      
      // behavior1 in folder1
      Behavior behaviorInFolder1 = createBehavior("behavior1 in folder1", "behavior1 in folder1 desc.", 0, true,
            new ArrayList<Poly>()); 
      addBehavior(childrenGroupForFolder1, behaviorInFolder1);
      addToFolder(folder1, childrenGroupForFolder1);
      
      // behavior1 in folder2
      Behavior behaviorInFolder2 = createBehavior("behavior2 in folder1", "behavior2 in folder1 desc.", 1, false,
            new ArrayList<Poly>()); 
      addBehavior(childrenGroupForFolder1, behaviorInFolder2);
      
      // subfolder1
      BehaviorFolder subfolder1 = new BehaviorFolder();
      subfolder1.setName("subfolder1");
      Behavior behaviorInSubfolder1 = createBehavior("behavior in subfolder1", "behavior in subfolder 1 desc.", 0, false,
            new ArrayList<Poly>());
      
      BehaviorFolderGroup childrenGroupForSubfolder1 = new BehaviorFolderGroup();
      addBehavior(childrenGroupForSubfolder1, behaviorInSubfolder1);
      addToFolder(subfolder1, childrenGroupForSubfolder1);
      // add subfolder1 to its parent
      addFolder(childrenGroupForFolder1, subfolder1);

      addFolder(group, folder1);
      
      BehaviorFolder folder2 = new BehaviorFolder();
      folder2.setName("folder2");
      addFolder(group, folder2);
      
      _model.setBehaviors(group);
      
   }
   
   private Behavior createBehaviorOne() {
      
      List<ActionNode> actionNodes = createActionNodes(createActionNode(2, "actionNode",
            30, 40, "action node comment", 1, false, createBinding("v1", "v1 expr")));
      List<CompoundActionNode> compoundActionNodes = createCompoundActionNodes(
            createCompoundActionNode(3, 30, 40, "action node comment", 1, false));
      NodeGroup nodeGroup1 = createNodeGroup(1, actionNodes, compoundActionNodes);
      
      List<Condition> conditions = new ArrayList<Condition>();
      conditions.add(createCondition(5, "condition expr", 100, 100, "condition comment",
            0, createBinding("c", "c expr")));
      conditions.add(createCondition(15, "condition expr2", 200, 200, "condition comment 2",
            0));
      
      List<Start> starts = createStartGroup(
            createStart(20, 1, createConnector(9, 10, 11, 10, 12, 20, 30, 1, true,
                  "connector comment", 1, createBinding("connector", "connector expr")))
                  );
      
      Poly p1 = createPoly(createIndexGroup("index1", "index2"), 
            createLocalGroup(createLocal("local1", "local1 type"), createLocal("local2", "local2 type")),
            nodeGroup1,
            conditions,
            starts);
      
      NodeGroup nodeGroup2 = createNodeGroup(2, new ArrayList<ActionNode>(), 
            new ArrayList<CompoundActionNode>());
      
      Poly p2 = createPoly(createIndexGroup("index0"),
            new ArrayList<Local>(), 
            nodeGroup2,
            new ArrayList<Condition>(),
            new ArrayList<Start>());
      
      List<Poly> polys = new ArrayList<Poly>();
      polys.add(p1);
      polys.add(p2);
      
      return createBehavior("behavior 1", "behavior 1 desc.", 0, true, polys, 
            createParameter("p1", "java.lang.Integer"), createParameter("p2", "java.lang.Boolean"));
   }
   
   private Behavior createBehavior(String name, String description, int exec, boolean interrupt,
         List<Poly> polys,  Parameter... parameters) {
      Behavior behavior = new Behavior();
      behavior.setName(name);
      behavior.setDescription(description);
      behavior.setExec(exec);
      behavior.setInterrupt(interrupt);
      behavior.setPolys(polys);
      for (Parameter param : parameters) {
        behavior.addParameter(param);
      }
      return behavior;
   }
   
   private Poly createPoly(List<String> indexGroup, List<Local> locals,
         NodeGroup nodeGroup, List<Condition> conditions, List<Start> starts) {
      Poly poly = new Poly();
      poly.setIndices(indexGroup);
      poly.setLocals(locals);
      poly.setNodes(nodeGroup);
      poly.setConditions(conditions);
      poly.setConnectors(starts);
      
      return poly;
   }
   
   private List<String> createIndexGroup(String... strings) {
      List<String> group = new ArrayList<String>();
      for (String index : strings) {
         group.add(index);
      }
      return group;
   }
   
   private List<Local> createLocalGroup(Local... locals) {
      List<Local> localGroup = new ArrayList<Local>();
      for (Local local : locals){
         localGroup.add(local);
      }
      
      return localGroup;
   }
   
   private Local createLocal(String name, String type) {
      Local local = new Local();
      local.setName(name);
      local.setType(type);
      return local;
   }
   
   private NodeGroup createNodeGroup(int initial, List<ActionNode> actionNodes,
         List<CompoundActionNode> compoundNodes) {
      NodeGroup group = new NodeGroup();
      group.setInitial(initial);
      group.setActionNodes(actionNodes);
      group.setCompoundActionNodes(compoundNodes);
      return group;
   }
   
   private List<ActionNode> createActionNodes(ActionNode... actionNodes) {
      List<ActionNode> group = new ArrayList<ActionNode>();
      for (ActionNode actionNode : actionNodes) {
         group.add(actionNode);
      }
      return group;
   }
   
   private List<CompoundActionNode> createCompoundActionNodes(CompoundActionNode... compoundActionNodes) {
      List<CompoundActionNode> group = new ArrayList<CompoundActionNode>();
      for (CompoundActionNode node : compoundActionNodes) {
         group.add(node);
      }
      return group;
   }
   
   private ActionNode createActionNode(int id, String expr, int cx, int cy, 
         String comment, int labelMode, boolean isFinal, Binding... bindings) {
      ActionNode node = new ActionNode();
      node.setId(id);
      node.setExpr(expr);
      node.setCx(cx);
      node.setCy(cy);
      node.setComment(comment);
      node.setLabelMode(labelMode);
      node.setIsFinal(isFinal);
      for (Binding binding : bindings){
         node.getBindings().add(binding);
      }
      
      return node;
   }
   
   private CompoundActionNode createCompoundActionNode(int id, int cx, int cy, 
         String comment, int labelMode, boolean isFinal, Binding... bindings) {
      CompoundActionNode node = new CompoundActionNode();
      node.setId(id);
      node.setCx(cx);
      node.setCy(cy);
      node.setComment(comment);
      node.setLabelMode(labelMode);
      node.setIsFinal(isFinal);
      for (Binding binding : bindings){
         node.getBindings().add(binding);
      }
      
      return node;
   }
   
   private Condition createCondition(int id, String expr, int cx, int cy, String comment, int labelMode,
         Binding... bindings) {
      Condition condition = new Condition();
      condition.setId(id);
      condition.setExpr(expr);
      condition.setCx(cx);
      condition.setCy(cy);
      condition.setComment(comment);
      condition.setLabelMode(labelMode);
      for (Binding binding : bindings){
         condition.getBindings().add(binding);
      }
      return condition;
   }
   
   private List<Start> createStartGroup(Start... starts) {
      List<Start> group = new ArrayList<Start>();
      for (Start start : starts) {
         group.add(start);
      }
      
      return group;
   }
   
   private Start createStart(int id, int type, Connector... connectors) {
      Start start = new Start();
      start.setId(id);
      start.setType(type);
      for (Connector connector : connectors) {
         start.getConnectors().add(connector);
      }
      return start;
   }
   
   private Connector createConnector(int id, int endId, int endType, int startX, int startY, 
         int endX, int endY,  
         int priority, boolean interrupt, String comment, int labelMode, Binding... bindings ) {
      Connector connector = new Connector();
      connector.setId(id);
      connector.setEndId(endId);
      connector.setEndType(endType);
      connector.setStartX(startX);
      connector.setStartY(startY);
      connector.setEndX(endX);
      connector.setEndY(endY);
      connector.setPriority(priority);
      connector.setInterrupt(interrupt);
      connector.setComment(comment);
      connector.setLabelMode(labelMode);
      
      for (Binding binding : bindings){
         connector.getBindings().add(binding);
      }
      
      return connector;
   }
   
   private Binding createBinding(String var, String expr) {
      Binding binding = new Binding();
      binding.setVar(var);
      binding.setExpr(expr);
      return binding;
   }
   
   
   
   private void createGlobals() {
      Global g1 = new Global();
      g1.setName("g1");
      g1.setType("java.lang.Integer");
      g1.setInitial("10");
      
      Global g2 = new Global();
      g2.setName("g2");
      g2.setType("java.lang.String");
      g2.setInitial("Global value for g2");
      
      _model.getGlobals().add(g1);
      _model.getGlobals().add(g2);
   }
   
   private void createJavaScript() {
      JavaScript javaScript = new JavaScript();
      javaScript.getImportedJavaClasses().add("class1");
      javaScript.getImportedJavaClasses().add("class2");
      javaScript.getImportedJavaClasses().add("class3");
      
      javaScript.getJsFiles().add("file1");
      javaScript.getJsFiles().add("file2");
      _model.setJavaScript(javaScript);
      
   }
   
   private void verifyBehaviors(SimBionicJava actual) {
      // behaviors
      //    behavior 1
      //    folder1
      //       behavior1 in folder1
      //       behavior2 in folder1
      //       subfolder1
      //          behavior in subfolder1
      //    folder2
      
      BehaviorFolderGroup behaviors = actual.getBehaviors();
      List<Object> behaviorOrFolders = behaviors.getBehaviorOrBehaviorFolder();
      assertEquals(3, behaviorOrFolders.size());
      
      // behavior 1
      Behavior behavior1 = (Behavior)behaviorOrFolders.get(0);
      verifyBehaviorOne(behavior1);
      
      // folder1
      BehaviorFolder folder1 = (BehaviorFolder)behaviorOrFolders.get(1);
      BehaviorFolderGroup folder1Children = folder1.getBehaviorChildren();
      List<Object> behaviorOrFoldersForFolder1 = folder1Children.getBehaviorOrBehaviorFolder();
      assertEquals(3, behaviorOrFoldersForFolder1.size());
      
      // behavior1 in folder1
      Behavior behavior1InFolder1 = (Behavior)behaviorOrFoldersForFolder1.get(0);
      verifyBehavior(behavior1InFolder1, "behavior1 in folder1", "behavior1 in folder1 desc.", 0, true,
            new ArrayList<Poly>()); 
      
      // behavior2 in folder1
      Behavior behavior2InFolder1 = (Behavior)behaviorOrFoldersForFolder1.get(1);
      verifyBehavior(behavior2InFolder1, "behavior2 in folder1", "behavior2 in folder1 desc.", 1, false,
            new ArrayList<Poly>()); 
      
      // subfolder1
      BehaviorFolder subfolder1 = (BehaviorFolder)behaviorOrFoldersForFolder1.get(2);
      assertEquals("subfolder1", subfolder1.getName());
      
      // behavior in subfolder1
      BehaviorFolderGroup subFolder1Children = subfolder1.getBehaviorChildren();
      assertEquals(1, subFolder1Children.getBehaviorOrBehaviorFolder().size()); // only one behavior
      verifyBehavior((Behavior)subFolder1Children.getBehaviorOrBehaviorFolder().get(0), 
            "behavior in subfolder1", "behavior in subfolder 1 desc.", 0, false,
            new ArrayList<Poly>());

     // folder2
      BehaviorFolder folder2 = (BehaviorFolder)behaviorOrFolders.get(2);
      assertEquals("folder2", folder2.getName());
      assertTrue(folder2.getBehaviorChildren() != null);
   }
   
   private void verifyBehaviorOne(Behavior actual) {
      List<ActionNode> actionNodes = createActionNodes(createActionNode(2, "actionNode",
            30, 40, "action node comment", 1, false, createBinding("v1", "v1 expr")));
      List<CompoundActionNode> compoundActionNodes = createCompoundActionNodes(
            createCompoundActionNode(3, 30, 40, "action node comment", 1, false));
      NodeGroup nodeGroup1 = createNodeGroup(1, actionNodes, compoundActionNodes);
      
      List<Condition> conditions = new ArrayList<Condition>();
      conditions.add(createCondition(5, "condition expr", 100, 100, "condition comment",
            0, createBinding("c", "c expr")));
      conditions.add(createCondition(15, "condition expr2", 200, 200, "condition comment 2",
            0));
      
      List<Start> starts = createStartGroup(
            createStart(20, 1, createConnector(9, 10, 11, 10, 12, 20, 30, 1, true,
                  "connector comment", 1, createBinding("connector", "connector expr")))
                  );
      
      Poly p1 = createPoly(createIndexGroup("index1", "index2"), 
            createLocalGroup(createLocal("local1", "local1 type"), createLocal("local2", "local2 type")),
            nodeGroup1,
            conditions,
            starts);
      
      NodeGroup nodeGroup2 = createNodeGroup(2, new ArrayList<ActionNode>(), 
            new ArrayList<CompoundActionNode>());
      
      Poly p2 = createPoly(createIndexGroup("index0"),
            new ArrayList<Local>(), 
            nodeGroup2,
            new ArrayList<Condition>(),
            new ArrayList<Start>());
      
      List<Poly> polys = new ArrayList<Poly>();
      polys.add(p1);
      polys.add(p2);
      
      verifyBehavior(actual, "behavior 1", "behavior 1 desc.", 0, true, polys, 
            createParameter("p1", "java.lang.Integer"), createParameter("p2", "java.lang.Boolean"));
      
   }
   
   private void verifyBehavior(Behavior actual, String expectedName, String expectedDescription,
         int expectedExec, boolean expectedInterrupt, List<Poly> expectedPolyGroup,
         Parameter... expectedParameters) {
      assertEquals(expectedName, actual.getName());
      assertEquals(expectedDescription, actual.getDescription());
      assertEquals(expectedExec, actual.getExec());
      assertTrue(expectedInterrupt == actual.isInterrupt());
      verifyPolys(expectedPolyGroup, actual.getPolys());
      
      List<Parameter> actualParams = actual.getParameters();
      assertEquals(expectedParameters.length, actualParams.size());
      int i = 0;
      for (Parameter expectedParam : expectedParameters) {
         verifyParameter(actualParams.get(i), expectedParam.getName(), expectedParam.getType());
         i++;
      }
   }
   
   private void verifyPolys(List<Poly> expected, List<Poly> actual) {
      assertEquals(expected.size(), actual.size());
      int i = 0;
      for (Poly poly : expected) {
         verifyPoly(poly, actual.get(i));
         i++;
      }
   }
   
   private void verifyPoly(Poly expected, Poly actual) {
      // verify index group
      List<String> expectedIndexGroup = expected.getIndices();
      List<String> actualIndexGroup = actual.getIndices();
      assertEquals(expectedIndexGroup.size(), actualIndexGroup.size());
      int i = 0;
      for (String index : expectedIndexGroup) {
         assertEquals(index, actualIndexGroup.get(i));
         i++;
      }
      
      // verify local group
      List<Local> expectedLocals = expected.getLocals();
      List<Local> actualLocals = actual.getLocals();
      assertEquals(expectedLocals.size(), actualLocals.size());
      i = 0;
      for (Local expectedLocal : expectedLocals) {
         Local actualLocal = actualLocals.get(i);
         assertEquals(expectedLocal.getName(), actualLocal.getName());
         assertEquals(expectedLocal.getType(), actualLocal.getType());
         i++;
      }
      
      // node group
      verifyNodeGroup(expected.getNodes(), actual.getNodes());
      
      // conditions
      List<Condition> expectedConditions = expected.getConditions();
      List<Condition> actualConditions = actual.getConditions();
      assertEquals(expectedConditions.size(), actualConditions.size());
      i = 0;
      for (Condition expectedCondition : expectedConditions) {
         Condition actualCondition = actual.getConditions().get(i);
         assertEquals(expectedCondition.getId(), actualCondition.getId());
         assertEquals(expectedCondition.getCx(), actualCondition.getCx());
         assertEquals(expectedCondition.getCy(), actualCondition.getCy());
         assertEquals(expectedCondition.getExpr(), actualCondition.getExpr());
         assertEquals(expectedCondition.getComment(), actualCondition.getComment());
         assertEquals(expectedCondition.getLabelMode(), actualCondition.getLabelMode());
         verifyBindings(expectedCondition.getBindings(), actualCondition.getBindings());
         i++;
      }
      
      // connectors
      List<Start> expectedStarts = expected.getConnectors();
      List<Start> actualStarts = actual.getConnectors();
      assertEquals(expectedStarts.size(), actualStarts.size());
      i = 0;
      for (Start expectedStart : expectedStarts) {
         Start actualStart = actual.getConnectors().get(i);
         assertEquals(expectedStart.getId(), actualStart.getId());
         assertEquals(expectedStart.getType(), actualStart.getType());
         
         List<Connector> expectedConnectors = expectedStart.getConnectors();
         List<Connector> actualConnectors = actualStart.getConnectors();
         int j = 0;
         for (Connector expectedConnector : expectedConnectors) {
            Connector actualConnector = actualConnectors.get(j);
            assertEquals(expectedConnector.getId(), actualConnector.getId());
            assertEquals(expectedConnector.getEndId(), actualConnector.getEndId());
            assertEquals(expectedConnector.getEndType(), actualConnector.getEndType());
            assertEquals(expectedConnector.getStartX(), actualConnector.getStartX());
            assertEquals(expectedConnector.getStartY(), actualConnector.getStartY());
            assertEquals(expectedConnector.getEndX(), actualConnector.getEndX());
            assertEquals(expectedConnector.getEndY(), actualConnector.getEndY());
            assertEquals(expectedConnector.getPriority(), actualConnector.getPriority());
            assertEquals(expectedConnector.isInterrupt(), actualConnector.isInterrupt());
            assertEquals(expectedConnector.getComment(), actualConnector.getComment());
            assertEquals(expectedConnector.getLabelMode(), actualConnector.getLabelMode());
            verifyBindings(expectedConnector.getBindings(), actualConnector.getBindings());
            j++;
         }
         
         i++;
      }
   }
   
   private void verifyNodeGroup(NodeGroup expected, NodeGroup actual) {
      assertEquals(expected.getInitial(), actual.getInitial());
      
      // action nodes
      List<ActionNode> expectedActionNodes = expected.getActionNodes();
      List<ActionNode> actualActionNodes = actual.getActionNodes();
      assertEquals(expectedActionNodes.size(), actualActionNodes.size());
      int i = 0;
      for (ActionNode expectedActionNode : expectedActionNodes) {
         ActionNode actualActionNode = actualActionNodes.get(i);
         assertEquals(expectedActionNode.getId(), actualActionNode.getId());
         assertEquals(expectedActionNode.getCx(), actualActionNode.getCx());
         assertEquals(expectedActionNode.getCy(), actualActionNode.getCy());
         assertEquals(expectedActionNode.getExpr(), actualActionNode.getExpr());
         assertEquals(expectedActionNode.getComment(), actualActionNode.getComment());
         assertTrue(expectedActionNode.isFinal() == actualActionNode.isFinal());
         assertEquals(expectedActionNode.getLabelMode(), actualActionNode.getLabelMode());
         verifyBindings(expectedActionNode.getBindings(), actualActionNode.getBindings());
         i++;
      }
      
     // compound action nodes
      List<CompoundActionNode> expectedCompoundActionNodes = expected.getCompoundActionNodes();
      List<CompoundActionNode> actualCompoundActionNodes = actual.getCompoundActionNodes();
      assertEquals(expectedCompoundActionNodes.size(), actualCompoundActionNodes.size());
      i = 0;
      for (CompoundActionNode expectedCompoundActionNode : expectedCompoundActionNodes) {
         CompoundActionNode actualCompoundActionNode = actualCompoundActionNodes.get(i);
         assertEquals(expectedCompoundActionNode.getId(), actualCompoundActionNode.getId());
         assertEquals(expectedCompoundActionNode.getCx(), actualCompoundActionNode.getCx());
         assertEquals(expectedCompoundActionNode.getCy(), actualCompoundActionNode.getCy());
         assertEquals(expectedCompoundActionNode.getComment(), actualCompoundActionNode.getComment());
         assertTrue(expectedCompoundActionNode.isFinal() == actualCompoundActionNode.isFinal());
         assertEquals(expectedCompoundActionNode.getLabelMode(), actualCompoundActionNode.getLabelMode());
         verifyBindings(expectedCompoundActionNode.getBindings(), actualCompoundActionNode.getBindings());
         i++;
      }
   }
   
   private void verifyBindings(List<Binding> expected, List<Binding> actual) {
      assertEquals(expected.size(), actual.size());
      int i = 0;
      for (Binding expectedBinding : expected) {
         Binding actualBinding = actual.get(i);
         assertEquals(expectedBinding.getVar(), actualBinding.getVar());
         assertEquals(expectedBinding.getExpr(), actualBinding.getExpr());
         i++;
      }
   }
   
   private void verifyGlobals(SimBionicJava actual) {
      List<Global> globals = actual.getGlobals();
      
      assertEquals(2, globals.size());
      
      Global g1 = globals.get(0);
      assertEquals("g1", g1.getName());
      assertEquals("java.lang.Integer", g1.getType());
      assertEquals("10", g1.getInitial());
      
      Global g2 = globals.get(1);
      assertEquals("g2", g2.getName());
      assertEquals("java.lang.String", g2.getType());
      assertEquals("Global value for g2",  g2.getInitial());
      
   }
   
   private void verifyJavaScript(SimBionicJava actual) {
      JavaScript javaScriptActual = actual.getJavaScript();
      List<String> importedClasses = javaScriptActual.getImportedJavaClasses();
      assertEquals(3, importedClasses.size());
      assertEquals("class1", importedClasses.get(0));
      assertEquals("class2", importedClasses.get(1));
      assertEquals("class3", importedClasses.get(2));
      
      List<String> jsFiles = javaScriptActual.getJsFiles();
      assertEquals(3, jsFiles.size());
      assertEquals("coreActionsPredicates/coreActionsPredicates.js", jsFiles.get(0));
      assertEquals("file1", jsFiles.get(1));
      assertEquals("file2", jsFiles.get(2));
      
      
   }
   
   
   private void verifyCategories(SimBionicJava actual) {
      List<Category> categories = actual.getCategories();
      
      assertEquals(2, categories.size());
      
      // category 1
      Category c1 = categories.get(0);
      assertEquals("category 1", c1.getName());
      List<Descriptor> descriptors = c1.getDescriptors();
      assertEquals(1, descriptors.size());
      Descriptor d1 = descriptors.get(0);
      assertEquals("descriptor1", d1.getName());
      
      List<Descriptor> children = d1.getDescriptors();
      assertEquals(2, children.size());
      Descriptor child1 = children.get(0);
      assertEquals("child1", child1.getName());
      Descriptor child2 = children.get(1);
      assertEquals("child2", child2.getName());

      // category 2
      Category c2 = categories.get(1);
      assertEquals("category 2", c2.getName());
      descriptors = c2.getDescriptors();
      assertEquals(1, descriptors.size());
      Descriptor d2 = descriptors.get(0);
      assertEquals("descriptor2", d2.getName());
      assertTrue(d2.getDescriptors().isEmpty());
   }
   
   
   
   private void verifyConstants(SimBionicJava actual) {
      
      List<Constant> constants = actual.getConstants();
      assertEquals(2, constants.size());
      
      Constant c1 = constants.get(0);
      assertEquals("c1", c1.getName());
      assertEquals("java.lang.String", c1.getType());
      assertEquals("c1 constant", c1.getValue());
     
      Constant c2 = constants.get(1);
      assertEquals("c2", c2.getName());
      assertEquals("java.lang.String", c2.getType());
      assertEquals("c2 constant", c2.getValue());
      
   }
   
   private void verifyActions(SimBionicJava model) {
      // see CreateActions
      //   actions
      //       testing action 1
      //       testing action 2
      //       folder1
      //           action in folder1
      //           subfolder1
      //                action in subfolder1
      //       folder2
      
      ActionFolderGroup actions = model.getActions();
      List<Object> actionOrFolders = actions.getActionOrActionFolder();
      assertEquals(4, actionOrFolders.size());
      
      // testing action 1
      Action testingAction1 = (Action)actionOrFolders.get(0);
      verifyAction(testingAction1, "testing action 1", "testing action 1 des.", true,
            createParameter("pa1", "java.lang.String"));
      
      // testing action 2
      Action testingAction2 = (Action)actionOrFolders.get(1);
      verifyAction(testingAction2, "testing action 2", "testing action 2 des.", false,
            createParameter("p1", "java.lang.Integer"));
      
      // folder1
      ActionFolder folder1 = (ActionFolder)actionOrFolders.get(2);
      ActionFolderGroup folder1Children = folder1.getActionChildren();
      List<Object> actionOrFoldersForFolder1 = folder1Children.getActionOrActionFolder();
      assertEquals(2, actionOrFoldersForFolder1.size());
      
      // action in folder1
      Action actionInFolder1 = (Action)actionOrFoldersForFolder1.get(0);
      verifyAction(actionInFolder1, "action in folder1", "action in folder 1 des.", true);
      
      // subfolder1
      ActionFolder subfolder1 = (ActionFolder)actionOrFoldersForFolder1.get(1);
      assertEquals("subfolder1", subfolder1.getName());
      
      // action in subfolder1
      ActionFolderGroup subFolder1Children = subfolder1.getActionChildren();
      assertEquals(1, subFolder1Children.getActionOrActionFolder().size()); // only one action
      verifyAction((Action)subFolder1Children.getActionOrActionFolder().get(0), 
            "action in subfolder1", "action in subfolder 1 desc.", false);

     // folder2
      ActionFolder folder2 = (ActionFolder)actionOrFolders.get(3);
      assertEquals("folder2", folder2.getName());
      assertTrue(folder2.getActionChildren() != null);
      
   }
   
   private void verifyPredicates(SimBionicJava model) {
      //    predicates
      //       predicate 1
      //       predicate 2
      //       folder1
      //           subfolder1
      //                predicate in subfolder1
      //           subfolder2
      //                predicate in subfolder2
      //       folder2
      
      PredicateFolderGroup predicates = model.getPredicates();
      List<Object> predicateOrFolders = predicates.getPredicateOrPredicateFolder();
      assertEquals(4, predicateOrFolders.size());
      
      // predicate 1
      Predicate predicate1 = (Predicate)predicateOrFolders.get(0);
      verifyPredicate(predicate1, "predicate 1", "predicate 1 des.", true, "java.lang.Boolean", 
            createParameter("pa1", "java.lang.String"));
      
      // predicate 2
      Predicate predicate2 = (Predicate)predicateOrFolders.get(1);
      verifyPredicate(predicate2, "predicate 2", "predicate 2 des.", true, "java.lang.Object", 
            createParameter("p1", "java.lang.Integer"), createParameter("p2", "java.lang.Integer"));
      
      // folder1
      PredicateFolder folder1 = (PredicateFolder)predicateOrFolders.get(2);
      PredicateFolderGroup folder1Children = folder1.getPredicateChildren();
      List<Object> predicateOrFoldersForFolder1 = folder1Children.getPredicateOrPredicateFolder();
      assertEquals(2, predicateOrFoldersForFolder1.size());
      
      // subfolder1
      PredicateFolder subfolder1 = (PredicateFolder)predicateOrFoldersForFolder1.get(0);
      assertEquals("subfolder1", subfolder1.getName());
      
      // predicate in subfolder1
      PredicateFolderGroup subFolder1Children = subfolder1.getPredicateChildren();
      assertEquals(1, subFolder1Children.getPredicateOrPredicateFolder().size()); // only one predicate
      verifyPredicate((Predicate)subFolder1Children.getPredicateOrPredicateFolder().get(0), 
            "predicate in subfolder1", "predicate in subfolder1 des.", 
            false, "java.lang.Boolean"); 
      
      // subfolder2
      PredicateFolder subfolder2 = (PredicateFolder)predicateOrFoldersForFolder1.get(1);
      assertEquals("subfolder2", subfolder2.getName());
      
      // predicate in subfolder2
      PredicateFolderGroup subFolder2Children = subfolder2.getPredicateChildren();
      assertEquals(1, subFolder2Children.getPredicateOrPredicateFolder().size()); // only one predicate
      verifyPredicate((Predicate)subFolder2Children.getPredicateOrPredicateFolder().get(0), 
            "predicate in subfolder2", "predicate in subfolder2 des.", 
            false, "java.lang.Boolean"); 


     // folder2
      PredicateFolder folder2 = (PredicateFolder)predicateOrFolders.get(3);
      assertEquals("folder2", folder2.getName());
      assertTrue(folder2.getPredicateChildren() != null);
      
   }
   
   private void verifyAction(Action actual, String expectedActionName, String expectedDescription, 
         boolean expectedIsCore, Parameter... expectedParameters) {
      assertEquals(expectedActionName, actual.getName());
      assertEquals(expectedDescription, actual.getDescription());
      assertTrue(expectedIsCore == actual.isCore());
      
      List<Parameter> actualParams = actual.getParameters();
      assertEquals(expectedParameters.length, actualParams.size());
      int i = 0;
      for (Parameter expectedParam : expectedParameters) {
         verifyParameter(actualParams.get(i), expectedParam.getName(), expectedParam.getType());
         i++;
      }
      
   }
   
   private void verifyParameter(Parameter actual, String name, String type) {
      assertEquals(name, actual.getName());
      assertEquals(type, actual.getType());
   }
   
   private void verifyPredicate(Predicate actual, String expectedName, String expectedDescription, 
         boolean expectedIsCore, String expectedReturnType, Parameter... expectedParameters) {
      assertEquals(expectedName, actual.getName());
      assertEquals(expectedDescription, actual.getDescription());
      assertTrue(expectedIsCore == actual.isCore());
      assertEquals(expectedReturnType, actual.getReturnType());
      
      List<Parameter> actualParams = actual.getParameters();
      assertEquals(expectedParameters.length, actualParams.size());
      int i = 0;
      for (Parameter expectedParam : expectedParameters) {
         verifyParameter(actualParams.get(i), expectedParam.getName(), expectedParam.getType());
         i++;
      }
      
   }
   
   private Action createAction(String actionName, String description, boolean isCore, Parameter... parameters) {
      Action action = new Action();
      action.setDescription(description);
      action.setName(actionName);
      action.setCore(isCore);
      
      for (Parameter param : parameters) {
         action.addParameter(param);
      }
      return action;
   }
   
   private Parameter createParameter(String name, String type) {
      Parameter param = new Parameter();
      param.setName(name);
      param.setType(type);
      return param;
   }
   
   private void addAction(ActionFolderGroup group, Action action) {
      group.addAction(action);
   }
   
   private void addFolder(ActionFolderGroup group, ActionFolder folder) {
      group.addActionFolder(folder);
   }
   
   private void addToFolder(ActionFolder folder, ActionFolderGroup child) {
      folder.setActionChildren(child);
   }
   
   private Predicate createPredicate(String actionName, String description, boolean isCore, String returnType, Parameter... parameters) {
      Predicate predicate = new Predicate();
      predicate.setDescription(description);
      predicate.setName(actionName);
      predicate.setCore(isCore);
      predicate.setReturnType(returnType);
      
      for (Parameter param : parameters) {
         predicate.addParameter(param);
      }
      return predicate;
   }
   
   private void addPredicate(PredicateFolderGroup group, Predicate predicate) {
      group.addPredicate(predicate);
   }
   
   private void addFolder(PredicateFolderGroup group, PredicateFolder folder) {
      group.addPredicateFolder(folder);
   }
   
   private void addToFolder(PredicateFolder folder, PredicateFolderGroup child) {
      folder.setPredicateChildren(child);
   }
   
   private void addBehavior(BehaviorFolderGroup group, Behavior behavior) {
      group.getBehaviorOrBehaviorFolder().add(behavior);
   }
   
   private void addFolder(BehaviorFolderGroup group, BehaviorFolder folder) {
      group.getBehaviorOrBehaviorFolder().add(folder);
   }
   
   private void addToFolder(BehaviorFolder folder, BehaviorFolderGroup child) {
      folder.setBehaviorChildren(child);
   }
   
   
}

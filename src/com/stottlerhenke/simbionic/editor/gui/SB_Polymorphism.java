
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Local;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Node;
import com.stottlerhenke.simbionic.common.xmlConverters.model.NodeGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_CancelException;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SB_Variable;
import com.stottlerhenke.simbionic.editor.gui.api.FindMatcher;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;

/**
 * Represents Poly data model in the UI.
 */
public class SB_Polymorphism extends SB_DrawableComposite {

   private static final long serialVersionUID = 2302585093L + 1002;
   protected SB_Behavior _parent = null;
   public SB_ElementComposite _elements = null;

   public SB_ConnectorComposite _connectors = null;

   private DefaultMutableTreeNode _locals = null;

   protected Rectangle _lastViewRect = null;

   public static final int MAX_STACK_SIZE = 3;

   transient private Vector _undoStack = new Vector();

   transient private Vector _redoStack = new Vector();

   transient private SB_CanvasMomento _momento = null;

   transient private boolean _modified = false;

   private Poly _dataModel;

   public SB_Polymorphism() // constructor for Externalizable object
   {
   } 

   public SB_Polymorphism(SB_Behavior parent, Poly dataModel) {
      _parent = parent;
      _dataModel = dataModel;

      _elements = new SB_ElementComposite();
      _connectors = new SB_ConnectorComposite();
      super.add(_elements);
      super.add(_connectors);

      setLocals(new DefaultMutableTreeNode("Locals"));
      _lastViewRect = new Rectangle();

      // update with the underlying data model
      updateWithDataModel();
   }
   
   /**
    * @return True if an action node or a condition node is highlighted.
    */
   public boolean isNodeHighlighted() {
   	int size = _elements.count();
   	for (int i = 0; i < size; ++i) {
   		SB_Drawable drawable = (SB_Drawable)_elements._drawables.get(i);
   		if (drawable.isHighlighted() && 
   		   (drawable instanceof SB_Rectangle || drawable instanceof SB_Condition)) {
   			return true;
   		}
   	}
       
   	return false;
   }
   
   /**
    * @return True if a link is highlighted.
    */
   public boolean isLinkHighlighted() {
   	int size = _connectors.count();
   	for (int i = 0; i < size; ++i) {
   		SB_Drawable drawable = (SB_Drawable)_connectors._drawables.get(i);
   		if (drawable.isHighlighted() && drawable instanceof SB_Connector) { 
   			return true;
   		}
   	}
       
   	return false;
   }

   @Override
   public List<SB_Drawable> removeHighlight() {
      List<SB_Drawable> removed = super.removeHighlight();

      // this is called after the highlighted element is removed from the canvas.
      // update the underlying data model
      NodeGroup nodes = getDataModel().getNodes();
      for (int i = 0; i < removed.size(); i++) {
	 SB_Drawable drawable = removed.get(i);
	 if (drawable instanceof SB_MultiRectangle) {
	    CompoundActionNode model = 
		  ((SB_MultiRectangle)drawable).getCompoundActionNodeModel();
	    nodes.removeCompoundActionNode(model);
	 } else if (drawable instanceof SB_Rectangle) {
	    ActionNode model = 
		  ((SB_Rectangle)drawable).getActionNodeModel();
	    nodes.removeActionNode(model);
	 } else if (drawable instanceof SB_Condition) {
	    Condition model = 
		  ((SB_Condition)drawable).getConditionModel();
	    getDataModel().removeCondition(model);
	 } else if (drawable instanceof SB_Connector) {
	    Connector model = 
		  ((SB_Connector)drawable).getDataModel();
	    Start startModel =  ((SB_Connector)drawable).getStartModel();
	    if (startModel != null) {
	       startModel.removeConnector(model);
	       if (startModel.getConnectors().isEmpty()) {
		  // no connectors - remove startModel 
		  getDataModel().removeConnector(startModel);
	       }
	    }
	 }
      }

      return removed;

   }
  

   private void updateWithDataModel() {
      // indices
      if (getIndices().isEmpty()) {
	 getDataModel().getIndices().add("Empty");
      }

      // locals
      for (Local localModel : getDataModel().getLocals()) {
	 SB_Variable sbVariable = new SB_Variable(localModel);
	 sbVariable.setNameToNextAvailable(_locals);
	 DefaultMutableTreeNode localNode = new DefaultMutableTreeNode(sbVariable);
	 _locals.add(localNode);
      }

      // nodes
      NodeGroup nodes = getDataModel().getNodes();
      int initialNodeId = nodes.getInitial();
      for (ActionNode actionNode : nodes.getActionNodes()) {
	 SB_Rectangle sbRectangle = new SB_Rectangle(actionNode);
	 if (sbRectangle.getId() == initialNodeId) {
	    sbRectangle.setInitial(true);
	 }
	 // Note: don't call _elements.add method because ids are re-generated.
	 _elements._drawables.add(sbRectangle);
      }

      for (CompoundActionNode compoundActionNode : nodes.getCompoundActionNodes()){
	 SB_MultiRectangle sbMultiRectangle = new SB_MultiRectangle(compoundActionNode);
	 if (sbMultiRectangle.getId() == initialNodeId) {
	    sbMultiRectangle.setInitial(true);
	 }
	 // Note: don't call _elements.add method because ids are re-generated.
	 _elements._drawables.add(sbMultiRectangle);
      }

      // conditions
      for (Condition condition : getDataModel().getConditions()) {
	 // Note: don't call _elements.add method because ids are re-generated.
	 _elements._drawables.add(new SB_Condition(condition));
      }

      // connectors
      for (Start startModel : getDataModel().getConnectors()) {
    	  int startId = startModel.getId();
    	  int startType = startModel.getType();
    	  SB_Element startElement = _elements.findById(startId, startType);

    	  for (Connector connectorModel : startModel.getConnectors()) {
    		  int endId = connectorModel.getEndId();
    		  int endType = connectorModel.getEndType();
    		  SB_Element endElement = _elements.findById(endId, endType);
    		  SB_Connector sbConnector = new SB_Connector(connectorModel, startModel);
    		  // Note: don't call setStartElement since that method reprioritize connectors.
    		  sbConnector._startElement = startElement;
    		  sbConnector.setEndElement(endElement);
    		  int priority = connectorModel.getPriority();
    		  if (priority != -1 && startElement != null) {
    			  if (startElement._connectors.size() < priority) {
    				  startElement._connectors.setSize(priority);
    			  }
    			  startElement._connectors.set(priority - 1, sbConnector);
    		  }

    		  sbConnector.updatePoints();
    		  // Note: don't call _connectors.add method because ids are re-generated.
    		  _connectors._drawables.add(sbConnector);
    	  }
    	  
      }


   }

   public Poly getDataModel() {
      return _dataModel;
   }



   public void draw(Graphics2D g2) {
      boolean needToResize = _elements.needToResize();
      _elements.draw(g2);
      if (needToResize) _connectors.updatePoints();
      _connectors.draw(g2);
   }

   public void bringHighlightToFront() {
      _elements.bringHighlightToFront();
      _connectors.bringHighlightToFront();
   }

   public boolean add(SB_Drawable drawable) {
      return false;
   }

   public boolean add(SB_Element element) {
	   // Note: must call this first to set the id of the new element.
	   boolean success = _elements.add(element);
      if ((element instanceof SB_Rectangle) && _elements.getInitial() == null) {
	 SB_Rectangle rectangle = (SB_Rectangle) element;
	 rectangle.setInitial(true);
	 getDataModel().getNodes().setInitial(rectangle.getId());
      }
      return success;
   }

   public String toString(){
      return _elements.toString();
   }

   public boolean add(SB_Connector connector) {
	   return _connectors.add(connector);
   }

   /**
    * Update other connector labels except for the given one; if null update all entities
    * @param connector
    */
   public void updateConnectorLabels(SB_Connector connector) {
	   for(Object sbc : _connectors._drawables) {

		   SB_Connector c = (SB_Connector) sbc;
		   if(!c.equals(connector))
			   ((SB_Connector) sbc).updateLabel();
	   }

   }
   
   protected SB_ElementComposite getElements() {
      return _elements;
   }

   protected SB_ConnectorComposite getConnectors() {
      return _connectors;
   }

   public void write(ObjectOutputStream s, boolean highlightOnly) {
      _elements.write(s, highlightOnly);
      _connectors.write(s, highlightOnly);

      // Recall that serialization copies objects (once), so the code takes advantage of this.
      writeNodeModel(s, highlightOnly);
      writeConnectorModel(s, highlightOnly);

   }

   private void writeNodeModel(ObjectOutputStream s, boolean highlightOnly) {
      try
      {
	 SB_Drawable drawable;
	 int size = _elements._drawables.size();
	 if (highlightOnly)
	    s.writeInt(_elements.countHighlight());
	 else
	    s.writeInt(size);

	 int initial = _dataModel.getNodes().getInitial();
	 Node initNode = null;
	 for (int i = 0; i < size; ++i)
	 {
	    drawable = (SB_Drawable) _elements._drawables.get(i);
	    if (!highlightOnly || drawable.isHighlighted()) {
	       if (drawable instanceof SB_MultiRectangle) {
		  CompoundActionNode model = 
			((SB_MultiRectangle)drawable).getCompoundActionNodeModel();
		  if (model.getId() == initial)
		     initNode = model;
		  s.writeObject(model);
	       } else if (drawable instanceof SB_Rectangle) {
		  ActionNode model = 
			((SB_Rectangle)drawable).getActionNodeModel();
		  if (model.getId() == initial)
		     initNode = model;
		  s.writeObject(model);
	       } else if (drawable instanceof SB_Condition) {
		  Condition model = 
			((SB_Condition)drawable).getConditionModel();
		  s.writeObject(model);
	       }
	    }
	 }
	 // Send the reference to initial node again (its id may change so we can't simply send initial id).
	 s.writeObject(initNode); 

      }
      catch (IOException e)
      {
	 System.err.println("i/o exception");
      }
   }

   private void writeConnectorModel(ObjectOutputStream s, boolean highlightOnly) {
      try
      {
	 SB_Drawable drawable;
	 int size = _connectors._drawables.size();
	 if (highlightOnly)
	    s.writeInt(_connectors.countHighlight());
	 else
	    s.writeInt(size);
	 for (int i = 0; i < size; ++i)
	 {
	    drawable = (SB_Drawable) _connectors._drawables.get(i);

	    SB_Connector oldConnector = (SB_Connector)drawable;
	    SB_Element startElement = oldConnector.getStartElement();
	    SB_Element endElement = oldConnector.getEndElement();

	    if (!highlightOnly || drawable.isHighlighted()) {
	       if (drawable instanceof SB_Connector) {
		  Start startModel;
		  if (highlightOnly) {

		     if (startElement == null || !startElement.isHighlighted()) {
			startModel = null;
		     }
		     else {
			int startId = startElement.getId();
			int startType = SB_Element.getType(startElement);
			startModel = null;
			for (Start start : _dataModel.getConnectors()) {
			   if (start.getId() == startId && start.getType() == startType) {
			      startModel = start;
			   }
			}
		     }

		  }
		  else startModel = oldConnector.getStartModel();

		  Connector oldModel = oldConnector.getDataModel();



		  s.writeObject(oldModel);
		  s.writeObject(startModel);

		  if (endElement == null || (!endElement.isHighlighted() && highlightOnly))
		  {
		     s.writeObject(false); // Boolean to indicate if there is an end
		  }
		  else s.writeObject(true);

	       }
	    } 
	 }
      }
      catch (IOException e)
      {
	 System.err.println("i/o exception");
      }
   }

   public void read(ObjectInputStream s, boolean highlightOnly) {
      _elements.read(s, highlightOnly);
      _connectors.read(s, highlightOnly);
      if (highlightOnly) _connectors.updatePoints();

      readNodeModel(s);
      readConnectorModel(s); 
   }

   private void readNodeModel(ObjectInputStream s) {

      try
      {
	 Node n;
	 int size = s.readInt();
	 for (int i = 0; i < size; ++i)
	 {
	    n = (Node) s.readObject();
	    if (n instanceof Condition)
	       _dataModel.addCondition((Condition)n);
	    else if (n instanceof CompoundActionNode) {
	       _dataModel.getNodes().addCompoundActionNode((CompoundActionNode) n);
	    }
	    else if (n instanceof ActionNode) {
	       _dataModel.getNodes().addActionNode((ActionNode) n);
	    }
	 }

	 Node initNode = (Node) s.readObject();
	 if (initNode != null && _dataModel.getNodes().getInitial() < 0)
	    _dataModel.getNodes().setInitial(initNode.getId());
      }
      catch (IOException e)
      {
	 System.err.println("i/o exception");
	 e.printStackTrace();

      }
      catch (ClassNotFoundException e)
      {
	 System.err.println("class not found");
	 e.printStackTrace();
      }
   }

   private void readConnectorModel(ObjectInputStream s) {
      try
      {
	 Connector connector;
	 int size = s.readInt();
	 for (int i = 0; i < size; ++i)
	 {
	    // This block of code is pretty tricky. There are a lot of corner cases being dealt with,
	    // and note that the same Start can be sent several times, so that is checked. 
	    connector = (Connector) s.readObject();
	    Start sm = (Start) s.readObject();
	    boolean hasEnd = (Boolean) s.readObject();

	    if (sm != null && !_dataModel.getConnectors().contains(sm)) { // Note that each Start will be added one single time.
	       // Wipe out old connectors for this Start, so that we don't automatically add them in the model.
	       sm.setConnectors(new ArrayList<Connector>()); 
	       _dataModel.addConnector(sm);
	    }
	    
	    if (sm != null) sm.addConnector(connector); // Note the ordering: this must come after previous clause.

	    if (!hasEnd) {
	       if (sm != null ) {
		  for (Connector c : sm.getConnectors()) {
		     if (c.getId() == connector.getId()) {
			c.setEndId(-1);
			break;
		     }
		  }
	       }
	       else connector.setEndId(-1);
	    }


	 }
      }
      catch (IOException e)
      {
	 System.err.println("i/o exception");
	 e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
	 System.err.println("class not found");
	 e.printStackTrace();
      }
   }


   /**
    * 
    * A very tricky function which resets all the IDs in the data model.
    * This means that node IDs are updated, and start IDs and end IDs in Connectors must be
    * updated to match, as well as the initial node ID.
    * Call judiciously.
    * 
    *  Note: This function is called by SB_Canvas.deleteSelection method.
    *  Whenever a canvas object is deleted, all the IDS in the data model is reset.
    * 
    * @param oldInitialId
    */
   protected void resetIds(int oldInitialId) {
	   
	   SB_Rectangle oldInitial = null;

	   SB_Element element;
	   int rectangleId = 0;
	   int conditionId = 0;


	   // Update node IDs, while simultaneously finding initial node 
	   for (int i = 0; i < _elements._drawables.size(); i++) {
		   element = (SB_Element) _elements._drawables.get(i);
		   Start correspondingStart = null; // Start which corresponds to this element (if any)


		   //TODO: O(n^2) is unfortunate...
		   for (Start s : _dataModel.getConnectors()) {
			   if (s.getId() == element.getId() && s.getType() == SB_Element.getType(element)) {
				   correspondingStart = s;
				   break;
			   }
		   }


		   if (element instanceof SB_Rectangle)
		   {
			   if (((SB_Drawable) _elements._drawables.get(i)).getId() == oldInitialId)
				   oldInitial =  (SB_Rectangle) _elements._drawables.get(i);

			   element.setId(rectangleId);
			   ++rectangleId;  
		   }
		   else
		   {
			   element.setId(conditionId);
			   ++conditionId;
		   }
		   
		   if (correspondingStart != null) { 
			  correspondingStart.setId(element.getId());
			  correspondingStart.setType(SB_Element.getType(element));
		   }
	   }
	   _connectors.resetIds();

	   // Update end IDs and start IDs to match the new IDs.
	   for (Object conn : _connectors._drawables) {
		   SB_Connector sbConnector = (SB_Connector) conn;
		   SB_Element end = sbConnector.getEndElement();
		   int newEndId = -1;
		   if (end != null) {
			   newEndId = end.getId();
		   } 

		   sbConnector.getDataModel().setEndId(newEndId); 
		   
		   SB_Element start = sbConnector.getStartElement();
		   int newStartId = -1;
		   //int oldStartId = -1;
		   Start startModel = sbConnector.getStartModel();
		   if (startModel != null) {
			   //oldStartId = startModel.getId();
			   if (start != null) {
				   newStartId = start.getId();
			   }
			   startModel.setId(newStartId);
		   }
		   
	   }

	   // update the initial node id.
	   int newInitialId = -1;
	   if (oldInitial != null) {
		   newInitialId = oldInitial.getId();
	   } 
	   
	   
	   _elements.setInitial(oldInitial); // initial node can be null
	   _dataModel.getNodes().setInitial(newInitialId); // initial id can be -1
   }

   public String getIndicesLabel() {
      String label = (String) getIndices().get(0);
      int size = getIndices().size();
      for (int i = 1; i < size; ++i)
	 label += " " + (String) getIndices().get(i);
      return label;
   }

   public void updateIndices(int type, int index, String name1, String name2) {
      if (_parent.isCore()) return;

      Vector indicesVector = getIndices();

      switch (type) {
      case SB_Catalog.kPolyIndexInsert:
	 if (index == indicesVector.size()) {
	    indicesVector.add(name1);
	    setModified(true);
	 }
	 break;
      case SB_Catalog.kPolyIndexDelete:
	 if (index < indicesVector.size()) {
	    indicesVector.remove(index);
	    setModified(true);
	 }
	 break;
      case SB_Catalog.kPolyIndexRename:
	 if (index < indicesVector.size()) {
	    if (name1.equals((String) indicesVector.get(index))) {
	       indicesVector.set(index, name2);
	       setModified(true);
	    }
	 }
	 break;
      case SB_Catalog.kPolyIndexMoveUp:
	 if (index < indicesVector.size()) {
	    indicesVector.set(index - 1, indicesVector.set(index, indicesVector
		  .get(index - 1)));
	    setModified(true);
	 }
	 break;
      case SB_Catalog.kPolyIndexMoveDown:
	 if (index < indicesVector.size() - 1) {
	    indicesVector.set(index + 1, indicesVector.set(index, indicesVector
		  .get(index + 1)));
	    setModified(true);
	 }
	 break;
      case SB_Catalog.kPolyIndexSelect:
	 if (index >= indicesVector.size()) indicesVector.setSize(index + 1);
	 indicesVector.set(index, name1);
	 setModified(true);
	 break;
      default:
	 break;
      }

      setIndices(indicesVector);
   }

   public void updateLocalsEditable() {
      int size = getLocals().getChildCount();
      for (int i = 0; i < size; ++i) {
	 DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) getLocals()
	       .getChildAt(i);
	 ((SB_Variable) childNode.getUserObject()).setEditable(!_parent
	       .isCore());
      }
   }

   protected Vector copyIndices() {
      Vector copy = new Vector();
      int size = getIndices().size();
      for (int i = 0; i < size; ++i) {
	 String index = (String) getIndices().get(i);
	 copy.add(new String(index));
      }
      return copy;
   }

   protected void copyLocals(DefaultMutableTreeNode locals) {
      getLocals().removeAllChildren();
      getDataModel().getLocals().clear();
      int size = locals.getChildCount();
      if (size == 0) return;
      DefaultMutableTreeNode childNode;
      SB_Variable var, copyVar;
      for (int i = 0; i < size; ++i) {
	 childNode = (DefaultMutableTreeNode) locals.getChildAt(i);
	 var = (SB_Variable) childNode.getUserObject();
	 Local localModel = new Local();
	 localModel.setName(var.getName());
	 localModel.setType(var.getType());
	 getDataModel().addLocal(localModel);
	 copyVar = new SB_Variable(localModel);
	 copyVar.setEditable(var.isEditable());
	 copyVar.setUserData(var.getUserData());
	 getLocals().add(new DefaultMutableTreeNode(copyVar));
      }
   }

   public boolean isModified() {
      return _modified;
   }

   public void setModified(boolean modified) 
   {
      _modified = modified;
      if(modified) 
      {
	 //if (_parent.getEditor()._saveItem != null)
	 _parent.getEditor().setDirty(true);
	 System.out.println("BTN modified: Behavior " + _parent.getName()
	       + ", Poly [" + getIndicesLabel() + "]");
      }
   }

   public void addToUndoStack(SB_CanvasMomento momento) {
      int size = _redoStack.size();
      for (int i = 0; i < size; ++i)
	 ((SB_CanvasMomento) _redoStack.get(i))._selection.reset();
      _redoStack.removeAllElements();
      if (_momento != null) {
	 _momento._selection.reset();
	 _momento = null;
      }
      if (momento == null) momento = new SB_CanvasMomento(this);
      size = _undoStack.size();
      if (size == MAX_STACK_SIZE)
	 ((SB_CanvasMomento) _undoStack.remove(0))._selection.reset();
      _undoStack.add(momento);

      if (_parent.getEditor().undoAction != null) {
	 _parent.getEditor().undoAction.setEnabled(true);
	 _parent.getEditor().redoAction.setEnabled(false);
      }
   }

   public void addToUndoStack() {
      addToUndoStack(null);
   }

   public boolean canUndo() {
      return !_undoStack.isEmpty();
   }

   protected void undo(SB_Canvas canvas) {
      int size = _undoStack.size();
      if (size == 0) return;

      if (_redoStack.size() == MAX_STACK_SIZE)
	 ((SB_CanvasMomento) _redoStack.remove(0))._selection.reset();
      if (_momento == null) _momento = new SB_CanvasMomento(this);
      _redoStack.add(_momento);
      if (_parent.getEditor().redoAction != null)
	 _parent.getEditor().redoAction.setEnabled(true);

      _momento = (SB_CanvasMomento) _undoStack.remove(size - 1);
      _momento.restore(canvas);
      if (size == 1 && _parent.getEditor().undoAction != null)
	 _parent.getEditor().undoAction.setEnabled(false);
   }

   public boolean canRedo() {
      return !_redoStack.isEmpty();
   }

   protected void redo(SB_Canvas canvas) {
      int size = _redoStack.size();
      if (size == 0) return;

      if (_undoStack.size() == MAX_STACK_SIZE)
	 ((SB_CanvasMomento) _undoStack.remove(0))._selection.reset();
      if (_momento == null) _momento = new SB_CanvasMomento(this);
      _undoStack.add(_momento);
      if (_parent.getEditor().undoAction != null)
	 _parent.getEditor().undoAction.setEnabled(true);

      _momento = (SB_CanvasMomento) _redoStack.remove(size - 1);
      _momento.restore(canvas);
      if (size == 1 && _parent.getEditor().redoAction != null)
	 _parent.getEditor().redoAction.setEnabled(false);
   }

   public void checkError(SB_ErrorInfo errorInfo, SB_TypeManager typeManager,I_CompileValidator validator) {
      _elements.updateComplex(_parent.getEditor());

      // local variables
      int size = getLocals().getChildCount();
      DefaultMutableTreeNode treeNode;
      SB_Variable local;
      for (int i = 0; i < size; ++i) {
	 treeNode = (DefaultMutableTreeNode)  getLocals().getChildAt(i);
	 local = (SB_Variable) treeNode.getUserObject();
	 validator.validateLocalVariable(local,this);
      }

      // rectangles and conditions
      _elements.checkError(this, errorInfo, validator);

      // connectors
      _connectors.checkError(this, errorInfo, validator);

   }

   public void writeExternal(ObjectOutput out) throws IOException {
      super.writeExternal(out);

      out.writeObject(_parent);
      out.writeObject(_elements);
      out.writeObject(_connectors);
      out.writeObject(_dataModel);
      out.writeObject(_locals);
      out.writeObject(_lastViewRect);
   }

   public void readExternal(ObjectInput in) throws ClassNotFoundException,
   IOException {
      super.readExternal(in);

      _parent = (SB_Behavior) in.readObject();
      _elements = (SB_ElementComposite) in.readObject();
      _connectors = (SB_ConnectorComposite) in.readObject();
      _dataModel = (Poly)in.readObject();
      _locals = (DefaultMutableTreeNode) in.readObject();
      _lastViewRect = (Rectangle) in.readObject();
   }

   private void setLocals(DefaultMutableTreeNode locals) {
      _locals = locals;
   }

  

   public int findOccurrences(Pattern pattern, String strReplace)
	 throws SB_CancelException {
      int total = 0;
      total += _elements.findOccurrences(pattern, strReplace, this);
      total += _connectors.findOccurrences(pattern, strReplace, this);
      return total;
   }

   /**
    * @return Returns the indices.
    */
   public Vector getIndices() {
      return new Vector(getDataModel().getIndices());
   }

   /**
    * @param indices
    *            The indices to set.
    */
   public void setIndices(Vector indices) {
      getDataModel().setIndices(new ArrayList<String>(indices));
   }

   /**
    * @return Returns the parent.
    */
   public SB_Behavior getParent() {
      return _parent;
   }

   /**
    * Sets the parent behavior.
    */
   public void setParent(SB_Behavior parent)
   {
      _parent = parent;
   }

   /**
    * @return Returns the locals.
    */
   public DefaultMutableTreeNode getLocals() {
      return _locals;
   }

   /**
    * Determines all behaviors that can be invoked directly from this behavior.
    * @return set of behavior names
    */
   public Set getReferencedBehaviors()
   {
      Set behaviorNames = new HashSet();

      Iterator elemIt = getElements()._drawables.iterator();
      while (elemIt.hasNext())
      {
	 SB_Element element = (SB_Element)elemIt.next();
	 if (element instanceof SB_Rectangle)
	 {
	    String expression = ((SB_Rectangle)element).getExpr();
	    int index = expression.indexOf('(');
	    if (index != -1)
	    {
	       behaviorNames.add( expression.substring(0, index) );
	    }
	 }
      }

      return behaviorNames;
   }

   /**
    * Searches this polymorphism for all elements (nodes, conditions, connectors)
    * that meet the given find criteria.
    * @param matcher
    * @return the list of matching elements
    */
   public List findElements(FindMatcher matcher)
   {
      SB_Rectangle initialNode = _elements.getInitial();
      List matches = new ArrayList();
      findElementsAux(initialNode,matcher,matches,new HashSet());
      return matches;
   }

   /**
    * Recursively searches for all elements matching the given condition.
    * @param currrentElement
    * @param matcher
    * @param matchesSoFar
    * @param alreadyVisited
    */
   private void findElementsAux(SB_Drawable currentElement,FindMatcher matcher,List matchesSoFar,HashSet alreadyVisited)
   {
      // check if we've visited this element before (to avoid getting stuck in cycles)
      if (alreadyVisited.contains(currentElement))
	 return;

      alreadyVisited.add(currentElement);

      // check if this element matches the criteria
      if (matcher.matches(currentElement)) {
	 matchesSoFar.add(currentElement);
      }

      // continue searching from this element
      if (currentElement instanceof SB_Connector) {
	 // check the endpoint of this connector
	 SB_Element destElement = ((SB_Connector)currentElement).getEndElement();
	 findElementsAux(destElement,matcher,matchesSoFar,alreadyVisited);
      }
      else {
	 // search down each outgoing path from this node until the target is found
	 Vector outConnectors = ((SB_Element)currentElement)._connectors;
	 for (int i=0; i < outConnectors.size(); ++i) {
	    SB_Connector conn = (SB_Connector)outConnectors.get(i);
	    findElementsAux(conn.getEndElement(),matcher,matchesSoFar,alreadyVisited);
	 }
      }
   }

}
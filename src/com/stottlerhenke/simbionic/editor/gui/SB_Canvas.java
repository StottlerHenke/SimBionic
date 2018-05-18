
package com.stottlerhenke.simbionic.editor.gui;

import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.COPY_NODE;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.CUT_NODE;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_COMMAND;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_LINK;
import static com.stottlerhenke.simbionic.editor.SimBionicEditor.DELETE_NODE;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.stottlerhenke.simbionic.common.xmlConverters.model.ActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Binding;
import com.stottlerhenke.simbionic.common.xmlConverters.model.CompoundActionNode;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Condition;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Connector;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Start;
import com.stottlerhenke.simbionic.editor.SB_Binding;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.api.EditorRegistry;
import com.stottlerhenke.simbionic.editor.gui.api.I_EditorListener;
import com.stottlerhenke.simbionic.editor.gui.api.I_ExpressionEditor;

/**
 * Represents the UI where the user can create a behavior.
 */
public class SB_Canvas extends JPanel implements MouseListener, MouseMotionListener,
        DropTargetListener
{
    protected SimBionicEditor _editor;

    protected boolean debugMode = false;
    
    protected static final int MAX_WIDTH = 2500;
    protected static final int MAX_HEIGHT = 2500;

    protected SB_Polymorphism _poly = null;
    protected Point _point = new Point();
    protected Point _downPoint = new Point();
    protected SB_DragType _dragType = new SB_DragType();
    protected Rectangle _dragRect = new Rectangle();
    public SB_Drawable _selDrawable = null;
    protected SB_Element _element = null;
    protected static SB_Connector _dragConnector = null; // safe allocation
    // since one canvas
    // active at a time
    protected SB_Element _downStartElement = null;
    protected SB_Element _downEndElement = null;
    protected Point _downStartPoint = new Point();
    protected Point _downEndPoint = new Point();
    protected int _downPriority = -1;
    protected boolean _lastButton1 = false;
    protected SB_CanvasMomento _momento = null;
    protected boolean _needToScroll = false;

    final static int kDropUnknown = 0;
    final static int kDropAction = 1;
    final static int kDropPredicate = 2;
    final static int kDropNonBooleanPredicate = 10;
    final static int kDropBehavior = 3;
    final static int kDropParameter = 4;
    final static int kDropParameterNA = 5;
    final static int kDropConstant = 6;
    final static int kDropGlobal = 7;
    final static int kDropLocal = 8;
    final static int kDropFolder = 9;

    protected DropTarget _dropTarget = null;
    protected boolean _allowDrop = true;
    protected int _dropType = kDropUnknown;

    public SB_Canvas(SimBionicEditor editor)
    {
        _editor = editor;

        setPreferredSize(new Dimension(MAX_WIDTH, MAX_HEIGHT));
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);
        addFocusListener(new FocusListener()
        {
            public void focusGained(FocusEvent event)
            {
                // System.out.println("canvas focus gained");
                updateEditItems();
            }

            public void focusLost(FocusEvent event)
            {
            }
        });

        // setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        _dropTarget = new DropTarget(this, this);
    }

    // convenience accessors

    SB_TabbedCanvas getTabbedCanvas()
    {
        return (SB_TabbedCanvas) ComponentRegistry.getContent();
    }

    public static SB_Connector getDragConnector()
    {
        return _dragConnector;
    }

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        //Enable rendering hints for improved graphics.
        Map<?, ?> desktopHints = (Map<?, ?>) Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints");
        if (desktopHints != null) {
        	g2.setRenderingHints(desktopHints);
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint( RenderingHints.  KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        if (_poly != null)
        {
        	g2.setFont(SB_Drawable.font);
        	_poly.draw(g2);
        }

        if (_dragType.type == SB_DragType.kMakeSelection)
        {
        	g2.setStroke(SB_Drawable.dashed);
        	Rectangle rect = new Rectangle(_point);
        	rect.add(_downPoint);
        	g2.draw(rect);
        	g2.setStroke(SB_Drawable.stroke);
        }

        if (_needToScroll)
        {
        	scrollRectToVisible(_poly._lastViewRect);
        	_needToScroll = false;
        }
    }

    public void mousePressed(MouseEvent e)
    {
        if (_poly == null)
            return;

        JFrame frame = ComponentRegistry.getFrame();
        if (frame != null)
        {
            Component component = frame.getFocusOwner();
            if (component instanceof SB_Autocomplete)
                ComponentRegistry.getToolBar().handleFocusLost((SB_Autocomplete) component);
        }
        requestFocus();

        _point.x = e.getX();
        _point.y = e.getY();

        boolean shiftPressed = e.isShiftDown();
        boolean controlPressed = e.isControlDown() || (_editor.toolbar != null && _editor.toolbar.getHackConnectorButton().isSelected()); // TODO rth remove ARASCMI hack
        boolean slideCopy = false;
        if (shiftPressed && controlPressed)
        {
            shiftPressed = false;
            controlPressed = false;
            slideCopy = true;
        }
        if (_poly.getParent().isCore())
        {
            controlPressed = false;
            slideCopy = false;
        }

        boolean button1 = (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0;
        if (e.getClickCount() == 2 && button1 && _lastButton1)
        {
            mouseDoubleClicked(e);
            return;
        }
        _lastButton1 = button1;

        if ((e.getModifiers() & (InputEvent.BUTTON2_MASK | InputEvent.BUTTON3_MASK)) != 0
                && !controlPressed && !slideCopy)
        {
            mouseRightPressed(e);
            return;
        }

        _dragConnector = _poly.getConnectors().containsPoint_StartEnd(_point, _dragType);
        if (_dragConnector != null)
        {
            if (_dragType.type == SB_DragType.kDragStartPoint)
            {
                SB_Connector dragConnector = _poly.getConnectors().containsPoint_End(_point);
                if (dragConnector != null)
                {
                    _dragConnector = dragConnector;
                    _dragType.type = SB_DragType.kDragEndPoint;
                }
            }
            _poly.setHighlighted(false);
            _dragConnector.setHighlighted(true);
        }

        SB_Drawable drawable = null;
        if (_dragConnector == null)
        {
            drawable = _poly.containsPoint(_point);
            if (drawable != null && !controlPressed)
            {
                boolean isHighlighted = !shiftPressed || !drawable.isHighlighted();
                if (isHighlighted)
                {
                    if (!shiftPressed && !drawable.isHighlighted())
                        _poly.setHighlighted(false);
                    _dragType.type = SB_DragType.kDragElements;
                }
                drawable.setHighlighted(isHighlighted);
            }
        }

        if (slideCopy && (drawable instanceof SB_Element))
        {
            _poly.addToUndoStack();
            SB_Element element = (SB_Element) drawable;
            if (element instanceof SB_Rectangle) {
                ActionNode actionNodeModel = new ActionNode();
                _element = new SB_Rectangle(actionNodeModel);
            }
            else {
                Condition conditionModel = new Condition();
                _element = new SB_Condition(conditionModel);
            }
            _element.setExpr(element.getExpr());
            _element.setBindings(SB_BindingsTable.copyBindings(element.getBindings()));
            _poly.setHighlighted(false);
            _element.setHighlighted(true);
            Rectangle rect = element.getRect();
            _element.setCenter((int) rect.getCenterX(), (int) rect.getCenterY());
            _poly.add(_element);
            _dragType.type = SB_DragType.kSlideElement;
        }

        _downPoint.x = _point.x;
        _downPoint.y = _point.y;
        if (_dragConnector == null && drawable == null)
        {
            if (!shiftPressed)
                _poly.setHighlighted(false);
            _poly.updatePrehighlight();
            _dragType.type = SB_DragType.kMakeSelection;
        }

        if (controlPressed)
        {
            if (drawable instanceof SB_Element)
                _element = (SB_Element) drawable;
            else
                _element = null;
            _dragType.type = SB_DragType.kNewConnectorPending;            
        } else if (_dragType.type == SB_DragType.kDragElements
                || _dragType.type == SB_DragType.kSlideElement
                || _dragType.type == SB_DragType.kDragStartPoint
                || _dragType.type == SB_DragType.kDragEndPoint)
        {
            _poly.bringHighlightToFront();
            if (_dragType.type == SB_DragType.kDragStartPoint
                    || _dragType.type == SB_DragType.kDragEndPoint)
            {
                _momento = new SB_CanvasMomento(_poly);
                _downStartElement = _dragConnector.getStartElement();
                _downEndElement = _dragConnector.getEndElement();
                _downStartPoint.setLocation(_dragConnector.getStartPoint());
                _downEndPoint.setLocation(_dragConnector.getEndPoint());
                _downPriority = _dragConnector.getPriority();
                _dragConnector.updatePoints();
                if (_dragType.type == SB_DragType.kDragStartPoint)
                {
                    Point delta = new Point(_dragConnector.getStartPoint());
                    delta.x = _downStartPoint.x - delta.x;
                    delta.y = _downStartPoint.y - delta.y;
                    _dragConnector.offsetStartPoint(delta.x, delta.y);
                } else
                {
                    Point delta = new Point(_dragConnector.getEndPoint());
                    delta.x = _downEndPoint.x - delta.x;
                    delta.y = _downEndPoint.y - delta.y;
                    _dragConnector.offsetEndPoint(delta.x, delta.y);
                }
                if (_dragConnector.isTwoWay())
                    _dragConnector.updatePoints();
            }
        }

        computeDragRect(_point);

        if (_dragType.type == SB_DragType.kDragElements)
            _dragType.type = SB_DragType.kDragPending;

        if (_poly.getParent().isCore() && _dragType.type != SB_DragType.kMakeSelection)
            _dragType.type = SB_DragType.kDragNone;

        if(_editor.toolbar != null)
            _editor.toolbar.getHackConnectorButton().setSelected(false);    // TODO rth remove ARASCMI hack

        updateSingle();
        repaint();
    }

    void computeDragRect(Point point)
    {
        Rectangle rect = new Rectangle(0, 0, MAX_WIDTH, MAX_HEIGHT);
        Rectangle ur = new Rectangle();
        if (_dragType.type == SB_DragType.kDragElements
                || _dragType.type == SB_DragType.kSlideElement)
        {
            ur = _poly.unionRect(ur, true);
        } else if (_dragType.type == SB_DragType.kDragStartPoint)
        {
            ur = _dragConnector.getStartRect();
        } else if (_dragType.type == SB_DragType.kDragEndPoint)
        {
            ur = _dragConnector.getEndRect();
        } else
        {
            _dragRect.setBounds(rect);
            return;
        }
        Point topLeft = new Point(rect.x, rect.y);
        Point bottomRight = new Point((int) rect.getMaxX(), (int) rect.getMaxY());

        _dragRect.x = point.x - (ur.x - rect.x);
        _dragRect.y = point.y - (ur.y - rect.y);
        _dragRect.width = 0;
        _dragRect.height = 0;
        _dragRect.add(point.x + ((int) (rect.getMaxX()) - ur.getMaxX()), point.y
                + ((int) (rect.getMaxY()) - ur.getMaxY()));
    }

    public void mouseDragged(MouseEvent e)
    {
        if (_poly == null || debugMode)
            return;

        Point point = new Point(e.getX(), e.getY());

        if (_dragType.type == SB_DragType.kDragPending)
        {
            int size_x = Math.abs(point.x - _downPoint.x);
            int size_y = Math.abs(point.y - _downPoint.y);
            if (size_x >= 3 || size_y >= 3)
            {
                _poly.addToUndoStack();
                _dragType.type = SB_DragType.kDragElements;
            } else
                return;
        } else if (_dragType.type == SB_DragType.kNewConnectorPending)
        {
            int size_x = Math.abs(point.x - _downPoint.x);
            int size_y = Math.abs(point.y - _downPoint.y);
            if (size_x >= 3 || size_y >= 3)
            {
                _poly.addToUndoStack();
                _poly.setHighlighted(false);
                Poly polyModel = _poly.getDataModel();
                int startType = SB_Element.getType(_element);
                Start startModel = null;
                if (_element != null) {
                  startModel = polyModel.getConnector(_element.getId(), startType);
                }
                Connector connectorModel = new Connector();
                _dragConnector = new SB_Connector(connectorModel, startModel);
                _dragConnector.setStartElement(_element, startModel);
                _dragConnector.setHighlighted(true);
                _dragConnector.offset(_downPoint.x, _downPoint.y, true);
                _dragConnector.updatePoints();
                _poly.add(_dragConnector);
                _poly.updateConnectorLabels(_dragConnector);
                updateSingle();
                // update menu items
                updateEditItems();
                _downPriority = -2;
                _dragType.type = SB_DragType.kDragEndPoint;
            } else
                return;
        }

        scrollRectToVisible(new Rectangle(point.x, point.y, 1, 1));

        int right = (int) _dragRect.getMaxX();
        int bottom = (int) _dragRect.getMaxY();
        if (point.x < _dragRect.x)
            point.x = _dragRect.x;
        if (point.x > right)
            point.x = right;
        if (point.y < _dragRect.y)
            point.y = _dragRect.y;
        if (point.y > bottom)
            point.y = bottom;

        if (_dragType.type == SB_DragType.kDragElements
                || _dragType.type == SB_DragType.kSlideElement)
        {
            _poly.offset(point.x - _point.x, point.y - _point.y, true);
            _poly.getConnectors().updatePoints();
            repaint();
        } else if (_dragType.type == SB_DragType.kDragStartPoint
                || _dragType.type == SB_DragType.kDragEndPoint)
        {
            SB_Element element = (SB_Element) (_poly.getElements().containsPoint(point));
            if (_dragType.type == SB_DragType.kDragStartPoint)
            {
               Poly polyModel = _poly.getDataModel();
               Start startModel = null;
               if (element != null) {
                  startModel = polyModel.getConnector(element.getId(), SB_Element.getType(element));
               }
                _dragConnector.setStartElement(element, startModel);
                _dragConnector.offsetStartPoint(point.x - _point.x, point.y - _point.y);
                _poly.updateConnectorLabels(_dragConnector);
            } else
            {
                _dragConnector.setEndElement(element);
                _dragConnector.offsetEndPoint(point.x - _point.x, point.y - _point.y);
            }
            repaint();
        } else if (_dragType.type == SB_DragType.kMakeSelection)
        {
            Rectangle rect = new Rectangle(new Point(Math.min(point.x, _downPoint.x), Math.min(
                point.y, _downPoint.y)));
            rect.add(Math.max(point.x, _downPoint.x), Math.max(point.y, _downPoint.y));
            _poly.makeSelection(rect);
            updateSingle();
            repaint();
        }
        _point.x = point.x;
        _point.y = point.y;
    }

    public void mouseReleased(MouseEvent e)
    {
        if (_poly == null)
            return;

        if (_dragType.type == SB_DragType.kDragStartPoint
                || _dragType.type == SB_DragType.kDragEndPoint)
        {
            SB_Connector dragConnector = _dragConnector;
            _dragConnector = null; // allows connector to check for loop
            dragConnector.updatePoints();
            if (dragConnector.getStartElement() != _downStartElement
                    || dragConnector.getEndElement() != _downEndElement
                    || !dragConnector.getStartPoint().equals(_downStartPoint)
                    || !dragConnector.getEndPoint().equals(_downEndPoint)
                    || dragConnector.getPriority() != _downPriority)
            {
                if (_momento != null)
                {
                    _poly.addToUndoStack(_momento);
                    _momento = null;
                }
                _poly.setModified(true);
            } else
            {
                _momento._selection.reset();
                _momento = null;
            }
        } else if (_dragType.type != SB_DragType.kDragNone
                && _dragType.type != SB_DragType.kDragPending
                && _dragType.type != SB_DragType.kNewConnectorPending
                && _dragType.type != SB_DragType.kMakeSelection)
        {
            _poly.setModified(true);
        }
        _dragType.type = SB_DragType.kDragNone;
        repaint();
    }

    void mouseDoubleClicked(MouseEvent e)
    {
        boolean controlPressed = e.isControlDown();
        if (controlPressed)
        {
            SB_Drawable drawable = _poly.containsPoint(_point);
            if (drawable == null)
            {
                _poly.setHighlighted(false);
                clearSingle();
                repaint();
            } else if (!drawable.isHighlighted())
            {
                _poly.setHighlighted(false);
                drawable.setHighlighted(true);
                updateSingle();
                repaint();
            }
        }
        if (_selDrawable instanceof SB_Rectangle)
        {
            SB_Rectangle rectangle = (SB_Rectangle) _selDrawable;
            if (rectangle.isBehavior() && !controlPressed)
            {
                getTabbedCanvas().setBehavior(rectangle.getExpr(), true);
                return;
            }
        }
        if (!_poly.getParent().isCore())
        {
        	if (_selDrawable instanceof SB_Element && !(_selDrawable instanceof SB_MultiRectangle)) {
        		ComponentRegistry.getToolBar().showExpressionDialog();
        	}
        	else if (_selDrawable instanceof SB_BindingsHolder) {
        		ComponentRegistry.getToolBar().showBindingsDialog(false);
        	}
        }
    }

    void mouseRightPressed(MouseEvent e)
    {
        _downPoint.x = _point.x;
        _downPoint.y = _point.y;

        SB_Drawable drawable = _poly.containsPoint(_point);
        if (drawable == null)
        {
            _poly.setHighlighted(false);
            clearSingle();
            repaint();
            getTabbedCanvas().handleCanvasPopup(this);
        } else if (!drawable.isHighlighted())
        {
            _poly.setHighlighted(false);
            drawable.setHighlighted(true);
            updateSingle();
            repaint();
        }
        if (drawable instanceof SB_Element)
        {
            SB_Element element = (SB_Element) drawable;
            getTabbedCanvas().handleElementPopup(this, element);
        } else if (drawable instanceof SB_Connector)
        {
            SB_Connector connector = (SB_Connector) drawable;
            getTabbedCanvas().handleConnectorPopup(this, connector);
        }
    }

    void deleteSelection()
    {

       Poly polyModel = _poly.getDataModel();
       int oldInitialId = polyModel.getNodes().getInitial();
       
        _poly.addToUndoStack();
        _poly.removeHighlightDependencies();
        _poly.removeHighlight();
        _poly.resetIds(oldInitialId);
        _poly.getConnectors().updateTwoWay();
        updateSingle();
        repaint();
        _poly.setModified(true);
        _poly.updateConnectorLabels(null);
    }

    void selectAll()
    {
        _poly.setHighlighted(true);
        updateSingle();
        repaint();
    }

    public SB_Connector insertConnector(SB_Element startElement, SB_Element endElement)
    {
        _poly.addToUndoStack();
        Poly polyModel = _poly.getDataModel();
        int startType = SB_Element.getType(startElement); 
        Start startModel = polyModel.getConnector(startElement.getId(), startType);
        Connector connectorModel = new Connector();
        SB_Connector connector = new SB_Connector(connectorModel, startModel);
        connector.setStartElement(startElement, startModel);
        connector.setEndElement(endElement);
        connector.updatePoints();
        _poly.add(connector);
        _poly.updateConnectorLabels(connector);
        return connector;
    }

    public SB_Rectangle insertAction(Point point, String expr)
    {
        ActionNode actionNodeModel = new ActionNode();
        actionNodeModel.setLabelMode(SB_Element.FULL_LABEL);
        Poly polyModel = _poly.getDataModel();
        polyModel.getNodes().addActionNode(actionNodeModel);
        SB_Rectangle action = new SB_Rectangle(actionNodeModel);
        insertElement(action, point, expr);
        return action;
    }

    public SB_MultiRectangle insertCompoundAction(Point point)
    {
      CompoundActionNode compoundActionNode = new CompoundActionNode();
      compoundActionNode.setLabelMode(SB_Element.FULL_LABEL);
      Poly polyModel = _poly.getDataModel();
      polyModel.getNodes().addCompoundActionNode(compoundActionNode);
    	SB_MultiRectangle action = new SB_MultiRectangle(compoundActionNode);
    	insertElement(action, point, "");
    	return action;
    }

    private SB_MultiRectangle insertCompoundActionWithSingleAction(Point point,
            String actionExpr) {
        SB_MultiRectangle compound = insertCompoundAction(point);
        Binding bindingModel = new Binding();
        bindingModel.setVar(SB_Binding.ACTION_BINDING);
        bindingModel.setExpr(actionExpr);
        SB_Binding binding = new SB_Binding(bindingModel);
        compound.addBinding(binding);
        return compound;
    }

    public SB_Condition insertCondition(Point point, String expr)
    {
       Condition conditionModel = new Condition();
       conditionModel.setLabelMode(SB_Element.FULL_LABEL);
       Poly polyModel = _poly.getDataModel();
       polyModel.addCondition(conditionModel);
        SB_Condition condition = new SB_Condition(conditionModel);
        insertElement(condition, point, expr);
        return condition;
    }

    void insertElement(final SB_Element element, Point point, String expr)
    {
        _poly.addToUndoStack();
        _poly.setHighlighted(false);
        element.setHighlighted(true);
        element.offset(point.x - 50, point.y - 37, true);
        element.setExpr(expr);
        _poly.add(element);
        updateSingle();
        repaint();
        _poly.setModified(true);
        
        int paren = expr.indexOf('(');
		String subtype = (paren > 0) ? expr.substring(0, paren) : "";
		I_ExpressionEditor editor = _editor.getEditorRegistry()
				.getExpressionEditor(EditorRegistry.EXPRESSION_TYPE_FUNCTION, subtype, expr);
		if (editor != null) {
			editor.editObject(expr, new I_EditorListener() {
				public void editingCanceled(I_ExpressionEditor source) {
				}

				public void editingCompleted(I_ExpressionEditor source,
						String result) {
					_poly.addToUndoStack();
					element.setExpr(result);
					_selDrawable = null;
					updateSingle();
					repaint();
					_poly.setModified(true);
				}
			});
		}
    }

    public void mouseMoved(MouseEvent e)
    {
        _point.x = e.getX();
        _point.y = e.getY();
        SB_Drawable drawable = _poly.containsPoint(_point);
        if (drawable instanceof SB_CommentHolder)
        {
            SB_CommentHolder holder = (SB_CommentHolder) drawable;
            // if (element._exprTrunc != null && element.getExpr().length() >
            // element._exprTrunc.length())
            if (holder.getLabelMode() != SB_CommentHolder.COMMENT_LABEL)
            {
                if (holder.getComment() != null && holder.getComment().length() > 0)
                    setToolTipText(holder.getComment());
                else
                    setToolTipText("(no user comment)");
            } else
            {
                if (holder instanceof SB_Element)
                {
                    SB_Element element = (SB_Element) holder;
                    String text = element.getExpr();
                    // if (text.length() == 0)
                    // text = "None";
                    if (element._bindingsString != null)
                        text = "<HTML>" + element._bindingsString + "<P>" + text;
                    setToolTipText(text);
                } else
                {
                    SB_Connector connector = (SB_Connector) holder;
                    setToolTipText(connector._bindingsString);
                }
            }
        } else
            setToolTipText(null);
    }

    public void mouseClicked(MouseEvent e)
    {
        updateEditItems();
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }

    boolean updateSingle()
    {
        if (_poly == null)
            return false;

        boolean core = _poly.getParent().isCore();
        // if (_editor.getMenuBar() != null)
        // {
        // boolean isHighlighted = _poly.isHighlighted();
        // _editor._cutAction.setEnabled(!core && isHighlighted);
        // _editor._copyAction.setEnabled(isHighlighted);
        // _editor._deleteItem.setEnabled(!core && isHighlighted);
        // }

        SB_Drawable selDrawable = _poly.singleHighlight();
        if (selDrawable != _selDrawable)
        {
            if (selDrawable == null)
            {
                clearSingle();
            } else
            {
                SB_ToolBar toolBar = ComponentRegistry.getToolBar();
                toolBar._exprField._ignoreCaretUpdate = true;

                _selDrawable = selDrawable;
                
                if (_selDrawable instanceof SB_Element &&
                		!(_selDrawable instanceof SB_MultiRectangle))
                {
                    SB_Element element = (SB_Element) _selDrawable;
                    toolBar._exprField.setText(element.getExpr());
                    toolBar._exprField.setCaretPosition(0);
                    toolBar._exprField.setEnabled(!core && !debugMode);
                    toolBar._exprAction.setEnabled(!core && !debugMode);
                } else
                {
                    toolBar._exprField.setText("");
                    toolBar._exprField.setEnabled(false);
                    toolBar._exprAction.setEnabled(false);
                }
                toolBar._exprField._ignoreCaretUpdate = false;

            }
            return true;
        }
        return false;
    }

    public void clearSingle()
    {
        _selDrawable = null;
        SB_ToolBar toolBar = ComponentRegistry.getToolBar();
        toolBar._exprField._ignoreCaretUpdate = true;
        toolBar._exprField.setText("");
        toolBar._exprField.setEnabled(false);
        toolBar._exprField._ignoreCaretUpdate = false;
        toolBar._exprAction.setEnabled(false);
    }

    protected void updateEditItems()
    {
        // if ((SimBionic)_editor).gets) == null) return;

        boolean core = _poly.getParent().isCore();

        _editor.undoAction.setEnabled(!core && _poly.canUndo() && !debugMode);
        _editor.redoAction.setEnabled(!core && _poly.canRedo() && !debugMode);

        boolean isHighlighted = _poly.isHighlighted();
        _editor.cutAction.setEnabled(!core && isHighlighted && !debugMode);
        _editor.deleteAction.setEnabled(!core && isHighlighted && !debugMode);
        _editor.copyAction.setEnabled(isHighlighted);
        _editor.pasteAction.setEnabled(!core
                && SB_TabbedCanvas._clipboard.getContents(this) != null && !debugMode);
        
       

        // update Cut, Copy, and Delete action names.
        int numHighlighted = _poly.countHighlight();
        boolean isNodeSelected = (numHighlighted == 1) && _poly.isNodeHighlighted(); 
        boolean isLinkSelected = (numHighlighted == 1) && _poly.isLinkHighlighted();
        
        String copyActionName = COPY_COMMAND;
        if (_editor.copyAction.isEnabled()) {
        	if (isNodeSelected) {
        		copyActionName = COPY_NODE;
        	} else if (isLinkSelected) {
        		copyActionName = COPY_LINK;
        	}  
        } 
        _editor.copyAction.putValue(Action.NAME, copyActionName);
        
        String cutActionName = CUT_COMMAND;
        if (_editor.cutAction.isEnabled()) {
        	if (isNodeSelected) {
        		cutActionName = CUT_NODE;
        	} else if (isLinkSelected) {
        		cutActionName = CUT_LINK;
        	}  
        } 
        _editor.cutAction.putValue(Action.NAME, cutActionName);
        
        String deleteActionName = DELETE_COMMAND;
        if (_editor.deleteAction.isEnabled()) {
        	if (isNodeSelected) {
        		deleteActionName = DELETE_NODE;
        	} else if (isLinkSelected) {
        		deleteActionName = DELETE_LINK;
        	}  
        } 
        _editor.deleteAction.putValue(Action.NAME, deleteActionName);
        
    }

    public void scrollToDrawable(SB_Drawable drawable)
    {
        // _poly.setHighlighted(false);
        // drawable.setHighlighted(true);
        // updateSingle();
        Rectangle rect;
        if (drawable instanceof SB_Element)
        {
            SB_Element element = (SB_Element) drawable;
            rect = element.getRect();
        } else
        {
            SB_Connector connector = (SB_Connector) drawable;
            rect = connector.getStartRect();
            rect = rect.union(connector.getStartRect());
        }
        JScrollPane scrollPane = (JScrollPane) getTabbedCanvas().getSelectedComponent();
        Rectangle rectViewport = scrollPane.getViewport().getViewRect();
        if (!rectViewport.contains(rect))
        {
            Rectangle rectGrow = new Rectangle(rect);
            int h = Math.max((rectViewport.width - rectGrow.width) / 2, 0);
            int v = Math.max((rectViewport.height - rectGrow.height) / 2, 0);
            rectGrow.grow(h, v);
            scrollRectToVisible(rectGrow);
        }
        repaint();
    }

    public void dragEnter(DropTargetDragEvent event)
    {
        event.acceptDrag(DnDConstants.ACTION_MOVE);
        _poly.setHighlighted(false);
        repaint();
    }

    public void dragExit(DropTargetEvent event)
    {
        _allowDrop = true;
    }

    public void dragOver(DropTargetDragEvent event)
    {
        Point point = event.getLocation();
        SB_Drawable drawable = _poly.containsPoint(point);
        _poly.setHighlighted(false);
        if (drawable != null)
            drawable.setHighlighted(true);
        if (updateSingle())
            repaint();
        _allowDrop = allowDrop();
    }

    boolean allowDrop()
    {
        if (_poly.getParent().isCore())
            return false;
        if (_dropType == kDropAction || _dropType == kDropBehavior
            || _dropType == kDropNonBooleanPredicate)
            return _selDrawable == null;
        else if (_dropType == kDropPredicate)
            return _selDrawable == null || hasWild(_selDrawable);
        else if (_dropType == kDropParameter || _dropType == kDropGlobal || _dropType == kDropLocal)
            return _selDrawable == null || (_selDrawable instanceof SB_BindingsHolder);
        else if (_dropType == kDropParameterNA)
            return false;
        else if (_dropType == kDropConstant)
            return hasWild(_selDrawable);
        else if (_dropType == kDropFolder)
            return false;
        return true;
    }

    boolean hasWild(SB_Drawable drawable)
    {
        if (drawable == null)
            return false;
        if (drawable instanceof SB_Element)
        {
            SB_Element element = (SB_Element) drawable;
            return element.getExpr().matches(".*[*].*");
        }
        return false;
    }

    void replaceWild(SB_Drawable drawable, String text)
    {
        if (drawable == null)
            return;
        if (drawable instanceof SB_Element)
        {
            _poly.addToUndoStack();
            SB_Element element = (SB_Element) drawable;
            // @kp changed for jdk1.3 compliance

            String replaced = element.getExpr().replaceFirst("[*]", text);
            element.setExpr(replaced);
            ComponentRegistry.getToolBar()._exprField.setText(element.getExpr());
            ComponentRegistry.getToolBar()._exprField.setCaretPosition(0);
            _poly.setModified(true);
        }
    }

    public void drop(DropTargetDropEvent event)
    {
        try
        {
            Transferable transferable = event.getTransferable();

            // we allow only Strings
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                event.acceptDrop(DnDConstants.ACTION_MOVE);
                String s = (String) transferable.getTransferData(DataFlavor.stringFlavor);

                int index = s.indexOf(':');
                if (index != -1)
                {
                    SB_Element element = null;
                    String elementType = s.substring(0, index);
                    String text = s.substring(index + 1);
                    if (elementType.equals("Action"))
                        _dropType = kDropAction;
                    else if (elementType.equals("Predicate"))
                        _dropType = kDropPredicate;
                    else if (elementType.equals("NonBooleanPredicate"))
                        _dropType = kDropNonBooleanPredicate;
                    else if (elementType.equals("Behavior"))
                        _dropType = kDropBehavior;
                    else if (elementType.equals("Parameter"))
                        _dropType = kDropParameter;
                    else if (elementType.equals("ParameterNA"))
                        _dropType = kDropParameterNA;
                    else if (elementType.equals("Constant"))
                        _dropType = kDropConstant;
                    else if (elementType.equals("Global"))
                        _dropType = kDropGlobal;
                    else if (elementType.equals("Local"))
                        _dropType = kDropLocal;
                    else if (elementType.equals("Folder"))
                        _dropType = kDropFolder;
                    else
                        _dropType = kDropUnknown;

                    if (_dropType != kDropUnknown && allowDrop())
                    {
                        boolean focusOnBinding = false;
                        switch (_dropType)
                        {
                        case kDropAction:
                            element = insertAction(event.getLocation(), text);
                            break;
                        case kDropNonBooleanPredicate:
                            element = insertCompoundActionWithSingleAction(
                                    event.getLocation(), text);
                            break;
                        case kDropPredicate:
                            if (_selDrawable == null)
                                element = insertCondition(event.getLocation(), text);
                            else
                                replaceWild(_selDrawable, text);
                            break;
                        case kDropBehavior:
                            element = insertAction(event.getLocation(), text);
                            ((SB_Rectangle) element).setIsBehavior(true);
                            break;
                        case kDropParameter:
                        case kDropGlobal:
                        case kDropLocal:
                            if (_selDrawable == null)
                            {
                                element = insertAction(event.getLocation(), "");
                                Binding bindingModel = new Binding();
                                bindingModel.setVar(text);
                                bindingModel.setExpr("");
                                element.addBinding(new SB_Binding(bindingModel));
                                focusOnBinding = true;
                            } else if (hasWild(_selDrawable))
                                replaceWild(_selDrawable, text);
                            else
                            {
                                if (_selDrawable instanceof SB_BindingsHolder)
                                {
                                    _poly.addToUndoStack();
                                    Binding bindingModel = new Binding();
                                    bindingModel.setVar(text);
                                    bindingModel.setExpr("");
                                    ((SB_BindingsHolder) _selDrawable).addBinding(new SB_Binding(bindingModel));
                                    focusOnBinding = true;
                                    _poly.setModified(true);
                                }
                            }
                            break;
                        case kDropConstant:
                            replaceWild(_selDrawable, text);
                            break;
                        }

                        if (focusOnBinding)
                        {
                            SB_Drawable drawable = _selDrawable;
                            _selDrawable = null;
                            updateSingle();
                        } else
                            requestFocus();
                        repaint();
                    }
                }
            }
            event.getDropTargetContext().dropComplete(true);
        } catch (IOException exception)
        {
            exception.printStackTrace();
            System.err.println("Exception" + exception.getMessage());
            event.rejectDrop();
        } catch (UnsupportedFlavorException ufException)
        {
            ufException.printStackTrace();
            System.err.println("Exception" + ufException.getMessage());
            event.rejectDrop();
        }
    }

    public void setDebugMode(boolean debugMode){
    	this.debugMode = debugMode;
    }
    
    public void dropActionChanged(DropTargetDragEvent event)
    {
    }

    /**
     * @return Returns the poly.
     */
    public SB_Polymorphism getPoly()
    {
        return _poly;
    }

    /**
     * @param poly
     *            The poly to set.
     */
    public void setPoly(SB_Polymorphism poly)
    {
        _poly = poly;
    }
    
    // XXX: MOTL
    public Point getCurrentPoint() {
        return _point;
    }
}

class SB_DragType
{
    final static int kDragNone = 0;
    final static int kDragElements = 1;
    final static int kDragPending = 2;
    final static int kDragStartPoint = 3;
    final static int kDragEndPoint = 4;
    final static int kNewConnectorPending = 5;
    final static int kMakeSelection = 6;
    final static int kSlideElement = 7;
    final static int kDropVar = 8;
    final static int kDropVarNewNode = 9;
    final static int kPasteSelection = 10;

    public int type = kDragNone;
}

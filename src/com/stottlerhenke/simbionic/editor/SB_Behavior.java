package com.stottlerhenke.simbionic.editor;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Poly;
import com.stottlerhenke.simbionic.editor.gui.ComponentRegistry;
import com.stottlerhenke.simbionic.editor.gui.SB_Descriptors;
import com.stottlerhenke.simbionic.editor.gui.SB_Line;
import com.stottlerhenke.simbionic.editor.gui.SB_Output;
import com.stottlerhenke.simbionic.editor.gui.SB_OutputBar;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;
import com.stottlerhenke.simbionic.editor.gui.SB_ProjectBar;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;

/**
 * Represents a behavior in the editor.
 */
public class SB_Behavior extends SB_Function implements Comparable
{
    private static final long serialVersionUID = 2302585093L + 4;

    /*
     * static final int kExec1 = 0; static final int kExec2 = 1; static final
     * int kExec3 = 2; static final String[] kExecNames = { "Run multi-tick,
     * interruptible", "Run in one tick, interruptible", "Run in one tick, no
     * interruption" };
     */

    public static final int kExecMultiTick = 0;
    public static final int kExecOneTick = 4;
    public static final int kExecUntilBlocked = 2;
    public static final String[] kExecNames =
    { "Run multi-tick", "Run in one tick", "Run until blocked" };

    public static int execToIndex(int exec)
    {
        switch (exec)
        {
        case kExecMultiTick:
            return 0;
        case kExecOneTick:
            return 1;
        case kExecUntilBlocked:
            return 2;
        default:
            return -1;
        }
    }

    public static int indexToExec(int index)
    {
        switch (index)
        {
        case 0:
            return kExecMultiTick;
        case 1:
            return kExecOneTick;
        case 2:
            return kExecUntilBlocked;
        default:
            return -1;
        }
    }

    public static final int kInterruptYes = 0;
    public static final int kInterruptNo = 1;
    public static final String[] kInterruptNames =
    { "Interruptible", "Non-interruptible" };

    public static int interruptToIndex(int interrupt)
    {
        return interrupt;
    }

    public static int indexToInterrupt(int index)
    {
        return index;
    }

   
    protected static ImageIcon _icon = null;
    protected static ImageIcon _mainIcon = null;
    protected static ImageIcon _coreIcon = null;
    protected static ImageIcon _coreMainIcon = null;

    transient private DefaultMutableTreeNode _tempNode = null;
    transient private boolean _modified = false;
    transient private SimBionicEditor mApp;
    
    protected boolean _isMain = false;
    protected int _lastIndex = 0;
    
    // TODO: Whenever this field is modified, the behavior model's polys field must be updated too.
    private Vector _polys = new Vector();
    
    public SB_Behavior () {
       
    }

    public SB_Behavior(Behavior model)
    {
        super(model);
        
        for (Poly poly : model.getPolys()) {
           _polys.add(new SB_Polymorphism(this, poly));
        }
        
        if (getPolyCount() == 0) {
           addPoly(new SB_Polymorphism(this, new Poly()));
        }
    }

    public int getLastIndex()
    {
        return _lastIndex;
    }

    public void setLastIndex(int lastIndex)
    {
        _lastIndex = lastIndex;
    }

    public int getExec()
    {
        return getBehaviorModel().getExec();
    }

    public void setExec(int exec)
    {
       getBehaviorModel().setExec(exec);
    }

    public int getInterrupt()
    {
        boolean interrupt = getBehaviorModel().isInterrupt();
        if (interrupt) {
           return kInterruptYes;
        } else {
           return kInterruptNo;
        }
    }

    public void setInterrupt(int interrupt)
    {
       getBehaviorModel().setInterrupt(interrupt == kInterruptYes);
    }

    public SimBionicEditor getEditor()
    {
        return mApp;
    }

    public void setEditor(SimBionicEditor editor)
    {
        mApp = editor;
    }

    public void clearPolys()
    {
        getBehaviorModel().clearPolys();
        _polys.clear();
    }

    public int getPolyCount()
    {
        return getBehaviorModel().getPolys().size();
    }

    public SB_Polymorphism getPoly(int i)
    {
        return  (SB_Polymorphism) _polys.get(i);
    }

    public void addPoly(SB_Polymorphism poly)
    {
        getBehaviorModel().addPoly(poly.getDataModel());
        _polys.add(poly);
    }

    public boolean removePoly(SB_Polymorphism poly)
    {
        getBehaviorModel().removePoly(poly.getDataModel());
        return _polys.remove(poly);
    }

    public void swapPoly(int i, int j)
    {
       getBehaviorModel().swapPoly(i, j);
       _polys.set(j, _polys.set(i, _polys.get(j)));
    }

    public SB_Polymorphism findPoly(ArrayList polyIndices)
    {
        SB_Polymorphism poly;
        int count = getPolyCount();
        for (int i = 0; i < count; ++i)
        {
            poly = getPoly(i);
            if (poly.getIndices().equals(polyIndices))
                return poly;

        }
        return null;
    }

    public Icon getIcon()
    {
        if (_isMain)
        {
            if (isCore())
            {
                if (_coreMainIcon == null)
                    _coreMainIcon = Util.getImageIcon("CoreHome16.gif");
                return _coreMainIcon;
            } else
            {
                if (_mainIcon == null)
                    _mainIcon = Util.getImageIcon("Home16.gif");
                return _mainIcon;
            }
        } else
        {
            if (isCore())
            {
                if (_coreIcon == null)
                    _coreIcon = Util.getImageIcon("CoreBehavior.gif");
                return _coreIcon;
            } else
            {
                if (_icon == null)
                    _icon = Util.getImageIcon("Behavior.gif");
                return _icon;
            }
        }
    }

    public String getToolTipText()
    {
        String description = getDescription();
        if (description == null || description.trim().equals(""))
            return null;
        if (description.length() > 50)
        {
            int index = description.lastIndexOf(' ', 50);
            if (index != -1)
            {
                return description.substring(0, index) + " ...";
            } else
            {
                return description.substring(0, 50) + "...";
            }
        }
        return description;
    }

    public void updatePolyIndices(int type, int index, String name1, String name2)
    {
        int size = getPolyCount();
        for (int i = 0; i < size; ++i)
            getPoly(i).updateIndices(type, index, name1, name2);
    }

    public DefaultMutableTreeNode getTempNode()
    {
        return _tempNode;
    }

    public void setTempNode(DefaultMutableTreeNode tempNode)
    {
        _tempNode = tempNode;
    }

    public void updateLocalsEditable()
    {
        int size = getPolyCount();
        for (int i = 0; i < size; ++i)
            getPoly(i).updateLocalsEditable();
    }

    public boolean isBTNModified()
    {
        if (_modified)
            return true;
        int size = getPolyCount();
        for (int i = 0; i < size; ++i)
            if (getPoly(i).isModified())
                return true;
        return false;
    }

    public void setBTNModified(boolean modified)
    {
        _modified = modified;
        if (modified)
        {
            // if (mApp._saveItem != null)
            mApp.setDirty(true);
            System.out.println("BTN modified: Behavior " + getName());
        } else
        {
            int size = getPolyCount();
            for (int i = 0; i < size; ++i)
                getPoly(i).setModified(false);
        }
    }

    public int getDrawableCount(Class c)
    {
        int count = 0;
        int size = getPolyCount();
        for (int i = 0; i < size; ++i)
            count += getPoly(i).count(c);
        return count;
    }

    public int compareTo(Object o)
    {
        return toString().compareToIgnoreCase(o.toString());
    }

   
    // after compiling parameters
    public void checkError(SB_ErrorInfo errorInfo, SB_TypeManager typeManager, I_CompileValidator validator)
    {
        SB_OutputBar outputBar = SB_OutputBar.getInstance();
        SB_Output build = outputBar.getBuild();

        build.addLine(new SB_Line(getName(), Color.black, null, null, this, SB_Line.MESSAGE));

        // validate polymorphisms
        SB_Polymorphism poly;
        int size = getPolyCount();
        SB_Descriptors descriptors = ((SB_ProjectBar) ComponentRegistry.getProjectBar())
              .getDescriptors();
        TreeSet set = new TreeSet();
        for (int i = 0; i < size; ++i)
        {
           poly = getPoly(i);
           build.addLine(new SB_Line(poly.getIndicesLabel(), Color.black, poly, null, null, SB_Line.MESSAGE));
           // check if valid hierarchy
           if (!descriptors.validPolyIndices(poly.getIndices()))
           {
              errorInfo._ne++;
              build.addLine(new SB_Line("ERROR: Invalid polymorphism '"
                    + poly.getIndicesLabel() + "'.", Color.red, poly, null, descriptors, SB_Line.ERROR));
           } else
           {
              // check if repeated hierarchy
              String indicesLabel = poly.getIndicesLabel();
              if (set.contains(indicesLabel))
              {
                 errorInfo._ne++;
                 build
                 .addLine(new SB_Line("ERROR: Repeated polymorphism '"
                       + poly.getIndicesLabel() + "'.", Color.red, poly, null,
                       descriptors, SB_Line.ERROR));
              } else
                 set.add(indicesLabel);
           }
           poly.checkError(errorInfo, typeManager,validator);
        }

    }

    public void writeExternal(ObjectOutput out) throws IOException
    {
        super.writeExternal(out);

        out.writeObject(getPolys());
        out.writeInt(getExec());
        out.writeInt(getInterrupt());
        out.writeBoolean(_isMain);
        out.writeInt(_lastIndex);
    }

    public void readExternal(ObjectInput in) throws ClassNotFoundException, IOException
    {
        super.readExternal(in);

        /*_polys = (Vector) in.readObject();
        setExec(in.readInt());
        if (SB_ProjectBar.getBTNVersion() < 4)
        {
            switch (getExec())
            {
            case 0: // multi-tick, interruptible
                setExec(kExecMultiTick);
                setInterrupt( kInterruptYes);
                break;
            case 1: // one-tick, interruptible
                setExec(kExecOneTick);
                setInterrupt(kInterruptYes);
                break;
            case 2: // one-tick, non-interruptible
                setExec(kExecOneTick);
                setInterrupt(kInterruptNo);
                break;
            default:
                break;
            }
        } else
           setInterrupt(in.readInt());
        _isMain = in.readBoolean();
        _lastIndex = in.readInt(); */
    }

    public String getTag()
    {
        return "behavior";
    }


    public int findOccurrences(Pattern pattern, String strReplace) throws SB_CancelException
    {
        int total = 0;
        int size = getPolyCount();
        for (int i = 0; i < size; ++i)
            total += getPoly(i).findOccurrences(pattern, strReplace);
        return total;
    }

    /**
     * @return Returns the isMain.
     */
    public boolean isMain()
    {
        return _isMain;
    }

    /**
     * @param isMain
     *            The isMain to set.
     */
    public void setMain(boolean isMain)
    {
        _isMain = isMain;
    }

    /**
     * @return Returns the polys.
     */
    public List getPolys()
    {
        Behavior behaviorModel = getBehaviorModel();
        return behaviorModel.getPolys();
    }
    
    /**
     * Creates a duplicate of this behavior.
     */
    public Object clone()
    {       
        SB_Behavior copy = null;
        
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream sOut = new ObjectOutputStream(out);
            this.writeExternal(sOut);
            sOut.flush();
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream sIn = new ObjectInputStream(in);
            Behavior copyModel = new Behavior(this.getBehaviorModel());
            copy = new SB_Behavior(copyModel);
            copy.readExternal(sIn);
        } catch (Exception e) {
           e.printStackTrace();
            System.err.println("Exception while duplicating behavior");
        }
        
        // update polys to point to cloned behavior
        //for (int i=0; i<copy.getPolys().size(); ++i)
        //    ((SB_Polymorphism)(copy.getPolys().get(i))).setParent(copy);
        return copy;
    }

    /**
     * Determines all behaviors that can be directly invoked from this behavior's polymorphisms.
     * @return set of behavior names
     */
    public Set/*<String*/ getReferencedBehaviors()
    {
        HashSet behaviors = new HashSet();
        
        Iterator polyIt = getPolys().iterator();
        while (polyIt.hasNext())
        {
            SB_Polymorphism poly = (SB_Polymorphism)polyIt.next();
            behaviors.addAll( poly.getReferencedBehaviors() );
        }
        
        return behaviors;
    }
    
    @Override
    public void setCore(boolean core) {
       // do nothing here
    }
    
    
    public Behavior getBehaviorModel() {
       return (Behavior)getDataModel();
    }
}
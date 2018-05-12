
package com.stottlerhenke.simbionic.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import org.xml.sax.XMLReader;

import com.stottlerhenke.simbionic.api.SB_Param;
import com.stottlerhenke.simbionic.common.SB_Logger;
import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.common.debug.DMFieldMap;
import com.stottlerhenke.simbionic.common.debug.MFCSocketInputStream;
import com.stottlerhenke.simbionic.common.debug.MFCSocketOutputStream;
import com.stottlerhenke.simbionic.common.debug.SB_DebugMessage;
import com.stottlerhenke.simbionic.common.xmlConverters.XMLObjectConverter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.JavaScript;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.editor.FileManager;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SB_Breakpoint;
import com.stottlerhenke.simbionic.editor.SB_Entity;
import com.stottlerhenke.simbionic.editor.SB_ErrorInfo;
import com.stottlerhenke.simbionic.editor.SB_Frame;
import com.stottlerhenke.simbionic.editor.SB_TypeManager;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.api.DefaultValidator;
import com.stottlerhenke.simbionic.editor.gui.api.I_CompileValidator;
import com.stottlerhenke.simbionic.editor.gui.summary.SummaryGenerator;
import com.stottlerhenke.simbionic.engine.debug.SB_DebugServer;

/**
 * This UI holds the {@link SimBionicJava} data model to load and save.
 */
public class SB_ProjectBar extends JTabbedPane implements ActionListener
{
    public static final Class SB_RectangleClass = SB_Rectangle.class;
    public static final Class SB_ConditionClass = SB_Condition.class;
    public static final Class SB_ConnectorClass = SB_Connector.class;

    
    protected SimBionicEditor _editor;
    public SB_Catalog _catalog;
    protected SB_TypeManager _typeManager;
    public SB_Descriptors _descriptors;

    /**
     * XXX: Workaround used to determine which file format was selected by
     * {@link #_fileChooser} when saving a file.
     * */
    private static final FileFilter SBJ_FILTER = new FileFilter() {
        @Override
        public String getDescription() {
            return "SimBionic Project (*.sbj)"; // file extension ".sbj"
        }
    
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
    
            String ext = SB_ProjectBar.getExt(f);
    
            return ext != null ? ext.equals("sbj")
                               : false;
        }
    };

    /**
     * XXX: Workaround used to determine which file format was selected by
     * {@link #_fileChooser} when saving a file.
     * */
    private static final FileFilter XML_FILTER = new FileFilter() {
        @Override
        public String getDescription() {
            return "SimBionic Project  in XML File (*.xml)";
        }
    
        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;
    
            String ext = SB_ProjectBar.getExt(f);
    
            return ext != null ? ext.equals("xml")
                               : false;
        }
    };

    private enum ProjectFileFormat {
        SBJ(".sbj"), XML(".xml");

        private final String extension;
        ProjectFileFormat(String extension) {
            this.extension = extension;
        }
    }

    protected JFileChooser _fileChooser;

    /**
     * XXX: Used to store the location of the "current project file" and pass
     * it between methods. This might not be the ideal approach when file
     * format matters.
     * */
    public File _projectFile;

    public static final String LOOPBACK_ADDR = "127.0.0.1";
    public static final int AI_PORT = 7242;
    public static final int MAX_CONNECT_TRIES = 15;
    public static final int CONNECT_TIMEOUT_SECS = 10;

    public enum DebugMode {
        RUN, PAUSE, STOP, STEP_INTO, STEP_OVER, STEP_ONE_TICK, STEP_RUN_TO_FINAL
    }

    protected boolean _debugging = false;
    protected DebugMode _debugMode = DebugMode.STOP;
    protected boolean _querying = false;
    protected Color _transcriptColor = Color.BLACK;
    protected Socket _connectionToServer;
    protected MFCSocketOutputStream _out;
    protected MFCSocketInputStream _in;
    protected SB_Logger _logger;
    public SB_Debugger _debugger;
    protected Timer _timer;

    protected boolean _projectModified = false;

    private SB_SettingsDialog _settingsDialog;
    private PropertiesDialog _propertiesDialog;
    
    protected static XMLReader _xr = null;

    protected SB_LocalsTree _localsTree;

    protected List _changeListeners;

    private SimBionicJava _dataModel;

    public SB_ProjectBar(SimBionicEditor editor)
    {
        super(BOTTOM);

        ComponentRegistry.setProjectBar(this);
        _editor = editor;

        _changeListeners = new ArrayList();

        _catalog = createCatalog();
        JScrollPane scrollCatalog = new JScrollPane(_catalog);
        addTab("Catalog", scrollCatalog);

        _descriptors = new SB_Descriptors(editor);
        JScrollPane scrollDescriptors = new JScrollPane(_descriptors);
        addTab("Descriptors", scrollDescriptors);

        setMinimumSize(new Dimension(100, 50));
    }

    public SimBionicJava getDataModel() {
       return _dataModel;
    }

    private void createDataModel() {
       _dataModel = new SimBionicJava();
       _dataModel.setIpAddress(LOOPBACK_ADDR);
       _dataModel.setLoopBack(true);
       _dataModel.setVersion(Version.FILE_VERSION);
    }

    private boolean useLoopback() {
       return _dataModel.isLoopBack();
    }

    private void setUseLoopback(boolean useLoopback) {
       _dataModel.setLoopBack(useLoopback);
    }

    private String getIpAddress() {
       return _dataModel.getIpAddress();
    }

    private void setIpAddress(String ipAddress) {
       _dataModel.setIpAddress(ipAddress);
    }

    protected SB_Catalog createCatalog()
    {
        return new SB_Catalog(_editor);
    }

    protected SB_TabbedCanvas getTabbedCanvas()
    {
        return ComponentRegistry.getContent();
    }

    public void setLocalsTree(SB_LocalsTree tree)
    {
        _localsTree = tree;
    }


    /**
     * Registers a listener to be notified of changes to the model.
     * @param listener
     */
    public void addChangeListener(SB_ChangeListener listener)
    {
        _changeListeners.add(listener);
    }

    /**
     * Unregisters a listener so that it is no longer notified of changes
     * to the model.
     * @param listener
     */
    public void removeChangeListener(SB_ChangeListener listener)
    {
        _changeListeners.remove(listener);
    }

    protected JFileChooser getFileChooser()
    {
        if (_fileChooser == null)
        {
            _fileChooser = new JFileChooser();
            _fileChooser.setFileFilter(SBJ_FILTER);
            _fileChooser.addChoosableFileFilter(XML_FILTER);

            _fileChooser.setAcceptAllFileFilterUsed(false);
            _fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }
        return _fileChooser;
    }

    public SB_Catalog getCatalog()
    {
        return _catalog;
    }

    public void setCatalog(SB_Catalog catalog)
    {
        _catalog = catalog;
    }

    protected SB_SettingsDialog getSettingsDialog()
    {
        if (_settingsDialog == null)
            _settingsDialog = new SB_SettingsDialog();
        return _settingsDialog;
    }
    
    protected void showProjectProperties () {
    	if (_propertiesDialog == null) {
    		_propertiesDialog = new PropertiesDialog();
    	}
    	 SimBionicJava dataModel = getDataModel();
     	// show the javaScript dialog
    	 _propertiesDialog.setDataModel(dataModel.getProjectProperties());
    	 _propertiesDialog.setVisible(true);
         if (_propertiesDialog.wasOkClicked() && _propertiesDialog.didPropertiesChange()) {
        	 dataModel.setProjectProperties(_propertiesDialog.getProjectProperties());
        	 setProjectModified(true);	
         }
    }

    public void goToNextError()
    {
        SB_OutputBar outputBar = SB_OutputBar.getInstance();
        outputBar.setSelectedIndex(SB_OutputBar.BUILD_INDEX);
        SB_Output build = SB_OutputBar._build;
        int sel = build.getPrevError();
        build.setSel(sel);
        build.scrollToSel();
    }

    public void goToPreviousError()
    {
        SB_OutputBar outputBar = SB_OutputBar.getInstance();
        outputBar.setSelectedIndex(SB_OutputBar.BUILD_INDEX);
        SB_Output build = SB_OutputBar._build;
        int sel = build.getNextError();
        build.setSel(sel);
        build.scrollToSel();
    }

    @Override
	public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (command == null)
            return;

        if (command.equals(SimBionicEditor.NEW_COMMAND))
            newProject();
        else if (command.equals(SimBionicEditor.OPEN_COMMAND))
            openProject();
        else if (command.equals(SimBionicEditor.SAVE_COMMAND))
            saveProject();
        else if (command.equals(SimBionicEditor.SAVEAS_COMMAND))
            saveProjectAs();
        else if (command.equals(SimBionicEditor.CHECK_ERROR_COMMAND))
            checkError();
        else if (command.equals(SimBionicEditor.SETTINGS_ITEM)) {
            getSettingsDialog().initDialog();
            getSettingsDialog().setVisible(true);
        }
        else if (command.equals(SimBionicEditor.CREATE_SUMMARY_COMMAND)) {
        	createSummary();
        }
        else if (command.equals(SimBionicEditor.PROJECT_PROPERTIES_COMMAND)) {
        	showProjectProperties();
        }
        else {
            System.err.println("Error in SB_ProjectBar.actionPerformed:"
                    + " unknown action command: " + command);
        }
    }

    public boolean isProjectModified() {
       return _projectModified;
    }

    public void setProjectModified(boolean modified) {
       _projectModified = modified;

        if (modified)
        {
            _editor.setDirty(true);
        }
    }


    public void newProject()
    {
        // if (saveModified() == JOptionPane.CANCEL_OPTION)
        // return;

        createDataModel();
        _projectFile = null;


        setupTypeManager();

        _descriptors.newDescriptors();
        _catalog.newCatalog();
        setSelectedIndex(0);

        SB_ToolBar toolBar = ComponentRegistry.getToolBar();
        toolBar.clearStacks();

        setProjectModified(false);
        _catalog.setBTNModified(false);
        // if (_editor.getMenuBar() != null)
        // _editor._saveAction.setEnabled(false);
    }

    public void openProject()
    {
        // if (saveModified() == JOptionPane.CANCEL_OPTION)
        // return;

        int returnVal = getFileChooser().showOpenDialog(ComponentRegistry.getFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = getFileChooser().getSelectedFile();
            if (!file.exists())
            {
                JOptionPane.showMessageDialog(ComponentRegistry.getFrame(), "'" + file.getPath()
                        + "' cannot be found.   ", "File Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (file.equals(_projectFile)) // check if already open
                return;

            _projectFile = file;

            setupTypeManager();

            JFrame frame = ComponentRegistry.getFrame();
            frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loadProject();
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    public boolean loadProject(File file)
    {
       _projectFile = file;

       setupTypeManager();

       if (ComponentRegistry.isStandAlone())
       {
           ComponentRegistry.getFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
       }

       loadProject();

       if (ComponentRegistry.isStandAlone())
       {
           ComponentRegistry.getFrame().setCursor(Cursor.getDefaultCursor());
       }
       return true;
    }

    protected void loadProject()
    {
       try
       {
           System.out.println("Opening " + _projectFile);
           _editor.updateMenuMostRecentUsedFiles(_projectFile);
           _dataModel = XMLObjectConverter.getInstance()
                   .fileToObject(_projectFile);

           _catalog.open(_dataModel);
           _descriptors.open(_dataModel);

           ComponentRegistry.getOutputBar().getBuild().addLine(new SB_Line("Loaded '" + _projectFile.getPath() + "'"));
           ComponentRegistry.getOutputBar().getBuild().addLine(new SB_Line("Load complete."));

       } catch (FileNotFoundException exception)
       {
           try
           {
               _catalog.loadBase();
           } catch (Exception e)
           {
               System.err.println("error parsing " + SB_Catalog.CORE_ACTIONS_PREDICATES_FILE + ": " + exception.getMessage());
           }
           _catalog.newGlobals();
           _catalog.newConstants();
           _catalog.getRoot().remove(_catalog._behaviors);
           _descriptors.newDescriptors();
           _editor.removeFromMostRecentUsedFiles(_projectFile);
       } catch (IOException exeption)
       {
           System.err.println("i/o exception");
           _editor.removeFromMostRecentUsedFiles(_projectFile);
       } catch (Exception e) {
          System.err.println("exception during loading project: " + e.getMessage());
          _editor.removeFromMostRecentUsedFiles(_projectFile);
      }

       _catalog.updateProjectTitle();

       SB_ToolBar toolBar = ComponentRegistry.getToolBar();
       toolBar.clearStacks();

       setProjectModified(false);
       _catalog.setBTNModified(false);

       ((SimBionicFrame) ComponentRegistry.getFrame()).updateTitle();

    }

    protected static String getExt(File f)
    {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1)
        {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    /**
     * XXX: This is mainly used because {@link #_projectFile} is "just a file",
     * and old code assumes only one output format is possible. A better
     * approach would be to turn {@link #_projectFile} into a struct that
     * stores both a File object representing the location of the project file
     * and the format of the file at that location.
     * */
    static ProjectFileFormat inferFormatFromFilename(File f) {
        if (f.getName().endsWith(ProjectFileFormat.SBJ.extension)) {
            return ProjectFileFormat.SBJ;
        } else if (f.getName().endsWith(ProjectFileFormat.XML.extension)) {
            return ProjectFileFormat.XML;
        } else return null;
    }

    public boolean saveProject()
    {
       if (_projectFile == null) {
          return saveProjectAs();
       }

       if (!_projectFile.getParentFile().exists())
       {
           if (!_projectFile.getParentFile().mkdirs())
           {
               JOptionPane.showMessageDialog(ComponentRegistry.getFrame(), "'" + _projectFile.getParent()
                       + "' cannot be found.   ", "Parent Directory Not Found",
                   JOptionPane.WARNING_MESSAGE);
               return false;
           }
       }

       getTabbedCanvas().storeLastValues();

       ProjectFileFormat format = inferFormatFromFilename(_projectFile);

       try {
          // set main name
    	  _dataModel.getProjectProperties().setDateLastUpdate(new Date().toString());
    	  _dataModel.getProjectProperties().setSimbionicVersion(Version.SIMBIONIC_VERSION);
          _dataModel.setMain(_catalog._main.getName());
          if (format.equals(ProjectFileFormat.SBJ)) {
              XMLObjectConverter.getInstance()
                  .saveZippedXML(_dataModel, _projectFile.getAbsoluteFile());
          } else {
              //Default: save as plaintext XML to file.
              XMLObjectConverter.getInstance()
                  .saveXML(_dataModel, _projectFile.getAbsoluteFile());
          }

          checkError();
          System.out.println("File saved.");
          _editor.updateMenuMostRecentUsedFiles(_projectFile);

       } catch (Exception ex) {
          System.err.println("exception during saving " + ex.getMessage());
          return false;
       }

       _catalog.setBTNModified(false);
       return true;


    }

    public boolean saveProjectAs()
    {
       if (_projectFile == null)
       {
           getFileChooser().setCurrentDirectory(new File(System.getProperty("user.dir")));
           getFileChooser().setSelectedFile(new File(""));
       } else
           getFileChooser().setSelectedFile(_projectFile);
       int returnVal = getFileChooser().showSaveDialog(ComponentRegistry.getFrame());
       if (returnVal == JFileChooser.APPROVE_OPTION)
       {
           File file = getFileChooser().getSelectedFile();
           ProjectFileFormat format;
           if (getFileChooser().getFileFilter() == SBJ_FILTER) {
               format = ProjectFileFormat.SBJ;
           } else if (getFileChooser().getFileFilter() == XML_FILTER) {
               format = ProjectFileFormat.XML;
           } else {
               // Indicates programming error (added new FileFilter without
               // adding handling for new type.)
               throw new RuntimeException(
                       "Unexpected file type chosen for output");
           }

           if(!file.getAbsolutePath().endsWith(format.extension)) {
        	   file = new File(file.getAbsolutePath() + format.extension);
           }
           
           if (file.exists())
           {
               int n = JOptionPane.showConfirmDialog(ComponentRegistry.getFrame(), "'"
                       + file.getPath() + "' already exists.   \n" + "Do you want to replace it?",
                   "Save Project", JOptionPane.YES_NO_OPTION);
               if (n == JOptionPane.NO_OPTION)
                   return false;
           }
           _projectFile = file;

           setProjectModified(true);
           _catalog.setBTNModified(true);

           if (saveProject())
           {
        	   ((SimBionicFrame) ComponentRegistry.getFrame()).updateTitle();
               _catalog.updateProjectTitle();
               return true;
           }
       }
       return false;
    }

    /**
     * @return True if the save was successful and the user did not press
     *         cancel. if either the user selected "no" to save, or the user
     *         selected "yes" to save and the save was successful; return false
     *         if the user selected "cancel" or the save was unsuccessful.
     */
    public boolean saveIfModified()
    {
        // if (_editor.getJMenuBar() != null && _editor._saveItem.isEnabled())
        if (_editor.isModified())
        {
            int n = JOptionPane.showConfirmDialog(ComponentRegistry.getFrame(),
                "Save changes to project?", "Save Project", JOptionPane.YES_NO_CANCEL_OPTION);
            if (n == JOptionPane.YES_OPTION)
            {
                return saveProject();
            }
            // if CANCEL, return false
            return (n == JOptionPane.NO_OPTION);
        } else
        {
            return true;
        }
    }

    /**
     * Compiles the project.
     * @return true on success
     */
    public boolean checkError()
    {
        return checkError(null,true,null);
    }

    public boolean checkError(SB_ErrorInfo errorInfo,boolean clearOutput,I_CompileValidator validator)
    {
        SB_Output build = SB_OutputBar._build;

        if (clearOutput)
            build.clearLines();
        else
            build.addLine(new SB_Line(""));
        build.addLine(new SB_Line("Checking error..."));
        build.requestFocus();

        // make sure output bar is visible with build tab selected
        ComponentRegistry.getOutputBar().setSelectedIndex(0);

        if (errorInfo == null)
            errorInfo = new SB_ErrorInfo();
        if (validator == null)
            validator = new DefaultValidator();

        validator.setOutputBar(build);
        validator.setErrorInfo(errorInfo);

        _descriptors.checkError(validator);

        // validate actions
        _catalog.checkError(_catalog._actions, null, validator);

        // validate predicates
        _catalog.checkError(_catalog._predicates, null, validator);

        // validate constants
        _catalog.validateConstants(errorInfo);

        // validate global variables
        _catalog.validateGlobals( errorInfo, validator);

        // validate behaviors
        _catalog.checkError(_catalog._behaviors, errorInfo, validator);


        // report number of errors and warnings
        String text = "";
        String str_info = "";
        int ne = errorInfo._ne;
        int nw = errorInfo._nw;
        if (ne > 0) // at least one error
        {
            str_info = " (checking error aborted)";

        } else // no errors
        {
            int nNodes = 0;
            int nConditions = 0;
            int nConnectors = 0;
            nNodes = _catalog.getDrawableCount(SB_RectangleClass);
            nConditions = _catalog.getDrawableCount(SB_ConditionClass);
            nConnectors = _catalog.getDrawableCount(SB_ConnectorClass);

            build.addLine(new SB_Line(""));
            build.addLine(new SB_Line("Compilation Statistics:"));

            build.addLine(new SB_Line("# of rectangles = " + nNodes));
            build.addLine(new SB_Line("# of conditions = " + nConditions));
            build.addLine(new SB_Line("# of connectors = " + nConnectors));
        }
        String str_e = "error";
        if (ne == 0 || ne >= 2)
            str_e += "s";
        String str_w = "warning";
        if (nw == 0 || nw >= 2)
            str_w += "s";
        text += " - " + ne + " " + str_e + ", " + nw + " " + str_w;

        text += str_info;

        build.addLine(new SB_Line(""));
        if (ne > 0)
            build.addLine(new SB_Line(text, Color.red));
        else
            build.addLine(new SB_Line(text));
        build.repaint();

        build.updateFirstLastErrors();
        build.updateMenuItems();
        build.scrollToBottom();

        return (ne == 0);
    }





    public static String searchAndReplace(String expr, String oldText, String newText)
    {
        int len_old = oldText.length();
        int len_new = newText.length();
        if (len_old == 0)
            return expr;
        int len_expr = expr.length();
        boolean in_quotes = false;
        int i = 0, j;
        while (i < len_expr)
        {
            char c = expr.charAt(i);
            if (c == '\\') // ignore escape sequences, e.g. \"
                i += 2;
            else if (c == '"') // check for quotes
            {
                in_quotes = !in_quotes;
                ++i;
            } else if (!in_quotes)
            {
                // move index to beginning of word
                while (i < len_expr && !(isAlphaNumUnderline(c = expr.charAt(i))))
                {
                    if (c == '"') // entering quotes
                        break;
                    ++i;
                }
                for (j = 0; j < len_old; ++j) // search for old text
                {
                    if (i < len_expr && expr.charAt(i) == oldText.charAt(j))
                        ++i;
                    else
                        break;
                }
                // move index to end of word
                while (i < len_expr && (isAlphaNumUnderline(expr.charAt(i))))
                {
                    ++i;
                    j = 0; // search did not reach end of word
                }
                if (j == len_old) // old text found
                {
                    // replace old text with new text
                    i -= len_old;
                    expr = expr.substring(0, i) + newText + expr.substring(i + len_old);
                    i += len_new;
                    len_expr += len_new - len_old;
                }
            } else
                ++i;
        }
        return expr;
    }

    protected static boolean isAlphaNumUnderline(char c)
    {
        return Character.isLetterOrDigit(c) || c == '_';
    }



    /**
     * Sends the given message to the debug server.
     * @param msg
     */
    public void sendMsg(SB_DebugMessage msg)
    {
      byte[] msgBytes = msg.serialize(_logger);

      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
      MFCSocketOutputStream tempOut = new MFCSocketOutputStream(byteOut);
      try
      {
        tempOut.writeByte(0); //Packet type
        tempOut.writeMFCInt( msgBytes.length ); //PacketSize
        tempOut.write(msgBytes);

        _out.write(byteOut.toByteArray());
        _logger.log(" [[ sent message " + msg.GetTypeName() + " to server ]]",SB_Logger.DEBUGGER);
//		SB_OutputBar._debug.addLine(new SB_Line("Sent '" + msg.GetTypeName() + "' to server."));
    }
      catch( IOException e)
      {
        _logger.log("!! " + e.toString(), SB_Logger.ERROR);
      }
    }

    /**
     * create screenshots of all behaviors in the current
     */
    protected void createSummary () {
    	if (this.saveIfModified()) {
    		new SummaryGenerator(_editor).generate();
    	}
    }
    
    /**
     * Checks to see if any message has been received
     * from the debug server.  Does not block.  Use
     * waitForMessage if you want to wait until a given
     * message type is received.
     * @return the message, or null if no message has been received
     * @throws IOException
     */
    public SB_DebugMessage receiveMsg() throws IOException
    {
      SB_DebugMessage msg = null;

        if(_in.available() > 0 )
        {
          byte packetType = _in.readByte();
          int dataSize = _in.readMFCInt();

          int msgType = _in.readByte();
          msg = new SB_DebugMessage(msgType);
          msg.deserialize(_in, _logger);
        }

      if (msg != null)
  	  {
  		_logger.log(" [[ received message " + msg.GetTypeName() + " from server. ]]", SB_Logger.DEBUGGER);
//  		SB_OutputBar._debug.addLine(new SB_Line("Received '" + msg.GetTypeName() + "' from server.", Color.green));
  	  }

      return msg;
    }

    /**
     * Blocks while waiting for a message of a specified type (or range of types) to arrive
     * from a debug client.
     * @param firstMsgType the start of the desired range of message types (inclusive)
     * @param lastMsgType the end of the desired range of message types (inclusive);
     *						-1 indicates a single desired message type == firstMsgType
     * @param timeout if positive, indicates the time in seconds to wait for the message before
     *					giving up
     * @return the received message, or null if it timed out or a eng_shutdown message was received
     */
    public SB_DebugMessage waitForMessage(int firstMsgType,
                                          int lastMsgType /*=-1*/,
                                          int timeout /*= SB_DebugServer::NO_TIMEOUT*/)
    {
      SB_DebugMessage msg = null;

      if (lastMsgType == -1)
        lastMsgType = firstMsgType;

      // wait for message or timeout, whichever comes first
      double waitSoFar;
      Date startTick = new Date();

      do
      {
        if (msg != null)
        {
            SB_OutputBar._debug.addLine(new SB_Line("Discarded message type " + msg.GetTypeName() + " while waiting.",Color.RED));
        }

        try {
          Thread.sleep(100);
        } catch(Exception ex) {
          _logger.log(ex.toString());
        }

        try {
            msg = receiveMsg();
        } catch (IOException e) {
            _logger.log(e.toString());
            return null;    // connection has failed
        }

        waitSoFar = (new Date().getTime() - startTick.getTime()) / 1000.0;

      } while (((msg == null) || (msg.GetMsgType() < firstMsgType) || (msg.GetMsgType() > lastMsgType)) &&
                       ((timeout == SB_DebugServer.NO_TIMEOUT) || (waitSoFar < timeout)));

      if (waitSoFar >= timeout) {
          _logger.log("[[ timed out while waiting for message from server ]]",SB_Logger.DEBUGGER);
          SB_OutputBar._debug.addLine(new SB_Line("Timed out while waiting for message from server.", Color.RED));
      }

      if (msg != null)
        _logger.log(" [[ received message '" + msg.GetTypeName() + "' ]]",SB_Logger.DEBUGGER);

      return msg;

    }

    /**
     * Connects to the debug server and puts the client in
     * debug mode.
     */
    public void connectDebug()
	{
        if (_debugging) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        SB_OutputBar.getInstance().setSelectedIndex(SB_OutputBar.DEBUG_INDEX);

        if (!connectToServer())
            return;

        _debugging = true;
        _querying = false;
        _debugMode = DebugMode.PAUSE;   // start in pause mode

        _debugger = new SB_Debugger(_editor);
        ((SimBionicFrame)ComponentRegistry.getFrame()).setDebugModeOn(_debugger);

        if (_timer == null) {
        	_timer = new Timer(50, new DebugServerListener());
        }
        _timer.start();
	}

    public void stepOverDebug()
    {
    	if (!_debugging || _querying) {
    		Toolkit.getDefaultToolkit().beep();
    		return;
    	}

    	_debugMode = DebugMode.STEP_OVER;

        _debugger.clearChangedVars();

        DMFieldMap fields = new DMFieldMap();
    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_STEP, fields) );

    	updateDebugToolbar();
    }

    public void stepOneTickDebug()
    {
    	if (!_debugging || _querying) {
    		Toolkit.getDefaultToolkit().beep();
    		return;
    	}

        _debugMode = DebugMode.STEP_ONE_TICK;

        _debugger.clearChangedVars();

    	long entityID = _debugger.getCurrentEntity();
    	DMFieldMap fields = new DMFieldMap();
    	fields.ADD_LONG_FIELD("stepEntity", entityID);
    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_STEP_ONE_TICK, fields) );

        updateDebugToolbar();
    }

    public void stepIntoDebug()
    {
    	if (!_debugging || _querying) {
    		Toolkit.getDefaultToolkit().beep();
    		return;
    	}

        _debugMode = DebugMode.STEP_INTO;

        _debugger.clearChangedVars();

        DMFieldMap fields = new DMFieldMap();
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_STEP_INTO, fields) );

        updateDebugToolbar();
    }

    public void runToFinal()
    {
        if (!_debugging || _querying) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        _debugMode = DebugMode.STEP_RUN_TO_FINAL;

        _debugger.clearChangedVars();

        long entityID = _debugger.getCurrentEntity();
        int frameID = _debugger.getCurrentFrame();

        //send gui_run_to_final
        DMFieldMap fields = new DMFieldMap();
        fields.ADD_INT_FIELD("frame", frameID);
        fields.ADD_LONG_FIELD("stepEntity", entityID);

        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_RUN_TO_FINAL, fields));

        updateDebugToolbar();
    }

    public void startDebug()
    {
        if (!_debugging)
        {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        _debugMode = DebugMode.RUN;

        // send gui_start
        DMFieldMap fields = new DMFieldMap();
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_START, fields) );

        updateDebugToolbar();
    }

    public void pauseDebug()
    {
        if (!_debugging)
        {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        _debugMode = DebugMode.PAUSE;

        // send gui_pause
        DMFieldMap fields = new DMFieldMap();
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_PAUSE, fields) );

        updateDebugToolbar();
    }

    public void stopDebug()
    {
        if (!_debugging) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        _debugMode = DebugMode.STOP;

        // send gui_stop
        DMFieldMap fields = new DMFieldMap();
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_STOP, fields) );

        // wait for eng_shutdown_ok
        SB_DebugMessage msg = waitForMessage(SB_DebugMessage.kDBG_ENG_SHUTDOWN_OK, -1, 3);

        closeDebug();
    }

    protected void closeDebug()
    {
        _timer.stop();

        try {
            _connectionToServer.close();
        } catch (IOException e) {
            SB_OutputBar._debug.addLine(new SB_Line("!! " + e.toString()));
        }

        _debugger._entities.clear();
        _debugger._execStackTable._tableModel._entity = null;

        _catalog.clearRunningState();
        getTabbedCanvas().getActiveCanvas().repaint();

        _debugging = false;
        ((SimBionicFrame)ComponentRegistry.getFrame()).setDebugModeOff(_debugger);
    }

    protected void updateDebugToolbar()
    {
        _editor.startAction.setEnabled(_debugMode != DebugMode.RUN);
        _editor.stepIntoAction.setEnabled(_debugMode != DebugMode.RUN);
        _editor.stepOverAction.setEnabled(_debugMode != DebugMode.RUN);
        _editor.pauseAction.setEnabled(_debugMode == DebugMode.RUN);

        boolean entities = _debugger._entities.size() > 0;

        _editor.stepOneTickAction.setEnabled((_debugMode != DebugMode.RUN) && entities);
        _editor.runToFinalAction.setEnabled((_debugMode != DebugMode.RUN) && entities);
    }

    public void setLocalDebug(String varName, SB_Param varValue)
    {
    	int entitySelectedRow = _debugger._entitiesTable.getSelectedRow();
    	long entity = Long.parseLong((String)_debugger._entitiesTable.getValueAt(entitySelectedRow, 0));
    	int frameSelectedRow = _debugger._execStackTable.getSelectedRow();
    	int frame = Integer.parseInt((String)_debugger._execStackTable.getValueAt(frameSelectedRow, 0));
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_LONG_FIELD("entity", entity);
    	fields.ADD_INT_FIELD("frame", frame);
    	fields.ADD_STR_FIELD("varName", varName);
    	try{
    		fields.ADD_PARAM_FIELD("varValue", varValue);
    	} catch(Exception e){e.printStackTrace();}
    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_SET_LOCAL, fields) );
    }

    public void addToWatchDebug()
    {
    	int entitySelectedRow = _debugger._entitiesTable.getSelectedRow();
    	long entity = Long.parseLong((String)_debugger._entitiesTable.getValueAt(entitySelectedRow, 0));

    	DMFieldMap fields = new DMFieldMap();
    	fields.ADD_LONG_FIELD("entity", entity);

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_ADD_TO_WATCH, fields) );
    }

    public void removeFromWatchDebug()
    {
    	int entitySelectedRow = _debugger._entitiesTable.getSelectedRow();
    	long entity = Long.parseLong((String)_debugger._entitiesTable.getValueAt(entitySelectedRow, 0));

    	DMFieldMap fields = new DMFieldMap();
    	fields.ADD_LONG_FIELD("entity", entity);

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_REMOVE_FROM_WATCH, fields) );
    }

    public void setGlobalDebug(String varName, SB_Param varValue)
    {
    	int entitySelectedRow = _debugger._entitiesTable.getSelectedRow();
    	long entity = Long.parseLong((String)_debugger._entitiesTable.getValueAt(entitySelectedRow, 0));
    	int frameSelectedRow = _debugger._execStackTable.getSelectedRow();
    	int frame = Integer.parseInt((String)_debugger._execStackTable.getValueAt(frameSelectedRow, 0));

    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_LONG_FIELD("entity", entity);
    	fields.ADD_INT_FIELD("frame", frame);
    	fields.ADD_STR_FIELD("varName", varName);
    	try{
    		fields.ADD_PARAM_FIELD("varValue", varValue);
    	} catch(Exception e){e.printStackTrace();}
    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_SET_GLOBAL, fields) );
    }

    public void addBreakVarDebug(SB_Breakpoint bp){
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_LONG_FIELD("entity", bp._entityId);
    	if(!bp._behavior.equals("Global")){
    		fields.ADD_STR_FIELD("behavior", bp._behavior);
    	}
    	else{
    		fields.ADD_STR_FIELD("behavior", "");
    	}
    	fields.ADD_STR_FIELD("variable", bp._varName);
    	fields.ADD_INT_FIELD("breakpointId", bp._breakpointId);
    	fields.ADD_INT_FIELD("iterations", bp._iterations);
    	fields.ADD_SA_FIELD("polyIndices", new ArrayList());
    	fields.ADD_STR_FIELD("constraint", "");

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_ADD_BREAK_VAR, fields) );
    }

    public void addBreakElemDebug(SB_Breakpoint bp){
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_LONG_FIELD("entity", bp._entityId);
   		fields.ADD_STR_FIELD("behavior", bp._behavior);
    	fields.ADD_INT_FIELD("breakpointId", bp._breakpointId);
    	fields.ADD_INT_FIELD("iterations", bp._iterations);
    	fields.ADD_SA_FIELD("polyIndices", new ArrayList());
    	fields.ADD_STR_FIELD("constraint", "");
    	fields.ADD_INT_FIELD("elemId", bp._elemId);
    	fields.ADD_INT_FIELD("type", bp._type);

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_ADD_BREAK_ELEM, fields) );
    }

    public void removeBreakpointDebug(SB_Breakpoint bp){
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_INT_FIELD("breakpointId", bp._breakpointId);

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_REMOVE_BREAKPOINT, fields) );
    }

    public void disableBreakpoint(int breakpointId){
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_INT_FIELD("breakpointId", breakpointId);

    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_DISABLE_BREAKPOINT, fields) );
    }

    public void enableBreakpoint(int breakpointId){
    	DMFieldMap fields = new DMFieldMap();

    	fields.ADD_INT_FIELD("breakpointId", breakpointId);
    	sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_ENABLE_BREAKPOINT, fields) );
    }

    /**
     * Opens a connection to the SimBionic debug server.
     * @return true iff the connection was successfully established
     */
    protected boolean connectToServer()
    {
        SB_Output debug = SB_OutputBar._debug;
       	debug.clearLines();

       	if (_projectFile == null)
       	{
       		debug.addLine(new SB_Line("No sim file specified.", Color.red));
       		return false;
       	}

       	_logger = new SB_Logger();
       	_logger.register(System.out, 2);

        debug.addLine(new SB_Line("Connecting to debug server..."));

       	// try repeatedly to contact the debug server
       	int tryNum = 0;
       	while (tryNum < MAX_CONNECT_TRIES) {
            try {
            	_connectionToServer = new Socket();
            	_connectionToServer.connect(new InetSocketAddress(getIpAddress(), AI_PORT), CONNECT_TIMEOUT_SECS*1000);

                _out = new MFCSocketOutputStream(_connectionToServer.getOutputStream());
                _in = new MFCSocketInputStream(_connectionToServer.getInputStream() );
                break;
    	    } catch (IOException e) {
    	    	debug.addLine(new SB_Line("Trying to connect to server...", Color.red));
    	    }
    	    ++tryNum;
       	}
       	if (!_connectionToServer.isConnected())
       	    return false;

     	debug.addLine(new SB_Line("Opened connection to server."));

        DMFieldMap fields = new DMFieldMap();
        fields.ADD_STR_FIELD( "simfileName", _projectFile.getName() );
        fields.ADD_INT_FIELD("simfileVersion", _dataModel.getVersion());
        fields.ADD_INT_FIELD( "simFormatVersion", _dataModel.getVersion() );
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_INIT, fields) );

    	SB_DebugMessage msg = null;

    	// do the initial handshake and version checking
    	while (true)
    	{
    		if (msg != null)
    		{
    			if (msg.GetMsgType() == SB_DebugMessage.kDBG_ENG_INIT_OK)
    			{
    				debug.addLine(new SB_Line("Handshake with server complete."));
    				_editor._breakpointFrame.initBreakpoints();
    				return true;
    			}
    			else if (msg.GetMsgType() == SB_DebugMessage.kDBG_ENG_INIT_FAILED)
    			{
    				int version = msg.GetIntField("simfileVersion");
    				String filename = msg.GetStringField("simfileName");
     				debug.addLine(new SB_Line("Received eng_init_failed: mismatched sim-files (" +
     				    _projectFile.getName() + " v" + FileManager.getInstance().getRevisionNumber(_projectFile) + " vs " +
     				        filename + " v" + version + ")!", Color.red));
       				try
					{
       					_connectionToServer.close();
					}
    			    catch (IOException e)
				    {
				    	debug.addLine(new SB_Line("!! " + e.toString()));
				    	return false;
				    }
     				return false;
    			}
    			else
    			{
    				debug.addLine(new SB_Line("Received unwanted msg while waiting for response: eng_init_ok", Color.red));
    			}
    		}

    		try {
                msg = receiveMsg();
            } catch (IOException e) {
                debug.addLine(new SB_Line("Connection to server failed!", Color.red));
                return false;
            }
    	}
   }



    private class SB_SettingsDialog extends JDialog
    {

        private JTextField _ipTextField;
        private JCheckBox _loopbackCheckBox;

        public SB_SettingsDialog()
        {
            super(ComponentRegistry.getFrame(), "Connection Settings", true);

            JPanel editPanel = new JPanel();
            editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
            editPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel label = new JLabel("Server IP address:");
            editPanel.add(label);
            editPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            JPanel ipPanel = new JPanel();
            ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.X_AXIS));
            ipPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            _ipTextField = new JTextField(10);
            _ipTextField.setMaximumSize(new Dimension(1000, 20));
            _ipTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
            _ipTextField.addActionListener(new ActionListener()
            {
                @Override
				public void actionPerformed(ActionEvent event)
                {
                    updateSettings();
                }
            });
            ipPanel.add(_ipTextField);
            ipPanel.add(Box.createHorizontalStrut(5));
            _loopbackCheckBox = new JCheckBox("Loopback");
            _loopbackCheckBox.setFocusPainted(false);
            _loopbackCheckBox.setSelected(true);
            _loopbackCheckBox.addActionListener(new ActionListener()
            {
                @Override
				public void actionPerformed(ActionEvent event)
                {
                    if (_loopbackCheckBox.isSelected())
                    {
                        _ipTextField.setText(LOOPBACK_ADDR);
                        _ipTextField.setEnabled(false);
                    } else {
                        _ipTextField.setEnabled(true);
                    }
                }
            });
            ipPanel.add(_loopbackCheckBox);
            ipPanel.add(Box.createRigidArea(new Dimension(32, 0)));
            editPanel.add(ipPanel);
            editPanel.add(Box.createRigidArea(new Dimension(0, 7)));

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPanel.add(Box.createHorizontalGlue());
            JButton settingsOK = new JButton("OK");
            settingsOK.setFocusPainted(false);
            settingsOK.addActionListener(new ActionListener()
            {

                @Override
				public void actionPerformed(ActionEvent event)
                {
                    updateSettings();
                }
            });
            buttonPanel.add(settingsOK);
            buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            JButton settingsCancel = new JButton("Cancel");
            settingsCancel.setFocusPainted(false);
            settingsCancel.addActionListener(new ActionListener()
            {

                @Override
				public void actionPerformed(ActionEvent event)
                {
                    SB_SettingsDialog.this.setVisible(false);
                }
            });
            buttonPanel.add(settingsCancel);

            getContentPane().add(editPanel, BorderLayout.CENTER);
            getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            pack();

            Dimension dialogSize = getSize();
            dialogSize.width = 375;
            setSize(dialogSize);
            Rectangle frameBounds = ComponentRegistry.getFrame().getBounds();
            setLocation(frameBounds.x + (frameBounds.width - dialogSize.width) / 2, frameBounds.y
                    + (frameBounds.height - dialogSize.height) / 2);
        }

        public void initDialog()
        {
            _ipTextField.setText(getIpAddress());
            _loopbackCheckBox.setSelected(useLoopback());
            _ipTextField.setEnabled(!useLoopback());
        }

        private void updateSettings()
        {
            String text = _ipTextField.getText();
            boolean useLoopback = _loopbackCheckBox.isSelected();
            if (text.equals(LOOPBACK_ADDR))
                useLoopback = true;
            if (!text.equals(getIpAddress())) {
               setIpAddress(text);
               setUseLoopback(useLoopback);
               setProjectModified(true);
            }
            setVisible(false);
        }

    }

    /**
     * @return Returns the descriptors.
     */
    public SB_Descriptors getDescriptors()
    {
        return _descriptors;
    }

    /**
     * @param descriptors
     *            The descriptors to set.
     */
    public void setDescriptors(SB_Descriptors descriptors)
    {
        _descriptors = descriptors;
    }


    public void setupTypeManager(){
        _typeManager = new SB_TypeManager();
        _catalog.setTypeManager(_typeManager);
        _localsTree.setTypeManager(_typeManager);
        _descriptors.setTypeManager(_typeManager);
    }

    public SB_TypeManager getTypeManager(){
    	return _typeManager;
    }

    class ListOfFiles implements Enumeration
    {
        private File[] listOfFiles;
        private int current = 0;

        public ListOfFiles(File[] listOfFiles)
        {
            this.listOfFiles = listOfFiles;
        }

        @Override
		public boolean hasMoreElements()
        {
            if (current < listOfFiles.length)
                return true;
            else
                return false;
        }

        @Override
		public Object nextElement()
        {
            FileInputStream in = null;

            if (!hasMoreElements())
                throw new NoSuchElementException("No more files.");
            else
            {
                File nextElement = listOfFiles[current];
                current++;
                try
                {
                    in = new FileInputStream(nextElement);
                }
                catch (FileNotFoundException e)
                {
                	System.err.println("ListOfFiles: Can't open " + nextElement);
                }
            }
            return in;
        }
    }

    /**
     * Notifies listeners that a behavior has been renamed.
     * @param behavior
     */
    public void behaviorRenamed(SB_Behavior behavior, String oldName)
    {
        Iterator listenerIt = _changeListeners.iterator();
        while (listenerIt.hasNext())
        {
            ((SB_ChangeListener)listenerIt.next()).behaviorRenamed(behavior,oldName);
        }
    }

    /**
     * Sets the IP address to be used for the server in debug mode.
     * @param serverAddress
     */
    public void setDebugServerIP(String serverAddress)
    {
       setIpAddress(serverAddress);
    }



    /**
     * Retrieves current debug server state for the given entity.
     * @param entity
     */
    void queryEntityState(SB_Entity entity)
    {
        _querying = true;

        System.out.println("--> querying entity " + entity);

        // retrieve current stack size for the entity
        DMFieldMap fields = new DMFieldMap();
        fields.ADD_ID_FIELD("entity", entity._entityId);
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_GET_ENTITY, fields) );
        SB_DebugMessage entityInfoMsg = waitForMessage(SB_DebugMessage.kDBG_ENG_ENTITY_INFO, -1, 10);
        int stackSize = entityInfoMsg.GetIntField("stackSize");
        long alive = entityInfoMsg.GetLongField("alive");

        entity._currentFrame = stackSize-1;
        entity._alive = alive;

        // retrieve information for each frame in the stack
        Vector stack = new Vector();
        for (int i=0; i<stackSize; ++i) {
            fields = new DMFieldMap();
            fields.ADD_ID_FIELD("entity", entity._entityId);
            fields.ADD_INT_FIELD("frame",i+1);
            sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_GET_FRAME, fields) );

            SB_DebugMessage frameInfoMsg = waitForMessage(SB_DebugMessage.kDBG_ENG_FRAME_INFO, -1, 10);
            SB_Frame newFrame = _debugger.makeFrame(
                    frameInfoMsg.GetIntField("parent"),
                    frameInfoMsg.GetStringField("behavior"),
                    frameInfoMsg.GetStringArrayField("polyIndices"),
                    frameInfoMsg.GetIntField("currentNode"),
                    frameInfoMsg.GetIntField("interrupt"));

            stack.add(newFrame);
        }
        entity._frames = stack;

        // local and global variable values for the entity are queried
        // when the entity is selected in the debugger

        _querying = false;
    }

    /**
     * Retrieve current global variable values for the given entity.
     * Assumes that entityId is the currently-selected entity.
     * @param entityId
     */
    public void queryEntityGlobals(long entityId)
    {
        _querying = true;

        System.out.println("--> querying globals for entity " + entityId);

        DMFieldMap fields = new DMFieldMap();
        fields.ADD_ID_FIELD("entity", entityId);
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_GET_GLOBAL_VARS, fields) );
        SB_DebugMessage globalVarsMsg = waitForMessage(SB_DebugMessage.kDBG_ENG_GLOBAL_VARS_INFO, -1, 10);
        ArrayList globalNames = globalVarsMsg == null? new ArrayList() : globalVarsMsg.GetStringArrayField("varNames");
        Vector globalValues = globalVarsMsg == null? new Vector() : (Vector) globalVarsMsg.GetField("varValues");

        _querying = false;

        _debugger.updateGlobalVariables(globalNames,globalValues);
    }

    /**
     * Retrieve current global variable values for the given entity and stack frame.
     * Assumes that entityId is the currently-selected entity.
     * @param entityId
     * @param frame
     */
    public void queryEntityLocals(long entityId,int frame)
    {
        _querying = true;

        System.out.println("--> querying locals for entity " + entityId + ", frame " + frame);

        DMFieldMap fields = new DMFieldMap();
        fields.ADD_ID_FIELD("entity", entityId);
        fields.ADD_INT_FIELD("frame", frame+1);
        sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_GET_LOCAL_VARS, fields) );
        SB_DebugMessage localVarsMsg = waitForMessage(SB_DebugMessage.kDBG_ENG_LOCAL_VARS_INFO, -1, 10);
        ArrayList localNames = localVarsMsg ==null? new ArrayList() : localVarsMsg.GetStringArrayField("varNames");
        Vector localValues = localVarsMsg == null? new Vector() : (Vector) localVarsMsg.GetField("varValues");

        _querying = false;

        _debugger.updateLocalVariables(localNames, localValues);
    }

    /**
     * Periodically checks for and processes unsolicited messages from the
     * debug server. Solicited messages -- those in response to a specific
     * query from the client -- are handled in
     *
     * @author houlette
     */
    private final class DebugServerListener implements ActionListener
    {
        @Override
		public void actionPerformed(ActionEvent evt) {
            if (_querying) return;

            SB_DebugMessage msg;
            try {
                msg = receiveMsg();
            } catch (IOException e) {
                _logger.log(e.toString());
                SB_OutputBar._debug.addLine(new SB_Line("Connection to server failed!",Color.red));
                closeDebug();
                return;
            }

            if (msg == null)
                return;

            switch (msg.GetMsgType())
            {
            case SB_DebugMessage.kDBG_ENG_BEHAVIOR_CHANGED:
            {
                long entityId = msg.GetIdField("entity");
                String behav_name = msg.GetStringField("behavior");
                ArrayList polyIndices = msg.GetStringArrayField("polyIndices");
                // node will be set at the end of the step when the stack is refreshed

                _debugger.changeBehavior(entityId,behav_name,polyIndices);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " reset behavior stack to '" + behav_name + "'",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_BREAKPOINT_HIT:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                int breakpointId = msg.GetIntField("breakpointId");

                stepFinished(entityId,frame);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " hit breakpoint " + breakpointId,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_CONDITION_CHECKED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                int conditionId = msg.GetIntField("conditionId");
                SB_Param conditionValue = msg.GetParamField("conditionValue");

                _debugger.checkCondition(entityId, frame, conditionId, conditionValue);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " checked condition " + conditionId + " on frame " + frame + ", value = '" + conditionValue + "'",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_CONDITION_FOLLOWED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                int conditionId = msg.GetIntField("conditionId");

                _debugger.followCondition(entityId, frame, conditionId);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " followed condition " + conditionId + " on frame " + frame,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_ENTITY_CREATED:
            {
                long entityId = msg.GetIdField("entity");
                String name = msg.GetStringField("name");
                _debugger.createEntity(entityId, name);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " (" + name + ") created",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_ENTITY_DESTROYED:
            {
                long entityId = msg.GetIdField("entity");
                _debugger.destroyEntity(entityId);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " destroyed",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_ENTITY_ENDING:
            {
                long entityId = msg.GetIdField("entity");
                _debugger.endEntity(entityId);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " completed tick",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_ENTITY_STARTING:
            {
                long entityId = msg.GetIdField("entity");
                _debugger.startEntity(entityId);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " started tick",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_FRAME_COMPLETED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;

                _debugger.completeFrame(entityId, frame);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " completed frame " + frame,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_FRAME_CREATED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                int parent = msg.GetIntField("parent");
                String behav_name = msg.GetStringField("behavior");
                ArrayList polyIndices = msg.GetStringArrayField("polyIndices");
                int currentNode = msg.GetIntField("currentNode");
                int interrupt = msg.GetIntField("interrupt");

                _debugger.createFrame(entityId, frame, parent, behav_name, polyIndices, currentNode, interrupt);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " created frame " + frame + " : " + behav_name,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_FRAME_CURRENT:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;

                _debugger.setCurrentFrame(entityId,frame);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " has current frame " + frame,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_FRAME_DISCARDED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;

                _debugger.discardFrame(entityId, frame);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " discarded frame " + frame,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_GLOBAL_CHANGED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                String varName = msg.GetStringField("varName");
                SB_Param value = msg.GetParamField("value");

                _debugger.changeGlobalVariable(entityId,frame,varName,value);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " global variable '" + varName + "' has new value '" + value + "'",_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_NODE_CHANGED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                int nodeId = msg.GetIntField("nodeId");

                _debugger.changeNode(entityId, frame, nodeId);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " changed current node in frame " + frame + " to " + nodeId,_transcriptColor));
            }
            break;

            case SB_DebugMessage.kDBG_ENG_SHUTDOWN:
            {
                // acknowledge shutdown from engine
                DMFieldMap fields = new DMFieldMap();

                sendMsg( new SB_DebugMessage(SB_DebugMessage.kDBG_GUI_SHUTDOWN_OK, fields) );

                SB_OutputBar._debug.addLine(new SB_Line("Received shutdown request from engine"));

                closeDebug();
            }
            break;

            case SB_DebugMessage.kDBG_ENG_STEP_FINISHED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                long alive = msg.GetLongField("alive");
                int query = msg.GetIntField("query");

                stepFinished(entityId,frame);

                SB_OutputBar._debug.addLine(new SB_Line("--- STEP COMPLETE ---",_transcriptColor));
                _transcriptColor = (_transcriptColor == Color.BLACK) ? Color.DARK_GRAY : Color.BLACK;
            }
            break;

            case SB_DebugMessage.kDBG_ENG_VAR_CHANGED:
            {
                long entityId = msg.GetIdField("entity");
                int frame = msg.GetIntField("frame")-1;
                String varName = msg.GetStringField("varName");
                SB_Param value = msg.GetParamField("value");

                _debugger.changeVariable(entityId,frame,varName,value);

                SB_OutputBar._debug.addLine(new SB_Line("Entity " + entityId + " local variable '" + varName + "' has new value '" + value + "'",_transcriptColor));
            }
            break;

            default:
            {
                SB_OutputBar._debug.addLine(new SB_Line("Unhandled engine message: " + msg.GetMsgType(),Color.RED));
            }
            break;
            }


            System.out.println("<-- " + msg.GetTypeName());
            System.out.println(_debugger.toString());
            if (_debugMode == DebugMode.PAUSE)
                System.out.println("*************************************");
        }

        /**
         * Sets the specified entity to be current and updates
         * its state from the server.  Also
         * @param entityId
         * @param frame
         */
        private void stepFinished(long entityId,int frame)
        {
            SB_Entity entity = _debugger.findEntity(entityId);
            _debugger.setCurrentEntity(entity);

            queryEntityState(entity);

            _debugger.setSelectedEntity(entity,true);

            _debugMode = DebugMode.PAUSE;
            updateDebugToolbar();
        }
    }
}


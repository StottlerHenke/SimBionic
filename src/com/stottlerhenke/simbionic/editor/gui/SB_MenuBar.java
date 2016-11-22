package com.stottlerhenke.simbionic.editor.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.stottlerhenke.simbionic.common.Version;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;

/**
 * SB_MenuBar contains the menu information for SimBionic, and is used by both
 * SimBionic and FlexiTrainer. This class should never create its own actions,
 * but just plug in actions from other classes to the UI components.
 * FlexiTrainer uses the static functions to create menus and add to the
 * MultiEditor menu; SimBionicFrame uses an instance of this class as the
 * SimBionic menu.
 * 
 */
public class SB_MenuBar extends JMenuBar
{
    public SB_MenuBar()
    {
        super();
    }
    
    public void create(SimBionicEditor simbionic)
    {
        JMenu fileMenu = simbionic.createFileMenu();
        this.add(fileMenu);
        
        JMenu editMenu = createEditMenu(simbionic);
        this.add(editMenu);

        JMenu viewMenu = createViewMenu(simbionic);
        this.add(viewMenu);

        JMenu buildMenu = createBuildMenu(simbionic);
        this.add(buildMenu);

        JMenu helpMenu = createHelpMenu(simbionic);
        this.add(helpMenu);
        
    }

    
    public static JMenu createEditMenu(SimBionicEditor simbionic)
    {
        JMenu editMenu = new JMenu("Edit");

        JMenuItem undoItem = new JMenuItem(simbionic.undoAction);
        undoItem.setToolTipText(null);
        JMenuItem redoItem = new JMenuItem(simbionic.redoAction);
        redoItem.setToolTipText(null);
        JMenuItem cutItem = new JMenuItem(simbionic.cutAction);
        cutItem.setToolTipText(null);
        JMenuItem copyItem = new JMenuItem(simbionic.copyAction);
        copyItem.setToolTipText(null);
        JMenuItem pasteItem = new JMenuItem(simbionic.pasteAction);
        pasteItem.setToolTipText(null);
        JMenuItem deleteItem = new JMenuItem(simbionic.deleteAction);
        deleteItem.setToolTipText(null);
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.addSeparator();
        editMenu.add(simbionic.selectAllAction);
        editMenu.addSeparator();
        editMenu.add(simbionic.findAction);
        editMenu.add(simbionic.replaceAction);
        // editMenu.addSeparator();
        return editMenu;
    }

    public static JMenu createBuildMenu(SimBionicEditor simbionic)
    {
        JMenu menu = new JMenu("Project");

        JMenuItem compileItem = new JMenuItem(simbionic.checkErrorAction);
        compileItem.setToolTipText(null);
        menu.add(compileItem);
        menu.addSeparator();
        
        JMenuItem javaScriptItem = new JMenuItem(simbionic.javaScriptSettingsAction);
        javaScriptItem.setToolTipText(null);
        menu.add(javaScriptItem);
        menu.addSeparator();
        
        JMenuItem buildBreakpointItem = new JMenuItem(simbionic.breakpointAction);
        menu.add(buildBreakpointItem);

        JMenuItem connectItem = new JMenuItem(simbionic.connectAction);
        menu.add(connectItem);
        
        menu.addSeparator();
        
        menu.add(simbionic.settingsAction);
        return menu;
    }
    
    public static JMenu createHelpMenu(SimBionicEditor simbionic)
    {
        JMenu helpMenu = new JMenu("Help");

        JMenuItem aboutItem = new JMenuItem(new AbstractAction("About") {
            public void actionPerformed(ActionEvent arg0)
            {
                Object[] items = {
                        new JLabel("SimBionic JavaScript v" + Version.SIMBIONIC_VERSION ),            
                        new JLabel(Version.SIMBIONIC_BUILD_DATE),
                        new JLabel(Version.COPYRIGHT)
                };
                JOptionPane.showMessageDialog(null,items);  
            }            
        });
        helpMenu.add(aboutItem);
        
        return helpMenu;
    }

    public static JMenu createViewMenu(SimBionicEditor simbionic)
    {
        SB_ToolBar tb = ComponentRegistry.getToolBar();
        JMenu viewMenu = new JMenu("View");
        viewMenu.add(tb._backAction);
        viewMenu.add(tb._forwardAction);
        viewMenu.addSeparator();
        viewMenu.add(simbionic.prevErrorAction);
        viewMenu.add(simbionic.nextErrorAction);
        return viewMenu;
    }

}

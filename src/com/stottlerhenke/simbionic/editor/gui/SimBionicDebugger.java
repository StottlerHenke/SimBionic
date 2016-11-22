package com.stottlerhenke.simbionic.editor.gui;

import javax.swing.UIManager;

/**
 * A version of the SimBionic interactive debugger suitable for invocation 
 * from a third-party application using the SimBionic runtime engine.
 */
public class SimBionicDebugger extends SimBionicFrame
{
    /**
     * Constructor.
     * @param fileName project filename to be opened on startup (may be null)
     */
    public SimBionicDebugger(String fileName)
    {
        super(fileName);
        
        setTitle("SimBionic Debugger");

        // hide the regular SimBionic menu bar and toolbar
        setJMenuBar(null);
        getContentPane().remove(_toolBar);
    }

    /**
     * Connects the debugger to a running SimBionic debug
     * server.
     * @param serverAddress IP address of the debug server
     */
    public void connect(String serverAddress)
    {
        _projectBar.setDebugServerIP(serverAddress);
        _projectBar.connectDebug();
    }

    /**
     * Launches the debugger.  First argument specifies the project
     * file, second (optional) argument specifies server IP address.
     * @param args
     */
    public static void main(String[] args)
    {
        if (args.length == 0) {
            System.err.println("Usage: SimBionicDebugger project-file [server IP]");
            System.exit(-1);
        }

        try 
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }
        
        SimBionicDebugger frame = new SimBionicDebugger(args[0]);
        frame.pack();
        frame.setVisible(true);

        String serverIp = (args.length > 1) ? args[1] : "127.0.0.1";
        frame.connect(serverIp);
    }

}
package com.stottlerhenke.simbionic.editor.gui.summary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.stottlerhenke.simbionic.common.xmlConverters.XMLObjectConverter;
import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.Util;
import com.stottlerhenke.simbionic.editor.gui.SB_Canvas;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_DrawableComposite;
import com.stottlerhenke.simbionic.editor.gui.SB_Element;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;

/**
 * Create images of behaviors used to generate a project summary
 * via xslt.
 * 
 * @author remolina
 *
 */
public class SummaryGenerator {
	protected SimBionicEditor _editor;
	static File directoryOfLastOpenedFile= null; 
	
	/**
	 * Create images of behaviors used to generate a project summary
	 * via xslt.
	 * @param editor
	 */
	public SummaryGenerator (SimBionicEditor editor) {
		this._editor = editor;
	}
	
	/**
	 * Start the generation of the listing, show dialog to select output directory
	 */
	public void generate() {
		JFileChooser fc = 
			new JFileChooser(directoryOfLastOpenedFile == null ? System.getProperty("user.dir") : directoryOfLastOpenedFile.getAbsolutePath()) ;
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setDialogTitle("Select Output Directory");
		if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
			File outputDirectory = fc.getSelectedFile();
			directoryOfLastOpenedFile = outputDirectory;
			generateImages(outputDirectory);
		}
	}
	
	/**
	 * uses swing worker to create images and inform user of progress and results
	 * @param outputDirectory
	 */
	void generateImages (File outputDirectory) {
		Icon workingIcon = Util.getImageIcon("loading.gif");
	    JLabel workingIndicator = new JLabel("Creating Listing ...", workingIcon, SwingUtilities.CENTER);
		JFrame frame = new JFrame();
		frame.setAlwaysOnTop(true);
		frame.setUndecorated(true);
		frame.getContentPane().add(workingIndicator);
		frame.setLocationRelativeTo(null);
		frame.pack();
		frame.setVisible(true);
		
		SwingWorker<Void,Void> task = new SwingWorker<Void, Void> () {
			Exception executionException;
			
			@Override
			protected Void doInBackground() throws Exception {
				try {
					generate(outputDirectory);
				}
				catch (Exception ex){
					ex.printStackTrace();
					executionException = ex;
				}
				return null;
			}
			
			@Override
			protected void done() {
				frame.dispose();
				if (executionException!=null) {
					JOptionPane.showMessageDialog(null, executionException + "\n" + executionException.getMessage(),
							"Save Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		};
		task.execute();
	}
	
	
	/**
	 * Save project xml and behavior images to the given directory
	 * @param outputDirectory
	 * @throws IOException 
	 */
	public void generate (File outputDirectory) throws IOException {
		setupOutputDirectory(outputDirectory);
		SimBionicJava model = _editor.getProjectBar().getDataModel();
		List<SB_Behavior> behaviors = new ArrayList<>();
		collectBehaviors(model.getBehaviors(),behaviors);
		
		for (SB_Behavior behavior : behaviors) {
			//System.out.println(behavior.getName());
			try {
				takeImage(behavior,outputDirectory);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		try {
			File projectFile = _editor.getProjectBar()._projectFile;
			String projectName = (projectFile==null) ? "sb_project" : projectFile.getName();
			int extensionIndex = projectName.indexOf(".");
			if (extensionIndex >-1) {
				projectName = projectName.substring(0,extensionIndex);
			}
			File outputFile = new File(outputDirectory,projectName+".xml");
			XMLObjectConverter.getInstance().saveXML(model, outputFile);
			preview(outputFile);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * invokes the default browser to open the give file. 
	 * @param outputFile
	 */
	protected void preview(File outputFile) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					Desktop.getDesktop().browse(outputFile.toURI());
				}
				catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Listing created at \n" +outputFile.getAbsolutePath(), "Listing Created", JOptionPane.INFORMATION_MESSAGE);	
				}
			}
		}.start();
	}	
	/**
	 * Create directory if necessary, remove any images in the directory
	 * @param outputDirectory
	 * @throws IOException 
	 */
	protected void setupOutputDirectory(File outputDirectory) throws IOException {
		if (outputDirectory.isDirectory()) {
			outputDirectory.mkdirs();
			//delete all png files from the directory
			
			//copy simbionic.xls to output directory
			File in = new File("xsl/simbionic.xsl");
			File out = new File(outputDirectory,"simbionic.xsl");
			if (out.exists()) {
				out.delete();
			}
			if (!out.exists()) {
				Files.copy(in.toPath(),out.toPath());
			}
		}
	}
	
	/**
	 * create SB_Behavior objects for all behaviors in the project
	 * @param behaviorsFolder
	 * @param behaviors
	 */
	void collectBehaviors( BehaviorFolderGroup behaviorsFolder, List<SB_Behavior> behaviors) {
		for (Object obj : behaviorsFolder.getBehaviorOrBehaviorFolder()) {
			if (obj instanceof Behavior) {
				behaviors.add(new SB_Behavior((Behavior)obj));
			} 
			else { // folder
				collectBehaviors(((BehaviorFolder)obj).getBehaviorChildren(), behaviors);
			}
		}
	}


	/**
	 * Create images for the given behavior including its polymorphism
	 * @param behavior
	 * @param outputDirectory directory where behavior images are places
	 * @throws Exception
	 */
    public void takeImage(SB_Behavior behavior, File outputDirectory) throws Exception {
        SB_Polymorphism poly;
        SB_Canvas canvas;
        int polyCount = behavior.getPolyCount();
 
        for (int i = 0; i < polyCount; ++i) {
            poly = behavior.getPoly(i);
            canvas = new SB_Canvas(null);            
            canvas.setPoly(poly);
            takeImage(canvas,behavior,poly,i+1,outputDirectory);
            //we need to take the image twice to correctly calculate the canvas width
            takeImage(canvas,behavior,poly,i+1,outputDirectory);
        }

    }
    
    /**
     * create image for the given behavior polymorphism.&nbsp;The generate image use the name format
     * btn_[behaviorName]_[polyPosition].pgn . 
     * @param canvas
     * @param behavior
     * @param poly
     * @param polyPosition  index in [1,n]
     * @param outputDirectory
     * @throws Exception
     */
    protected void takeImage(SB_Canvas canvas,SB_Behavior behavior, SB_Polymorphism poly, int polyPosition, File outputDirectory) throws Exception {
    	//add the canvas to a container, make sure the canvas is drawn even
    	//if the container is not visible
    	JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(canvas, BorderLayout.CENTER);
		// pack() makes subcomponents in frame get their sizes;
		f.pack();
		canvas.revalidate();
		canvas.repaint();
		
		
		File fileName = new File(outputDirectory,"btn_"+behavior.getName() + "_" + polyPosition+".png");
		createImageIO(canvas,fileName,"png");
    }
    
 
    /**
     * calculate the minimum rectangle starting at (0,0) that will cover the
     * graphical elements (rectangles, conditions, connectors) in the canvas.
     * @param canvas
     * @return
     */
    protected Rectangle calculateBounds(SB_Canvas canvas) {
    	SB_Polymorphism poly = canvas.getPoly();
    	if (poly==null) return null;
    	Vector<SB_Drawable> drawables = (Vector<SB_Drawable>)poly._drawables;
    	if (drawables==null) return null;
    	Rectangle bounds = new Rectangle(0,0,30,30);
    	for (SB_Drawable drawable : drawables) {
    		bounds = union(bounds,position(drawable));
    	}
    	return bounds;
    }
    
    /**
     * union of two rectangles, null if both rectangles are null
     * @param a
     * @param b
     * @return
     */
    Rectangle union(Rectangle a, Rectangle b) {
    	if (a==null) return b;
    	if (b==null) return a;
    	Rectangle union = new Rectangle();
    	Rectangle.union(a, b, union);
    	return union;
    }
    
    /**
     * the bounds for a drawable
     * @param drawable
     * @return
     */
    protected Rectangle position(SB_Drawable drawable) {
    	if (drawable instanceof SB_Element) {
    		return ((SB_Element)drawable).getHRect();
    	}
    	else if (drawable instanceof SB_DrawableComposite) {
    		return position((SB_DrawableComposite)drawable);
    	}
    	return null;
    }
    
    /**
     * union of the bounds of the composite's children
     * @param composite
     * @return
     */
    protected Rectangle position(SB_DrawableComposite composite) {
    	Vector<SB_Drawable> drawables = (Vector<SB_Drawable>)composite._drawables;
    	if (drawables==null) return null;
    	Rectangle bounds = null;
    	for (SB_Drawable drawable : drawables) {
    		bounds = union(bounds,position(drawable));
    	}
    	return bounds;
    }
    
	/**
	 * Writes the container in the specified format to the given file.
	 * The format file should be one returned by {@link ImageIO#getWriterFormatNames()}.
	 * 
	 * @param file
	 * @param formatName  a String containing the informal name of the format. See {@link ImageIO#getWriterFormatNames()}
	 * @throws Exception
	 */
	public void createImageIO(SB_Canvas canvas, File file, String formatName) throws Exception {
		Iterator<ImageWriter> availableWriters = ImageIO.getImageWritersByFormatName(formatName);
		if (availableWriters == null || !availableWriters.hasNext()) {
			throw new RuntimeException("There is not a Java writer to export to " + formatName );
		}

		FileOutputStream output = new FileOutputStream(file);
		
		Rectangle bounds = calculateBounds(canvas);
		if (bounds == null) {
			bounds = new Rectangle(0,0,300,300);
		}

		Dimension size = new Dimension(bounds.width,bounds.height);
		int margin = 10;
		
		// Creates the buffered image from GraphContainer's graphics;
		BufferedImage image = new BufferedImage(size.width+margin*2, 
			size.height+margin*2, BufferedImage.TYPE_INT_RGB);//this is a 8-bit

		Graphics2D g2d = image.createGraphics();

		// fills background with the background color;
		Color oldColor = g2d.getColor();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, size.width+margin*2, size.height+margin*2);
		g2d.setColor(oldColor);
		
		// paints the graph container
		canvas.paint(g2d);
		ImageIO.write(image, formatName, output); 
		output.flush();
		output.close();
	};

}

package com.stottlerhenke.simbionic.editor.gui.summary;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import com.stottlerhenke.simbionic.common.xmlConverters.model.Behavior;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolder;
import com.stottlerhenke.simbionic.common.xmlConverters.model.BehaviorFolderGroup;
import com.stottlerhenke.simbionic.common.xmlConverters.model.SimBionicJava;
import com.stottlerhenke.simbionic.editor.SB_Behavior;
import com.stottlerhenke.simbionic.editor.SimBionicEditor;
import com.stottlerhenke.simbionic.editor.gui.SB_Drawable;
import com.stottlerhenke.simbionic.editor.gui.SB_DrawableComposite;
import com.stottlerhenke.simbionic.editor.gui.SB_Element;
import com.stottlerhenke.simbionic.editor.gui.SB_Polymorphism;

/**
 * 
 * @author remolina
 *
 */
public class SummaryGenerator {
	protected SimBionicEditor _editor;
	 
	public SummaryGenerator ( SimBionicEditor editor) {
		this._editor = editor;
	}
	
	public void generate () {
		SimBionicJava model = _editor.getProjectBar().getDataModel();
		List<SB_Behavior> behaviors = new ArrayList<>();
		collectBehaviors(model.getBehaviors(),behaviors);
		
		for (SB_Behavior behavior : behaviors) {
			System.out.println(behavior.getName());
			try {
				takeImage(behavior);
			}
			catch (Exception e) {
				e.printStackTrace();
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
	 * @throws Exception
	 */
    public void takeImage(SB_Behavior behavior) throws Exception {
        SB_Polymorphism poly;
        Simple_Canvas canvas;
        JViewport viewport;
        JScrollPane scrollCanvas;
        int polyCount = behavior.getPolyCount();
 
        for (int i = 0; i < polyCount; ++i) {
            poly = behavior.getPoly(i);
      
            canvas = new Simple_Canvas();
            scrollCanvas = new JScrollPane(canvas);
            scrollCanvas.getHorizontalScrollBar().setUnitIncrement(10);
            scrollCanvas.getVerticalScrollBar().setUnitIncrement(10);
            //canvas.setNeedToScroll(true); // note: must call// scrollRectToVisible() later...
            
            canvas.setPoly(poly);
            //canvas.scrollRectToVisible(canvas.getPoly()._lastViewRect); // ...because
            scrollCanvas.setAutoscrolls(true);
            takeImage(canvas,behavior,poly);
        }

    }
    
    protected void takeImage(Simple_Canvas canvas,SB_Behavior behavior,  SB_Polymorphism poly) throws Exception {
    	updateCanvas(canvas);
    	JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(canvas, BorderLayout.CENTER);

		// Initializes GUI components inside graph;


		// pack() makes subcomponents in frame get their sizes;
		f.pack();

//		Dimension size = new Dimension(width, graph.getPreferredSize().height);
//		graph.setPreferredSize(size);
//		narrativeLabelPrintResizer.resizeForPrinting(width);

		// pack() again to make subcomponents displayable;
		f.pack();
		canvas.revalidate();
		canvas.repaint();
		
		
		String fileName = "images/"+behavior.getName() + "_" + poly.getIndicesLabel()+".png";
		createImageIO(canvas,fileName,"png");
    	 
    }
    
    protected void updateCanvas(Simple_Canvas canvas) {
        if (canvas.getPoly() != null) {
           // canvas.clearSingle();
           // canvas.updateSingle();
            canvas.getPoly().getElements().updateComplex(null);
            canvas.repaint();
        }
    }
    
    protected Rectangle calculateBounds(Simple_Canvas canvas) {
    	SB_Polymorphism poly = canvas.getPoly();
    	if (poly==null) return null;
    	Vector<SB_Drawable> drawables = (Vector<SB_Drawable>)poly._drawables;
    	if (drawables==null) return null;
    	Rectangle bounds = null;
    	for (SB_Drawable drawable : drawables) {
    		Rectangle dPosition = position(drawable);
    		System.out.println(dPosition);
    		bounds = union(bounds,position(drawable));
    	}
    	return bounds;
    }
    
    Rectangle union(Rectangle a, Rectangle b) {
    	if (a==null) return b;
    	if (b==null) return a;
    	Rectangle union = new Rectangle();
    	Rectangle.union(a, b, union);
    	return union;
    }
    
    protected Rectangle position(SB_Drawable drawable) {
    	if (drawable instanceof SB_Element) {
    		return ((SB_Element)drawable).getHRect();
    	}
    	else if (drawable instanceof SB_DrawableComposite) {
    		return position((SB_DrawableComposite)drawable);
    	}
    	return null;
    }
    
    protected Rectangle position(SB_DrawableComposite composite) {
    	Vector<SB_Drawable> drawables = (Vector<SB_Drawable>)composite._drawables;
    	if (drawables==null) return null;
    	Rectangle bounds = null;
    	for (SB_Drawable drawable : drawables) {
    		Rectangle dPosition = position(drawable);
    		System.out.println(dPosition);
    		bounds = union(bounds,position(drawable));
    	}
    	return bounds;
    }
    
	/**
	 * Writes the container in the specified format to the given file.
	 * The format file should be one returned by {@link ImageIO#getWriterFormatNames()}.
	 * 
	 * @param file
	 * @param formatName  a String containg the informal name of the format. See {@link ImageIO#getWriterFormatNames()}
	 * @throws Exception
	 */
	public void createImageIO(Simple_Canvas canvas,String file, String formatName) throws Exception {
		Iterator<ImageWriter> availableWriters = ImageIO.getImageWritersByFormatName(formatName);
		if (availableWriters == null || !availableWriters.hasNext()) {
			throw new RuntimeException("There is not a Java writer to export to " + formatName );
		}

		FileOutputStream output = new FileOutputStream(file);
		
		Rectangle bounds = calculateBounds(canvas);
		if (bounds == null) {
			bounds = new Rectangle(0,0,300,300);
		}
		bounds.x -=100;
		bounds.width +=200;
		
		System.out.println(bounds);
		Dimension size = new Dimension(bounds.width,bounds.height);
		int margin = 0;
		//  	Dimension size = this.getPreferredSize();
		// Creates the buffered image from GraphContainer's graphics;
		BufferedImage image = new BufferedImage(size.width+margin*2, 
			size.height+margin*2, BufferedImage.TYPE_INT_RGB);//this is a 8-bit

		Graphics2D g2d = image.createGraphics();

		// fills background with the background color;
		Color oldColor = g2d.getColor();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, size.width+margin*2, size.height+margin*2);
		g2d.setColor(oldColor);

		Rectangle2D.Double paintArea = new Rectangle2D.Double(margin, margin, size.width, size.height);
		
		g2d.translate(-(bounds.x - margin/2), -(bounds.y - margin/2));
		// paints the graph container
		canvas.paint(g2d);


		//canvas.paint(g2d, new Rectangle2D.Double(margin, margin, size.width, size.height));

		ImageIO.write(image, formatName, output); 
		output.flush();
		output.close();
	};

}

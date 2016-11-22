package com.stottlerhenke.simbionic.editor.gui.autocomplete;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;

import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class SB_GlassPane extends JPanel {

	public SB_GlassPane(){
		setLayout(null);
		setOpaque(false);

		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		int colorValue = Integer.parseInt("FFFF8C", 16);
		list.setBackground(new Color(colorValue));
		list.setCellRenderer(new MatchListRenderer());

		completionList = new JScrollPane(list);
		add(completionList);
	}

	public void setLocation(JComponent editingComponent){
		Container container = editingComponent.getParent();
		while (container != null && !(container instanceof Window))
			container = container.getParent();

		int verticalOffset = 0;
		int horizontalOffset = 0;
		if (editingComponent instanceof JTextArea) {
			JTextArea textArea = (JTextArea)editingComponent;
			if ( textArea.getCaret()!=null && textArea.getCaret().getMagicCaretPosition()!=null) {
				verticalOffset = textArea.getCaret().getMagicCaretPosition().y;
				horizontalOffset = textArea.getCaret().getMagicCaretPosition().x;
			}
		}


		if (container instanceof JFrame) {
			((JFrame)container).setGlassPane(this);
			verticalOffset += 4;
		}
		else if (container instanceof JDialog)
			((JDialog)container).setGlassPane(this);

		try{
			Point textFieldLocation = editingComponent.getLocationOnScreen();

			Point windowLocation = container.getLocationOnScreen();
			completionList.setLocation(textFieldLocation.x - windowLocation.x + horizontalOffset, textFieldLocation.y - windowLocation.y + verticalOffset);
		}
		catch (IllegalComponentStateException ex){
			// TODO
		}
	}

	/**
	 * updates the glass panel label with the given text 
	 * @param text - html formated text indicating the completions and the current selected
	 *    completions.
	 */
	public void setText(String text){
		list.removeAll();

		if (text == null || text.length() == 0) {
			completionList.setVisible(false);
			completionList.validate();
		}
		else {
			final String[] textList = text.split("<BR>\n");
			if (textList.length > 0 ) {
				list.setModel(new AbstractListModel<String>() {
					@Override
					public int getSize() {
						return textList.length;
					}

					@Override
					public String getElementAt(int index) {
						return textList[index]; 
					}
				});

				adjustPaneSize();  
			}
		}
	}

	/**
	 * set the size of the scroll pane containing the label such that
	 * it "fit" the label.
	 */
	protected void adjustPaneSize() {
		// set the size of the scroll pane to "fit" the label 
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				completionList.invalidate();
				completionList.validate(); //calculate new label dimensions 

				//maximum width/height for the scroll panel containing the 
				//list with the completions
				int maxW = 400;
				int maxH = 300;

				//todo the max sizes should depend on the available space and the
				// location of the caret. 

				//actual sizes for the scroll bar
				Dimension preferredSize = completionList.getPreferredSize();

				int w = preferredSize.width;
				int h = preferredSize.height;

				//account for the space taken by the bars (if any)
				Dimension listPreferredSize = list.getPreferredSize();

				if (completionList.getHorizontalScrollBar()!= null) {
					if (w>=maxW ) {
						//the width of the panel will be set to maxW which will cause the
						// horizontal bar to show up, which means more height might be needed
						h += completionList.getHorizontalScrollBar().getHeight();
					}
					else {
						if (!completionList.getHorizontalScrollBar().isVisible() ) {
							//sometimes the panel preferred size includes space for the bar although is not visible
							//compare the label size and the panel size. If there is not bar, the difference of those
							//values should be at most the size of bar size
							if (preferredSize.height - listPreferredSize.height > completionList.getHorizontalScrollBar().getHeight()) {
								h -= completionList.getHorizontalScrollBar().getHeight();
							}
						}
					}
				}

				if (completionList.getVerticalScrollBar()!= null) {
					if (h >=maxH) {
						//the height of the panel will be set to maxH which will cause the
						// vertical bar to show up, which means more width might be needed
						w += completionList.getVerticalScrollBar().getWidth();
					}
					else {
						if (!completionList.getVerticalScrollBar().isVisible()) {
							//sometimes the panel preferred size includes space for the bar although is not visible
							//compare the label size and the panel size. If there is not bar, the difference of those
							//values should be at most the size of bar size
							if (preferredSize.width -listPreferredSize.width > completionList.getVerticalScrollBar().getWidth()) {
								w -= completionList.getVerticalScrollBar().getWidth();
							}
						}  
					}
				}

				if (list.getModel().getSize() == 1) { // special case
					h = listPreferredSize.height + 20;
				}


				w = Math.min(w, maxW);
				h = Math.min(h, maxH);

				Dimension d = new Dimension(w,h);            
				completionList.setSize(d);
				completionList.setVisible(true);
				completionList.invalidate();
				completionList.validate();

			}
		});
	}

	/**
	 * like {@link #setText(String)} but places the vertical scroll bar (if any)
	 * so that the selectedIndex is shown at the start of the window
	 * 
	 * @param text
	 * @param selectedIndex
	 * @param total
	 */
	public void setText(String text, int selectedIndex, int total) {
		setText(text);
		if (text == null || text.length() == 0)  return;
		//move the scrollbar to show the selection
		if (completionList.getVerticalScrollBar() != null) {
			int range = completionList.getVerticalScrollBar().getMaximum() - completionList.getVerticalScrollBar().getMinimum();
			int val = (int)(((float)(selectedIndex*range)) / ((float) total));
			int value = completionList.getVerticalScrollBar().getMinimum() + val;
			completionList.getVerticalScrollBar().setValue(value);
		}
	}

	public JList<String> list;
	/** scroll pane containing the label **/
	private JScrollPane completionList;

}
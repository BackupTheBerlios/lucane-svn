/*
 * Lucane - a collaborative platform
 * Copyright (C) 2004  Vincent Fiack <vfiack@mail15.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
 
/*
 * Based on HotSheet ItemListCellRenderer.java by John Munsh
 */
package org.lucane.applications.rssreader.gui;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.text.*;

import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.jperdian.rss2.dom.RssItem;


class ItemComponent extends Component 
{
	protected RssItem item;
	protected boolean isSelected, hasFocus, isViewed;
	protected int score;
	
	private final int margin = 8;
	private final int height = 100;
	private final int maxIconWidth = 140;
	private final int maxIconHeight = height - (margin * 2);
	
	
	ItemComponent(RssItem item, boolean isSelected, boolean hasFocus, 
		boolean isViewed, int score) {

		this.item = item;
		this.isSelected = isSelected;
		this.hasFocus = hasFocus;
		this.isViewed = isViewed;
		score = score > 100 ? 100 : score;
		this.score = score < 0 ? 0 : score;
		
		if (isSelected) 
			setBackground(UIManager.getColor("List.selectionBackground"));
		else
			setBackground(UIManager.getColor("List.background"));
	}

	////////////////////////////////////////////////////////////////////////////
	//                                                          Helper Functions
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Calculates any scaling and/or translations that may need to be done to a
	 * given image in order to display it centered within an area of the given
	 * size.
	 */
	protected AffineTransform createTransforms(Image image, Dimension dimension) {
		int imageHeight = image.getHeight(null);
		int imageWidth = image.getWidth(null);
		AffineTransform returnValue = new AffineTransform();

		if ((imageHeight > dimension.getHeight()) || (imageWidth > dimension.getWidth())) 
		{

			// Scaling will be necessary.
			double scaleFactor = Math.min(dimension.getHeight() / imageHeight, 
				dimension.getWidth() / imageWidth);
            
			// Concatenate a scaling transform on.
			AffineTransform temp = new AffineTransform();
			temp.setToScale(scaleFactor, scaleFactor);
			returnValue.concatenate(temp);
            
			// Recalculate the image height and width based on the scale factor.
			// We need to do this because we might need to horizontally or
			// vertically center the image.
			imageHeight *= scaleFactor;
			imageWidth *= scaleFactor;
		}
        
		// Note that it is possible to need both scaling and centering so we
		// always do our scaling first and then after we know our final
		// dimensions for the image we do our centering.
		if ((imageHeight < dimension.getHeight()) || (imageWidth < dimension.getWidth())) 
		{            
			// Centering will be necessary.
			AffineTransform temp = new AffineTransform();
			temp.setToTranslation((dimension.getWidth() - imageWidth) / 
				2, (dimension.getHeight() - imageHeight) / 2);
			returnValue.concatenate(temp);
		}
        
		return returnValue;
	}

	public void paint(Graphics g) 
	{
		float curY;        
		int textMargin = maxIconWidth + (margin * 2);

		Graphics2D g2 = (Graphics2D) g;

		g2.setBackground(getBackground());
		g2.clearRect(0, 0, getWidth(), getHeight());
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		if (hasFocus)
			paintFocusBorder(g2);

		boolean hasIcon = paintChannelIcon(g2);
		if(! hasIcon)
			textMargin = margin;
		
		curY = paintTitle(g2, textMargin);
		paintDescription(g2, textMargin, curY);
		paintScore(g2);
            
		g2.setColor(Color.lightGray);
		g2.fillRect(0, getHeight() - 3, getWidth(), getHeight());

		// If the item has already been viewed, draw a film over the whole
		// item to render it much subtler.
		if (isViewed)
			paintFog(g2);
	}

	private void paintFocusBorder(Graphics2D g2)
	{
		Border focusBorder = UIManager.getBorder("List.focusCellHighlightBorder");
		focusBorder.paintBorder(this, g2, 0, 0, getWidth(), getHeight());
	}

	private void paintDescription(Graphics2D g2, int textMargin, float curY)
	{
		 String description = item.getDescription().trim();
		 if(description.length() == 0)
			 return;
			
		 Font smallFont = g2.getFont().deriveFont(Font.PLAIN, 10.0f);
		 float wrappingWidth = getWidth() - textMargin;

		 description = description.replace('\n', ' ');
		 AttributedString str = new AttributedString(description);
		 str.addAttribute(TextAttribute.FONT, smallFont);
		 AttributedCharacterIterator strIterator = str.getIterator();
		 FontRenderContext fontRenderContext = new FontRenderContext(null, false, false);
		 LineBreakMeasurer measurer = new LineBreakMeasurer(strIterator, fontRenderContext);

		 while (measurer.getPosition() < strIterator.getEndIndex()) 
		 {
			 TextLayout layout = measurer.nextLayout(wrappingWidth);

			 // Adjust current elevation.
			 curY += Math.floor(layout.getAscent());

			 Point2D.Float penPosition = new Point2D.Float(textMargin, curY);
			 layout.draw(g2, penPosition.x, penPosition.y);

			 // Move to next line.
			 curY += layout.getDescent() + layout.getLeading();

			 // If we've done enough lines to fill the printable area, don't
			 // bother getting more.
			 if (curY >= getHeight()-3) 
				 break;
		 }
	}

	private float paintTitle(Graphics2D g2, int textMargin)
	{
		float curY = margin;

		String title = item.getTitle().trim();
		if(title.length() == 0)
			return curY;

		g2.setColor(Color.black);
		Font currentFont = g2.getFont();
		Font boldFont = currentFont.deriveFont(Font.BOLD);
		float wrappingWidth = getWidth() - textMargin;

		AttributedString str = new AttributedString(title);
		str.addAttribute(TextAttribute.FONT, boldFont);
		AttributedCharacterIterator strIterator = str.getIterator();
		FontRenderContext fontRenderContext = new FontRenderContext(null, false, false);
		LineBreakMeasurer measurer = new LineBreakMeasurer(strIterator,	fontRenderContext);

		while (measurer.getPosition() < strIterator.getEndIndex()) 
		{
			TextLayout layout = measurer.nextLayout(wrappingWidth);
			curY += Math.floor(layout.getAscent());
			Point2D.Float penPosition = new Point2D.Float(textMargin, curY);
			layout.draw(g2, penPosition.x, penPosition.y);
			curY += layout.getDescent() + layout.getLeading();
			if (curY >= getHeight()-3)
					break;
		}

		return curY;
	}

	private boolean paintChannelIcon(Graphics2D g2)
	{
		// Draw the icon for the channel of the item.
		ImageIcon imageIcon = null;
		try {
			imageIcon = new ImageIcon(item.getSource().getImage().getURL());
		} catch(Exception e) {}
		
		if(imageIcon == null)
			return false; 

		// Create the scaling transformation needed to ensure that the
		// graphic associated with this channel will fit in the space
		// set aside for it.
		AffineTransform transform = createTransforms(imageIcon.getImage(), 
			new Dimension(maxIconWidth,	maxIconHeight));

		// Now that we have our scaling transformation, create another
		// transform to perform translation (i.e. shift the position of
		// the image) and append the two translations to each other.
		AffineTransform temp = new AffineTransform();
		temp.setToTranslation(margin, margin);
		transform.concatenate(temp);
        
		// Draw the image, applying the transformations to scale and
		// translate it appropriately.
		g2.drawImage(imageIcon.getImage(), transform, null);
		return true;
	}

	private void paintFog(Graphics2D g2)
	{
		AlphaComposite ac =	AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .5f);
		g2.setComposite(ac);

		// The background color is what we use to ghost the item.
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
	}

	private void paintScore(Graphics2D g2)
	{
		int mod = 255 - ((score - 50) * 5);

		if (score > 50) 
			g2.setColor(new Color(255, mod, mod));
		else if (score < 50) 
			g2.setColor(new Color(mod, mod, 255));

		g2.fillRect(0, 0, margin / 2, getHeight());
	}

	public Dimension getPreferredSize() {
		return (new Dimension(300, height));
	}
}
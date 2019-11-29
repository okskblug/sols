package kr.co.atis.uiutil;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;
import javax.swing.text.DefaultHighlighter;

public class HighlightTextAreaLine {

	/**
	 * ref.SSCCE.java 
	 * Line Highlighter
	 * @param evt
	 */
	public static void tAreaHighlighter(JTextArea txtArea) {
		DefaultHighlighter highlighter =  (DefaultHighlighter)txtArea.getHighlighter();
		highlighter.removeAllHighlights();	// Clear Highlighter
		
        DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( new Color(0, 0, 255, 30) );
        highlighter.setDrawsLayeredHighlights(false); // this is the key line

        try {
        	int line 	= txtArea.getLineOfOffset( txtArea.getCaretPosition() );	// Select Line
        	int start 	= txtArea.getLineStartOffset( line );
        	int end 	= txtArea.getLineEndOffset( line );

        	highlighter.addHighlight(start, end, painter );
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
	
	
	/**
	 * Key Event (Key Code Check)
	 * @param e
	 * @param txtArea
	 */
	public static void tAreaHighlighterKeyCheck(KeyEvent e, JTextArea txtArea) {

		if(!e.isShiftDown() && !e.isControlDown() && !e.isAltDown() && e.getKeyCode() != KeyEvent.VK_CAPS_LOCK)
		{
			HighlightTextAreaLine.tAreaHighlighter(txtArea);
		}
	}
	
	
	/**
	 * Compare Highlight
	 * @param txtArea
	 */
	public static void tAreaHighlighterCompare(JTextArea txtArea, int line) {
		DefaultHighlighter highlighter =  (DefaultHighlighter)txtArea.getHighlighter();
		highlighter.removeAllHighlights();	// Clear Highlighter
		
        DefaultHighlighter.DefaultHighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter( new Color(255, 0, 0, 30) );
        highlighter.setDrawsLayeredHighlights(false); // this is the key line

        try {
        	int start 	= txtArea.getLineStartOffset( line );
        	int end 	= txtArea.getLineEndOffset( line );

        	highlighter.addHighlight(start, end, painter );
        } catch(Exception e) {
            e.printStackTrace();
        }
	}
}

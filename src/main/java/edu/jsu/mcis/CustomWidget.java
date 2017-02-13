package edu.jsu.mcis;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class CustomWidget extends JPanel implements MouseListener {
    private java.util.List<ShapeObserver> observers;
    
    
    private final Color HEX_SELECTED_COLOR = Color.green;
    private final Color HEX_DEFAULT_COLOR = Color.white;
	
	private final Color OCT_SELECTED_COLOR = Color.red;
    private final Color OCT_DEFAULT_COLOR = Color.white;
	
    private boolean hexagonIsSelected;
    private Point[] hexagonVertices;
	
	private boolean octagonIsSelected;
	private Point[] octagonVertices;

    
    public CustomWidget() {
        observers = new ArrayList<>();
		
		hexagonIsSelected = true;
		hexagonVertices = new Point[6];
       
        for(int i = 0; i < hexagonVertices.length; i++) { 
			hexagonVertices[i] = new Point();
		}
		
        Dimension dim = getPreferredSize();
        calculateVertices(hexagonVertices,dim.width, dim.height, 0, dim.width/3 , dim.height /2);
        
		octagonIsSelected = false;
		octagonVertices = new Point[8];
		for(int i = 0; i < octagonVertices.length; i++){
			octagonVertices[i] = new Point();
		}
		
		calculateVertices(octagonVertices, dim.width, dim.height, Math.PI * 0.125, dim.width - (dim.width / 3), dim.height / 2);
		setBorder(BorderFactory.createLineBorder(Color.black));
        addMouseListener(this);
    }

    
    public void addShapeObserver(ShapeObserver observer) {
        if(!observers.contains(observer)) observers.add(observer);
    }
    public void removeShapeObserver(ShapeObserver observer) {
        observers.remove(observer);
    }
    private void notifyObservers() {
        ShapeEvent event = new ShapeEvent(hexagonIsSelected, octagonIsSelected);
		
        for(ShapeObserver obs : observers) {
            obs.shapeChanged(event);
        }
    }
    
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(200, 200);
    }

    private void calculateVertices(Point[] vertices, int width, int height, double offsetRad, int offsetX, int offsetY) {
        int side = Math.min(width, height) / 2;
		for(int i = 0; i < vertices.length; i++){
			double rad, x, y; 
			rad = offsetRad + (i * (Math.PI / (vertices.length / 2)));
			x = Math.cos(rad);
			y = Math.sin(rad);
			vertices[i].setLocation(offsetX + (x * (side) / 4), offsetY + (y *(side) / 4));
		}
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
		int width = getWidth();
		int height = getHeight();
		
        calculateVertices(hexagonVertices, width, height, 0, width /3, height / 2);
        Shape shape = getShape(hexagonVertices);
        g2d.setColor(Color.black);
        g2d.draw(shape);
		
        if(hexagonIsSelected) {
            g2d.setColor(HEX_SELECTED_COLOR);
            g2d.fill(shape);
        }
        else {
            g2d.setColor(HEX_DEFAULT_COLOR);
            g2d.fill(shape);            
        }
		
		calculateVertices(octagonVertices, width, height, Math.PI * 0.125, width - (width / 3), height / 2);
		shape = getShape(octagonVertices);
		g2d.setColor(Color.black);
		g2d.draw(shape);
		
		if(octagonIsSelected){
			g2d.setColor(OCT_SELECTED_COLOR);
		}
		else{
			g2d.setColor(OCT_DEFAULT_COLOR);
		}
		g2d.fill(shape);
	}
    

    public void mouseClicked(MouseEvent event) {
        Shape shape = getShape(hexagonVertices);
		
        if(shape.contains(event.getX(), event.getY())) {
            hexagonIsSelected = !hexagonIsSelected;
			octagonIsSelected = !hexagonIsSelected;
            notifyObservers();
        }
		else{
			shape = getShape(octagonVertices);
			if(shape.contains(event.getX(), event.getY())){
				octagonIsSelected = !octagonIsSelected;
				hexagonIsSelected = !octagonIsSelected;
				notifyObservers();
			}
		}
        repaint(getBounds());
    }
    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}
    
    public Shape getShape(Point[] vertex) {
        int[] x = new int[vertex.length];
        int[] y = new int[vertex.length];
        for(int i = 0; i < vertex.length; i++) {
            x[i] = vertex[i].x;
            y[i] = vertex[i].y;
        }
        Shape shape = new Polygon(x, y, vertex.length);
        return shape;
    }
	public Shape[] getShapes(){
		return new Shape[]{
			getShape(hexagonVertices),
			getShape(octagonVertices)
		};
	}
    public boolean isSelected() {
		return isHexagonSelected() || isOctagonSelected(); 
	
	}
	public boolean isHexagonSelected(){
		return hexagonIsSelected;
	}
	public boolean isOctagonSelected(){
		return octagonIsSelected;
	}


	public static void main(String[] args) {
		JFrame window = new JFrame("Custom Widget");
        window.add(new CustomWidget());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(300, 300);
        window.setVisible(true);
	}
}

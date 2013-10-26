
package terroristSimulation;

import java.awt.Color;

import uchicago.src.sim.gui.DrawableEdge;
import uchicago.src.sim.gui.SimGraphics;
import uchicago.src.sim.network.DefaultEdge;
import uchicago.src.sim.network.Node;


/**
 * @author ichoyjx
 *Edge class, shown as lines between nodes.
 */
public class TSEdge extends DefaultEdge implements DrawableEdge{

	private Color color;

	public TSEdge(){}

	public TSEdge(Node from, Node to, float strength, Color color){
		super(from, to, "", strength);
		this.color = color;
	}

	public void setColor(Color c){
		color = c;
	}

	public void draw(SimGraphics g, int fromX, int toX, int fromY, int toY){
		g.drawDirectedLink(color, fromX, toX, fromY, toY);
	}
}

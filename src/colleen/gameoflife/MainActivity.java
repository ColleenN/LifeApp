package colleen.gameoflife;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.GridLayout;

public class MainActivity extends Activity
{
	GridLayout myGrid;
	ViewGroup myRoot;
	private int delay = 500; // Time between steps when in run mode
	private Handler timer;
	Runnable mySimRunner = new Runnable()
	{
		public void run()
		{
			doStep(null);
			timer.postDelayed(mySimRunner, delay);
		}
	};
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		myRoot = ((ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content));
		myGrid = (GridLayout)((ViewGroup) myRoot.getChildAt(0)).getChildAt(0);
		setupGrid();
		timer = new Handler();
	}
	
	public void setupGrid()
	{
		DisplayMetrics dim = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dim);
		
		//Calc # Columns
		int cellW = getBaseContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		myGrid.setColumnCount(dim.widthPixels/cellW);
		
		//Calc # Rows
		int btnH = getBaseContext().getResources().getDimensionPixelSize(R.dimen.btn_height);
		int cellH = getBaseContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		int numRows = (dim.heightPixels - btnH*3)/cellH;
		
		// Populate grid
		for(int i = 0; i < myGrid.getColumnCount()*numRows; i++)
			LayoutInflater.from(getBaseContext()).inflate(R.layout.life_btn_init, myGrid, true);
	}
	
	public void clearBoard(View v)
	{
		for(int i = 0; i < myGrid.getChildCount(); i++)
		{
			((LifeButton)(myGrid.getChildAt(i))).setLive(false);
		}
	}
	
	public void doStep(View v)
	{
		ArrayList<LifeButton> flips = new ArrayList<LifeButton>();
		for(int i = 0; i < myGrid.getChildCount(); i++)
		{
			
			if(toToggle(i)) flips.add((LifeButton)myGrid.getChildAt(i));
		}
		for(LifeButton btn : flips)
		{
			toggle(btn);
		}
	}
	
	
	
	public void runSim(View v)
	{
		mySimRunner.run();
		v.setVisibility(View.INVISIBLE);
		findViewById(R.id.endBtn).setVisibility(View.VISIBLE);
		findViewById(R.id.stepBtn).setEnabled(false);
	}
	
	public void endSim(View v)
	{
		timer.removeCallbacks(mySimRunner);
		v.setVisibility(View.INVISIBLE);
		findViewById(R.id.runBtn).setVisibility(View.VISIBLE);
		findViewById(R.id.stepBtn).setEnabled(true);
	}
	
	
	public int getNumAdjLive(int btnIndex)
	{
		int[] btnCoord = getCoords(btnIndex, myGrid.getColumnCount());
		ArrayList<int[]> adj = getAdjCoord(btnCoord, myGrid.getColumnCount(), myGrid.getRowCount());
		int count = 0;
		for(int[] c : adj)
			if(isLive(getIndex(c, myGrid.getColumnCount()))) count++;
		return count;
	}
	
	public boolean isLive(int btnIndex)
	{
			return ((LifeButton)(myGrid.getChildAt(btnIndex))).isLive();
	}
	
	public boolean toToggle(int btnIndex)
	{
		int adj = getNumAdjLive(btnIndex);
		if(isLive(btnIndex))
		{
			boolean val = !((adj == 2) || (adj == 3));
			return val;
		}
		else
			return adj == 3;
	}
	public void toggle(View v)
	{
		if(! (v instanceof LifeButton)) return;
		LifeButton btn = (LifeButton)v;
		btn.setLive(!btn.isLive());
		v.postInvalidate();
	}
	
	public int[] getCoords(int index, int numCols)
	{
		int[] coord = new int[2];
		coord[0] = index % numCols;
		coord[1] = index / numCols;
		return coord;
	}
	
	public int getIndex(int[] coord, int numCols)
	{
		return coord[0] + numCols * coord[1];	
	}
	
	public ArrayList<int[]> getAdjCoord(int[] coord, int numCols, int numRows)
	{
		ArrayList<int[]> adjCoords = new ArrayList<int[]>();
		
		boolean leftEdge = coord[0] == 0;
		boolean rightEdge = coord[0] == numCols-1;
		boolean topEdge = coord[1] == 0;
		boolean botEdge = coord[1] == numRows-1;
		
		int[] temp;
		
		//Handle side cells
		if(!leftEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]-1;
			temp[1] = coord[1];
			adjCoords.add(temp);
		}
		if(!rightEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]+1;
			temp[1] = coord[1];
			adjCoords.add(temp);
		}
		if(!topEdge)
		{
			temp = new int[2];
			temp[0] = coord[0];
			temp[1] = coord[1]-1;
			adjCoords.add(temp);
		}
		if(!botEdge)
		{
			temp = new int[2];
			temp[0] = coord[0];
			temp[1] = coord[1]+1;
			adjCoords.add(temp);
		}
		
		// Handles corners
		if(!leftEdge && !topEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]-1;
			temp[1] = coord[1]-1;
			adjCoords.add(temp);
		}
		if(!rightEdge && !topEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]+1;
			temp[1] = coord[1]-1;
			adjCoords.add(temp);
		}
		if(!leftEdge && !botEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]-1;
			temp[1] = coord[1]+1;
			adjCoords.add(temp);
		}
		if(!rightEdge && !botEdge)
		{
			temp = new int[2];
			temp[0] = coord[0]+1;
			temp[1] = coord[1]+1;
			adjCoords.add(temp);
		}
		
		return adjCoords;
	}
}

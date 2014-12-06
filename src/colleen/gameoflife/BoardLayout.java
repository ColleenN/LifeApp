package colleen.gameoflife;

import java.util.ArrayList;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;

public class BoardLayout extends GridLayout {

	public BoardLayout(Context context)
	{
		super(context);
	}

	public BoardLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public BoardLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		if(getChildCount() == 0) fillBoard();
		super.onLayout(changed, left, top, right, bottom);
	}
	
	public void evolve()
	{
		ArrayList<LifeButton> flips = new ArrayList<LifeButton>();
		for(int i = 0; i < getChildCount(); i++)
		{
			
			if(toToggle(i)) flips.add((LifeButton)getChildAt(i));
		}
		for(LifeButton btn : flips)
		{
			btn.setLive(!btn.isLive());
			btn.refreshDrawableState();
		}
	}
	
	public void clear()
	{
		for(int i = 0; i < getChildCount(); i++)
		{
			((LifeButton)(getChildAt(i))).setLive(false);
		}
	}
	
	
	
	
	private void fillBoard()
	{
		//Calc # Columns
		int cellW = getContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		setColumnCount(getWidth()/cellW);
		
		//Calc # Rows
		int cellH = getContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		int numRows = getHeight()/cellH;
		
		// Populate grid
		for(int i = 0; i < getColumnCount()*numRows; i++)
			LayoutInflater.from(getContext()).inflate(R.layout.life_btn_init, this, true);
		
		int vertPad = (getHeight() - (numRows*cellH))/2;
		int horizPad = (getWidth() - (getColumnCount()*cellW))/2;
		
		setPadding(horizPad, vertPad, 0, 0);
		
	}
	
	private int getNumAdjLive(int btnIndex)
	{
		int[] btnCoord = getCoords(btnIndex);
		ArrayList<int[]> adj = getAdjCoord(btnCoord);
		int count = 0;
		for(int[] c : adj)
			if(isLive(getIndex(c))) count++;
		return count;
	}
	
	private boolean isLive(int btnIndex)
	{
		return ((LifeButton)(getChildAt(btnIndex))).isLive();
	}
	
	private boolean toToggle(int btnIndex)
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
	
	private int[] getCoords(int index)
	{
		int[] coord = new int[2];
		coord[0] = index % getColumnCount();
		coord[1] = index / getColumnCount();
		return coord;
	}
	
	private int getIndex(int[] coord)
	{
		return coord[0] + getColumnCount() * coord[1];	
	}
	
	private ArrayList<int[]> getAdjCoord(int[] coord)
	{
		ArrayList<int[]> adjCoords = new ArrayList<int[]>();
		
		boolean leftEdge = coord[0] == 0;
		boolean rightEdge = coord[0] == getColumnCount()-1;
		boolean topEdge = coord[1] == 0;
		boolean botEdge = coord[1] == getRowCount()-1;
		
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

package colleen.gameoflife;

import java.util.ArrayList;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;

public class BoardLayout extends GridLayout
{

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
		if(getChildCount() == 0) 
			setupBoard();
		else
		{
			if(getRowCount() > calcNumRows())
				trimRow();
			if(getColumnCount() > calcNumCols())
				trimColumn();
			if(getRowCount() < calcNumRows())
				addRow();
			if(getColumnCount() < calcNumCols())
				addColumn();
		}
		super.onLayout(changed, left, top, right, bottom);
		setPadding(calcHorizPad(), calcVertPad(), 0, 0);
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
	
	//Saves a string representation of the board
	public String save()
	{
		String state = "<board>";
		state += "<width>" + getColumnCount() + "</width>";
		state += "<height>" + getRowCount() + "</height>";
		for(int i = 0; i < getChildCount(); i++)
		{
			if(((LifeButton)getChildAt(i)).isLive())
			{
				int[] coord = getCoords(i);
				state += "<coordinate>" + coord[0] + "," + coord[1] + "</coordinate>";
			}
		}
		state += "</board>";
		return state;
	}
	
	//Fills spaces on board as specified in state
	public void initialize(String state)
	{
		state = state.split("<width>")[1];
		int width = Integer.valueOf(state.split("</width>")[0]);
		state = state.split("</width>")[1];
		state = state.split("<height>")[1];
		int height = Integer.valueOf(state.split("</height>")[0]);
		state = state.split("</height>")[1];
		setColumnCount(width);
		setRowCount(height);
		
		while(state.contains("<coordinate>"))
		{
			state = state.split("<coordinate>")[1];
			String coordStr = state.split("</coordinate>")[0];
			state = state.split("</coordinate>")[1];
			
			int[] coord = new int[2];
			coord[0] = Integer.valueOf(coordStr.split(",")[0]);
			coord[1] = Integer.valueOf(coordStr.split(",")[1]);
			((LifeButton)getChildAt(getIndex(coord))).setLive(true);
		}
	}
	
	private void trimRow()
	{
		for(int i = 0; i < getColumnCount(); i++)
			removeViewAt(getChildCount());
		setRowCount(getRowCount()-1);
	}
	
	private void addRow()
	{
		for(int i = 0; i < getColumnCount(); i++)
			LayoutInflater.from(getContext()).inflate(R.layout.life_btn_init, this, true);
		setRowCount(getRowCount()+1);
	}
	
	private void trimColumn()
	{
		ArrayList<View> toGo = new ArrayList<View>();
		for(int i = 1; i <= getRowCount(); i++)
			toGo.add(getChildAt(getColumnCount() * i - 1));
		for(View v : toGo)
			removeView(v);
		
		setColumnCount(getColumnCount()-1);
	}

	private void addColumn()
	{
		for(int i = 1; i <= getRowCount(); i++)
		{
			View v = LayoutInflater.from(getContext()).inflate(R.layout.life_btn_init, this, false);
			addView(v, (getColumnCount()+1) * i - 1);
		}
		setColumnCount(getColumnCount()+1);
	}
	
	//calculates the # rows board should have
	private int calcNumRows()
	{
		int cellH = getContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		return getHeight()/cellH;
	}
	
	//calculates the # columns board should have
	// calculates the # of columns board should have
	private int calcNumCols()
	{
		int cellW = getContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		return getWidth()/cellW;
	}
	
	private int calcVertPad()
	{
		int cellH = getContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		return (getHeight() - (getRowCount()*cellH))/2;
	}
	
	private int calcHorizPad()
	{
		int cellW = getContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		return (getWidth() - (getColumnCount()*cellW))/2;
	}
	
	private void setupBoard()
	{
		setColumnCount(calcNumCols());
		int numRows = calcNumRows();
		
		// Populate grid
		for(int i = 0; i < getColumnCount()*numRows; i++)
			LayoutInflater.from(getContext()).inflate(R.layout.life_btn_init, this, true);
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
	
	protected ArrayList<int[]> getAdjCoord(int[] coord)
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

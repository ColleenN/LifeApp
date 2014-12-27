package colleen.gameoflife;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

public class Board extends Fragment
{
	protected GridLayout root;
	
	public Board()
	{
		
	}
	
	public Board(GridLayout init)
	{
		root = init;
	}
	
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		root.getViewTreeObserver().addOnGlobalLayoutListener(
		new OnGlobalLayoutListener()
		{
			public void onGlobalLayout()
			{	
				if(root.getChildCount() == 0)
					setupBoard();
				else
				{
					if(root.getRowCount() > calcNumRows())
						trimRow();
					if(root.getColumnCount() > calcNumCols())
						trimColumn();
					if(root.getRowCount() < calcNumRows())
						addRow();
					if(root.getColumnCount() < calcNumCols())
						addColumn();
				}
				root.setPadding(calcHorizPad(), calcVertPad(), 0, 0);
				
				
			}
		});
		
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		if(root == null)
			root = (GridLayout)inflater.inflate(R.layout.board_grid, container, false);
		
		RelativeLayout.LayoutParams settings = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		settings.addRule(RelativeLayout.ABOVE, R.id.topBtnRow);
		root.setLayoutParams(settings);
		
		
		return root;
	}
	
	
	public void clear()
	{
		for(int i = 0; i < root.getChildCount(); i++)
		{
			((LifeButton)(root.getChildAt(i))).setLive(false);
		}
	}
	
	public void evolve()
	{
		ArrayList<LifeButton> flips = new ArrayList<LifeButton>();
		for(int i = 0; i < root.getChildCount(); i++)
		{
			
			if(toToggle(i)) flips.add((LifeButton)root.getChildAt(i));
		}
		for(LifeButton btn : flips)
		{
			btn.setLive(!btn.isLive());
			btn.refreshDrawableState();
		}
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
		return ((LifeButton)(root.getChildAt(btnIndex))).isLive();
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
		coord[0] = index % root.getColumnCount();
		coord[1] = index / root.getColumnCount();
		return coord;
	}
	
	private int getIndex(int[] coord)
	{
		return coord[0] + root.getColumnCount() * coord[1];	
	}
	
	protected ArrayList<int[]> getAdjCoord(int[] coord)
	{
		ArrayList<int[]> adjCoords = new ArrayList<int[]>();
		
		boolean leftEdge = coord[0] == 0;
		boolean rightEdge = coord[0] == root.getColumnCount()-1;
		boolean topEdge = coord[1] == 0;
		boolean botEdge = coord[1] == root.getRowCount()-1;
		
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
	
	private void setupBoard()
	{
		root.setColumnCount(calcNumCols());
		int numRows = calcNumRows();
		
		// Populate grid
		for(int i = 0; i < root.getColumnCount()*numRows; i++)
			LayoutInflater.from(root.getContext()).inflate(R.layout.life_btn_init, root, true);
	}

	private int calcVertPad()
	{
		int cellH = root.getContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		return (root.getHeight() - (calcNumRows()*cellH))/2;
	}
	
	private int calcHorizPad()
	{
		int cellW = root.getContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		return (root.getWidth() - (root.getColumnCount()*cellW))/2;
	}
	
	private void trimRow()
	{
		for(int i = 0; i < root.getColumnCount(); i++)
			root.removeViewAt(root.getChildCount());
		root.setRowCount(root.getRowCount()-1);
	}
	
	private void addRow()
	{
		for(int i = 0; i < root.getColumnCount(); i++)
			LayoutInflater.from(root.getContext()).inflate(R.layout.life_btn_init, root, true);
		root.setRowCount(root.getRowCount()+1);
	}
	
	private void trimColumn()
	{
		ArrayList<View> toGo = new ArrayList<View>();
		for(int i = 1; i <= root.getRowCount(); i++)
			toGo.add(root.getChildAt(root.getColumnCount() * i - 1));
		for(View v : toGo)
			root.removeView(v);
		
		root.setColumnCount(root.getColumnCount()-1);
	}

	private void addColumn()
	{
		for(int i = 1; i <= root.getRowCount(); i++)
		{
			View v = LayoutInflater.from(root.getContext()).inflate(R.layout.life_btn_init, root, false);
			root.addView(v, (root.getColumnCount()+1) * i - 1);
		}
		root.setColumnCount(root.getColumnCount()+1);
	}
	
	private int calcNumRows()
	{
		int cellH = root.getContext().getResources().getDimensionPixelSize(R.dimen.cell_height);
		return root.getHeight()/cellH;
	}
	
	private int calcNumCols()
	{
		int cellW = root.getContext().getResources().getDimensionPixelSize(R.dimen.cell_width);
		return root.getWidth()/cellW;
	}
}

package colleen.gameoflife;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;

public class WrappedBoardLayout extends BoardLayout {

	public WrappedBoardLayout(Context context)
	{
		super(context);
	}

	public WrappedBoardLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public WrappedBoardLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	protected ArrayList<int[]> getAdjCoord(int[] coord)
	{
		ArrayList<int[]> adjCoords = new ArrayList<int[]>();

		int[] temp = new int[2];
		
		for(int x = 0; x < 3; x++)
		{
			for(int y = 0; y < 3; y++)
			{
				temp = new int[2];
				temp[0] = coord[0]-1+x;
				temp[1] = coord[1]-1+y;
				adjCoords.add(temp);
			}
		}
		int midIndex = -1;
		for(int[] c : adjCoords)
		{
			if(c[0] == coord[0] && c[1] == coord[1]) midIndex = adjCoords.indexOf(c);
			if(c[0] == -1) c[0] = getColumnCount()-1;
			if(c[1] == -1) c[1] = getRowCount()-1;
			if(c[0] == getColumnCount()) c[0] = 0;
			if(c[1] == getRowCount()) c[1] = 0; 
		}
		adjCoords.remove(midIndex);
		
		
		return adjCoords;
	}
}
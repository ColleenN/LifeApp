package colleen.gameoflife;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.Button;

public class MainActivity extends Activity
{
	private int delay = 500; // Time between steps when in run mode
	private Handler timer;
	BoardLayout activeBoard;
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
		timer = new Handler();
		activeBoard = (BoardLayout)findViewById(R.id.gameBoard);
	}
	
	public void clearBoard(View v)
	{
		activeBoard.clear();
	}
	
	public void doStep(View v)
	{
		activeBoard.evolve();
	}
	
	public void toggle(View v)
	{
		if(! (v instanceof LifeButton)) return;
		LifeButton btn = (LifeButton)v;
		btn.setLive(!btn.isLive());
		v.postInvalidate();
	}
	
	public void runSim(View v)
	{
		mySimRunner.run();
		v.setVisibility(View.GONE);
		findViewById(R.id.endBtn).setVisibility(View.VISIBLE);
		findViewById(R.id.stepBtn).setEnabled(false);
	}
	
	public void endSim(View v)
	{
		timer.removeCallbacks(mySimRunner);
		v.setVisibility(View.GONE);
		findViewById(R.id.runBtn).setVisibility(View.VISIBLE);
		findViewById(R.id.stepBtn).setEnabled(true);
	}
	
	public void swapType(View v)
	{
		BoardLayout regBoard = (BoardLayout)findViewById(R.id.gameBoard);
		WrappedBoardLayout wrapBoard = (WrappedBoardLayout)findViewById(R.id.wrappedGameBoard);
		if(regBoard.getVisibility() == View.VISIBLE)
		{
			String state = regBoard.save();
			wrapBoard.initialize(state);
			regBoard.setVisibility(View.GONE);
			wrapBoard.setVisibility(View.VISIBLE);
			((Button) v).setText(R.string.btn_label6);
			activeBoard = wrapBoard;
		}
		else
		{
			String state = wrapBoard.save();
			regBoard.initialize(state);
			wrapBoard.setVisibility(View.GONE);
			regBoard.setVisibility(View.VISIBLE);
			((Button) v).setText(R.string.btn_label5);
			activeBoard = regBoard;
		}
		activeBoard.postInvalidate();
	}
	
}
package colleen.gameoflife;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;

public class MainActivity extends Activity
{
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
		timer = new Handler();
	}
	
	public void clearBoard(View v)
	{
		((BoardLayout)findViewById(R.id.gameBoard)).clear();
	}
	
	public void doStep(View v)
	{
		((BoardLayout)findViewById(R.id.gameBoard)).evolve();
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
	
}	
	

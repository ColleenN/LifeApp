package colleen.gameoflife;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends FragmentActivity
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
		
		if(savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction().add(R.id.rootLayout, new Board(), "gameBoard").commit();
		}
	}
	
	public void clearBoard(View v)
	{
		((Board) (getSupportFragmentManager().findFragmentByTag("gameBoard"))).clear();
	}
	
	public void doStep(View v)
	{
		((Board) (getSupportFragmentManager().findFragmentByTag("gameBoard"))).evolve();
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
		FragmentManager fm = getSupportFragmentManager();
		Board newBoard;
		Board current = (Board) fm.findFragmentByTag("gameBoard");
		GridLayout theGrid = (GridLayout) (((ViewGroup) current.getView()).getChildAt(0));
		if(current instanceof WrappedBoard)
		{
			newBoard = new Board( theGrid );
			((TextView) v).setText(R.string.btn_label5);
		}
		else
		{
			newBoard = new WrappedBoard( theGrid );
			((TextView) v).setText(R.string.btn_label6);
		}
		((ViewGroup) theGrid.getParent()).removeView(theGrid);
		fm.beginTransaction().replace(R.id.rootLayout, newBoard, "gameBoard").setCustomAnimations
			(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out).commit();
		
	}
	
}
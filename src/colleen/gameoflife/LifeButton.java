package colleen.gameoflife;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class LifeButton extends ImageButton
{
	private static final int[] LIVE_STATE_SET = {R.attr.live};
	private boolean live;
	
	public LifeButton(Context context)
	{
		super(context);
	}
	
	public LifeButton(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	}

	public LifeButton(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
	
	public boolean isLive()
	{
		return live;
	}

	public void setLive(boolean live)
	{
		this.live = live;
		refreshDrawableState();
	}

	public int[] onCreateDrawableState(int extraSpace)
	{
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (live)
        	mergeDrawableStates(drawableState, LIVE_STATE_SET);
        
        return drawableState;
	}
}
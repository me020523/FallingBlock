package practise.fallingblock;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import practise.fallingblock.view.GameBoard;

/**
 * Created by evilatom on 14-5-25.
 */
public class GameMain extends Activity
    implements View.OnClickListener
{
    private GameBoard mGameBoard = null;
    private Button mUp = null;
    private Button mDown = null;
    private Button mLeft = null;
    private Button mRight = null;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        mGameBoard = (GameBoard)findViewById(R.id.board);
        mUp = (Button)findViewById(R.id.up);
        mUp.setOnClickListener(this);
        mDown = (Button)findViewById(R.id.down);
        mDown.setOnClickListener(this);
        mLeft = (Button)findViewById(R.id.left);
        mLeft.setOnClickListener(this);
        mRight = (Button)findViewById(R.id.right);
        mRight.setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGameBoard.onGameStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mGameBoard.onGameEnd();
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.left:
                mGameBoard.left();
                break;
            case R.id.right:
                mGameBoard.right();
                break;
            case R.id.up:
                mGameBoard.rotate();
                break;
            case R.id.down:
                mGameBoard.down();
                break;
        }
    }
}
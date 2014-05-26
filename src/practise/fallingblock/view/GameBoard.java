package practise.fallingblock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import practise.fallingblock.R;
import practise.fallingblock.utils.IGamePadListener;
import practise.fallingblock.utils.IGameStateListener;

/**
 * Created by evilatom on 14-5-25.
 */
public class GameBoard extends View
    implements IGamePadListener, IGameStateListener
{

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 20;
    private final int BOARD_REFRESH_TIME = 800;
    private final int BOARD_QUICK_REFRESH_TIME = 50;

    private int mBlockElementSize = 0;
    private int mBlockElementPadding = 0;
    private int mBlockElementContainSize = 0;
    private int mBoardWidthInPixel = 0;
    private int mBoardHeightInPixel = 0;
    private int mBoardBorderSize = 0;
    private int mBoardLineSize = 0;

    private Paint mBoardBorderPaint = null;
    private Paint mBoardLinePaint = null;
    private Paint mBlockElementPaint = null;
    private Paint mBlockElementBackGroundPaint = null;

    private int[][] mBoardData = null;
    private Block mCurrentBlock = null;

    private final static int BLOCK_FALLING = 1;
    private final static int BLOCK_LEFT = 2;
    private final static int BLOCK_RIGHT = 3;
    private final static int BLOCK_ROTATE = 4;
    private final static int BLOCK_STOP = 5;
    private final static int BLOCK_SPEEDUP = 6;
    private final static int BLOCK_START = 7;
    private int mBlockState = BLOCK_STOP;


    private Handler mHandler = new Handler();
    public GameBoard(Context context)
    {
        super(context);
    }

    public GameBoard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.GameBoard);
        mBlockElementSize = (int)ta.getDimension(R.styleable.GameBoard_blockElementSize,8.0f);
        mBoardBorderSize = (int)ta.getDimension(R.styleable.GameBoard_borderSize,2.0f);
        mBoardLineSize = (int)ta.getDimension(R.styleable.GameBoard_boardLineSize,1.0f);
        mBlockElementPadding = (int)ta.getDimension(R.styleable.GameBoard_blockElementPadding,1.0f);

        calculateBoardParam();
        createBoardData();

        mBoardBorderPaint = new Paint();
        mBoardBorderPaint.setColor(Color.BLUE);
        mBoardBorderPaint.setStyle(Paint.Style.STROKE);
        mBoardBorderPaint.setStrokeWidth(mBoardBorderSize);

        mBoardLinePaint = new Paint();
        mBoardLinePaint.setColor(Color.GRAY);

        mBlockElementPaint = new Paint();
        mBlockElementPaint.setColor(Color.WHITE);
        mBlockElementPaint.setStyle(Paint.Style.FILL);

        mBlockElementBackGroundPaint = new Paint();
        mBlockElementBackGroundPaint.setColor(Color.BLACK);
        mBlockElementPaint.setStyle(Paint.Style.FILL);
    }

    private Runnable mRefreshRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            mHandler.removeCallbacks(this);
            refreshBoard();
            mHandler.postDelayed(mRefreshRunnable,BOARD_REFRESH_TIME);
        }
    };

    @Override
    public void onGameStart()
    {
        mBlockState = BLOCK_STOP;
        mHandler.postDelayed(mRefreshRunnable,50);
    }

    @Override
    public void onGameEnd()
    {
        mBlockState = BLOCK_STOP;
        mHandler.removeCallbacks(mRefreshRunnable);
    }

    protected void createBoardData()
    {
        mBoardData = new int[BOARD_HEIGHT][BOARD_WIDTH];
        for(int i = 0;i < BOARD_HEIGHT;i++)
            for(int j = 0;j < BOARD_WIDTH;j++)
                mBoardData[i][j] = 0;
    }

    protected void clearBlockFromBoard()
    {
        //clear previous block
        if(mCurrentBlock != null)
        {
            int bw = mCurrentBlock.getWidth();
            int bh = mCurrentBlock.getHeight();
            int bx = mCurrentBlock.getX();
            int by = mCurrentBlock.getY();
            for(int i = by; i < by + bh;i++)
                for(int j = bx;j < bx + bw;j++)
                    mBoardData[i][j] ^= mCurrentBlock.get(i - by,j - bx);
        }
    }

    protected void addBlockToBoard()
    {
        if(mCurrentBlock != null)
        {
            int bw = mCurrentBlock.getWidth();
            int bh = mCurrentBlock.getHeight();
            int bx = mCurrentBlock.getX();
            int by = mCurrentBlock.getY();
            for(int i = by; i < by + bh;i++)
                for(int j = bx;j < bx + bw;j++)
                    mBoardData[i][j] |= mCurrentBlock.get(i - by,j - bx);
        }
    }

    protected void refreshBoard()
    {
        switch (mBlockState)
        {
            case BLOCK_START:
                newBlock();
                addBlockToBoard();
                mBlockState = BLOCK_FALLING;
                break;
            case BLOCK_STOP:
                removeFullLines();
                mBlockState = BLOCK_START;
                break;
            case BLOCK_LEFT:
                if(mCurrentBlock != null && mBlockState == BLOCK_LEFT)
                {
                    if (canLeft())
                    {
                        clearBlockFromBoard();
                        mCurrentBlock.left();
                        addBlockToBoard();
                        mBlockState = BLOCK_FALLING;
                        break;
                    }
                }
            case BLOCK_RIGHT:
                if(mCurrentBlock != null && mBlockState == BLOCK_RIGHT)
                {
                    if (canRight())
                    {
                        clearBlockFromBoard();
                        mCurrentBlock.right();
                        addBlockToBoard();
                        mBlockState = BLOCK_FALLING;
                        break;
                    }
                }
            case BLOCK_ROTATE:
                if(mCurrentBlock != null && mBlockState == BLOCK_ROTATE)
                {
                    if (canRotate())
                    {
                        clearBlockFromBoard();
                        mCurrentBlock.rotate();
                        addBlockToBoard();
                        mBlockState = BLOCK_FALLING;
                        break;
                    }
                }
            case BLOCK_FALLING:
                if(mCurrentBlock != null)
                {
                    if (canFalling())
                    {
                        clearBlockFromBoard();
                        mCurrentBlock.down();
                        addBlockToBoard();
                    }
                    else
                    {
                        mBlockState = BLOCK_STOP;
                        onBlockStop();
                    }
                }
                break;
        }
        invalidate();
    }

    protected boolean canFalling()
    {
        if(mCurrentBlock == null)
            return true;
        int x = mCurrentBlock.getX();
        int y = mCurrentBlock.getY();
        int height = mCurrentBlock.getHeight();
        int width = mCurrentBlock.getWidth();

        //out of the game region
        if(y + height >= BOARD_HEIGHT)
            return false;

        boolean ret = true;
        clearBlockFromBoard();
        for(int i = 0;i < height;i++)
        {
            for(int j = 0;j < width;j++)
            {

                if(mBoardData[y + i + 1][x + j] == 1 && mCurrentBlock.get(i,j) == 1)
                    ret = false;
            }
        }
        addBlockToBoard();
        return ret;
    }

    protected boolean canRight()
    {
        if(mCurrentBlock == null)
            return true;
        int x = mCurrentBlock.getX();
        int y = mCurrentBlock.getY();
        int height = mCurrentBlock.getHeight();
        int width = mCurrentBlock.getWidth();

        //out of the game region
        if(x + width >= BOARD_WIDTH)
            return false;

        boolean ret = true;
        clearBlockFromBoard();
        for(int i = 0;i < height;i++)
        {
            if(mBoardData[y + i][x + width] == 1)
                ret = false;
        }
        addBlockToBoard();
        return ret;
    }

    protected boolean canLeft()
    {
        if(mCurrentBlock == null)
            return true;
        int x = mCurrentBlock.getX();
        int y = mCurrentBlock.getY();
        int height = mCurrentBlock.getHeight();
        int width = mCurrentBlock.getWidth();

        //out of the game region
        if(x <= 0)
            return false;

        boolean ret = true;
        clearBlockFromBoard();
        for(int i = 0;i < height;i++)
        {
            for(int j = 0;j < width;j++)
            {

                if(mBoardData[y + i][x + j - 1] == 1)
                    ret = false;

            }
        }
        addBlockToBoard();
        return ret;
    }

    protected boolean canRotate()
    {
        Block testBlock = mCurrentBlock.clone();
        testBlock.rotate();

        int  x = testBlock.getX();
        int y = testBlock.getY();
        int width = testBlock.getWidth();
        int height = testBlock.getHeight();
        if(y < 0 || y + height >= BOARD_HEIGHT)
            return false;
        if(x < 0 || x + width >= BOARD_WIDTH)
            return false;
        return true;
    }

    protected void removeFullLines()
    {
        int nonEmptyLines = 0;
        boolean empty = true;

        int fullLineBegin = BOARD_HEIGHT;
        int fullLines = 0;
        boolean full = true;
        for(int i = BOARD_HEIGHT - 1;i >= 0;)
        {
            full = true;
            //check whether the current line is full
            for(int j = 0;j < BOARD_WIDTH;j++)
            {
                if(mBoardData[i][j] == 0)
                {
                    full = false;
                    break;
                }
            }

            if(full)
            {
                //the current line is full
                ++fullLines;
                if(fullLineBegin >= BOARD_HEIGHT)
                    fullLineBegin = i;
                --i;
            }
            else
            {
                if(removeCurrentFullLines(fullLineBegin,fullLines))
                {
                    i = fullLineBegin;
                }
                else
                {
                    i--;
                }
                fullLineBegin = BOARD_HEIGHT;
                fullLines = 0;
            }
        }
    }

    protected boolean removeCurrentFullLines(int begin,int lines)
    {
        if(begin >= BOARD_HEIGHT)
            return false;         //no full lines
        //find some full lines, so clear them
        for(int i = begin;i >= lines;i--)
        {
            for(int j = 0;j < BOARD_WIDTH;j++)
            {
                mBoardData[i][j] = mBoardData[i - lines][j];
                mBoardData[i - lines][j] = 0;
            }
        }
        int a = begin;
        return true;
    }

    protected void newBlock()
    {
        mCurrentBlock = Block.randomBlock(this);
        mCurrentBlock.setX((BOARD_WIDTH >> 1) - 1);
        mCurrentBlock.setY(0);
    }

    protected void calculateBoardParam()
    {
        //the size of block element container size
        mBlockElementContainSize = mBlockElementSize + mBlockElementPadding;

        mBoardWidthInPixel = BOARD_WIDTH * mBlockElementContainSize +
                mBoardBorderSize + (BOARD_WIDTH - 1) * mBoardLineSize;
        mBoardHeightInPixel = BOARD_HEIGHT * mBlockElementContainSize +
                mBoardBorderSize + (BOARD_HEIGHT -1)*mBoardLineSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mBoardWidthInPixel,mBoardHeightInPixel);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawBoardBorder(canvas);
        drawBoardGrid(canvas);
        drawBoard(canvas, canvas.getWidth(), canvas.getHeight());
    }

    protected void drawBoardBorder(Canvas canvas)
    {
        canvas.drawRect(0,0,mBoardWidthInPixel,mBoardHeightInPixel,mBoardBorderPaint);
    }

    protected void drawBoardGrid(Canvas canvas)
    {
        //horizontal lines
        int y = 0;
        for(int i = 1; i < BOARD_HEIGHT;i++)
        {

            y = i * mBlockElementContainSize + (i - 1) * mBoardLineSize + mBoardBorderSize;
            canvas.drawLine(0,y,mBoardWidthInPixel,y,mBoardLinePaint);
        }
        int x = 0;
        for(int i = 1;i < BOARD_WIDTH;i++)
        {
            x = i * mBlockElementContainSize + (i - 1) * mBoardLineSize + mBoardBorderSize;
            canvas.drawLine(x,0,x,mBoardHeightInPixel,mBoardLinePaint);
        }
    }

    protected void drawBoard(Canvas canvas,int width,int height)
    {
        int rectW = 0;  //the width of the element square of a block
        int rectH = 0;  // the height of the element square of a block

        rectW = width / BOARD_WIDTH;
        rectH = width / BOARD_HEIGHT;

        for(int i = 0;i < BOARD_HEIGHT;i++)
        {
            for(int j = 0; j < BOARD_WIDTH;j++)
            {
                //drawBlockElementContainer(canvas,mBlockElementBackGroundPaint,i,j);
                if(mBoardData[i][j] == 1)
                {
                    drawBlockElment(canvas,mBlockElementPaint,i,j);
                }
            }
        }
    }

    protected void drawBlockElment(Canvas canvas,Paint paint,int i,int j)
    {
        int left = j * mBlockElementContainSize + (j - 1) * mBoardLineSize + mBlockElementPadding + mBoardBorderSize;
        int top = i * mBlockElementContainSize + (i - 1) * mBoardLineSize + mBlockElementPadding + mBoardBorderSize;
        int right = left + mBlockElementContainSize - mBlockElementPadding;
        int bottom = top + mBlockElementContainSize - mBlockElementPadding;
        canvas.drawRect(left,top,right,bottom,paint);
    }
    protected void drawBlockElementContainer(Canvas canvas,Paint paint,int i,int j)
    {
        int left = j * mBlockElementContainSize + (j - 1) * mBoardLineSize;
        int top = i * mBlockElementContainSize + (i - 1) * mBoardLineSize;
        int right = left + mBlockElementContainSize;
        int bottom = top + mBlockElementContainSize;
        canvas.drawRect(left,top,right,bottom,paint);
    }

    @Override
    public void left()
    {
        mBlockState = BLOCK_LEFT;
        onBlockLeft();
    }

    @Override
    public void right()
    {
        mBlockState = BLOCK_RIGHT;
        onBlockRight();
    }

    @Override
    public void down()
    {
        mBlockState = BLOCK_FALLING;
        onSpeedup();
    }

    @Override
    public void rotate()
    {
        mBlockState = BLOCK_ROTATE;
        onBlockRotate();
    }

    protected void quickRefreshRate()
    {
        mHandler.removeCallbacks(mRefreshRunnable);
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mHandler.removeCallbacks(this);
                refreshBoard();
                mHandler.postDelayed(mRefreshRunnable,BOARD_REFRESH_TIME);
            }
        },50);
    }

    protected void onBlockStop()
    {
        quickRefreshRate();
    }
    protected  void onBlockLeft()
    {
        quickRefreshRate();
    }
    protected  void onBlockRight()
    {
        quickRefreshRate();
    }
    protected  void onBlockRotate()
    {
        quickRefreshRate();
    }
    protected void onSpeedup()
    {
        quickRefreshRate();
    }
}

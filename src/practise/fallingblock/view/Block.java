package practise.fallingblock.view;

/**
 * Created by evilatom on 14-5-25.
 */
public class Block
{
    public static final int TYPE_S = 1;
    public static final int TYPE_Z = TYPE_S + 1;
    public static final int TYPE_J = TYPE_Z + 1;
    public static final int TYPE_L = TYPE_J + 1;
    public static final int TYPE_I = TYPE_L + 1;
    public static final int TYPE_O = TYPE_I + 1;
    public static final int TYPE_T = TYPE_O + 1;
    public static final int TYPE_ALL = TYPE_T + 1;

    //some private attributes of block
    private int mX = 0; //the x position relative to the board
    private int mY = 0; //the y position relative ot the board
    private int mWidth = 0; //the width of the bolck;
    private int mHeight = 0;// the height of the block;

    private int mPattern[][] = null; // the pattern of the block

    private GameBoard mBoard = null;
    private int mType = TYPE_ALL;

    protected Block(int type,GameBoard board)
    {
        mBoard = board;
        mType = type;
        switch (type)
        {
            case TYPE_S:
                mWidth = 3;
                mHeight = 2;
                mPattern = new int[][]{
                        {0,1,1},
                        {1,1,0}
                };
                break;
            case TYPE_Z:
                mWidth = 3;
                mHeight = 2;
                mPattern = new int[][]{
                        {1,1,0},
                        {0,1,1}
                };
                break;
            case TYPE_J:
                mWidth = 2;
                mHeight = 3;
                mPattern = new int[][]{
                        {0,1},
                        {0,1},
                        {1,1}
                };
                break;
            case TYPE_L:
                mWidth = 2;
                mHeight = 3;
                mPattern = new int[][]{
                        {1,0},
                        {1,0},
                        {1,1}
                };
                break;
            case TYPE_I:
                mWidth = 1;
                mHeight = 4;
                mPattern = new int[][]{
                        {1},
                        {1},
                        {1},
                        {1}
                };
                break;
            case TYPE_O:
                mWidth = 2;
                mHeight = 2;
                mPattern = new int[][]{
                        {1,1},
                        {1,1}
                };
                break;
            case TYPE_T:
                mWidth = 3;
                mHeight = 2;
                mPattern = new int[][]{
                        {0,1,0},
                        {1,1,1}
                };
                break;
            default:
                break;
        }
    }

    public static Block SBlock(GameBoard board)
    {
        return new Block(TYPE_S,board);
    }

    public static Block ZBlock(GameBoard board)
    {
        return new Block(TYPE_Z,board);
    }

    public static Block JBlock(GameBoard board)
    {
        return new Block(TYPE_J,board);
    }

    public static Block LBlock(GameBoard board)
    {
        return new Block(TYPE_L,board);
    }

    public static Block IBlock(GameBoard board)
    {
        return new Block(TYPE_I,board);
    }

    public static Block OBlock(GameBoard board)
    {
        return new Block(TYPE_O,board);
    }

    public static Block TBlock(GameBoard board)
    {
        return new Block(TYPE_T,board);
    }

    public int getX()
    {
        return mX;
    }
    public void setX(int x)
    {
        mX = x;
    }

    public int getY()
    {
        return mY;
    }
    public void setY(int y)
    {
        mY = y;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public int get(int i,int j)
    {
        if(i >= mHeight || i < 0)
            return 0;
        if(j >= mWidth || j < 0)
            return 0;
        return mPattern[i][j];
    }

    public void down()
    {
        ++mY;
    }

    public void left()
    {
        --mX;
    }
    public void right()
    {
        ++mX;
    }
    public void rotate()
    {
        int width = mHeight;
        int height = mWidth;
        int data[][] = new int[mWidth][mHeight];
        for(int i = 0;i < mHeight;i++)
        {
            for(int j = 0;j < mWidth;j++)
            {
                data[j][width - i - 1] = mPattern[i][j];
            }
        }

        mPattern = data;
        mHeight = height;
        mWidth = width;
    }
    public Block clone()
    {
        Block block = new Block(mType,mBoard);
        block.mWidth = mWidth;
        block.mHeight = mHeight;
        block.mPattern = new int[mHeight][mWidth];
        block.mX = mX;
        block.mY = mY;
        for(int i = 0;i < block.mHeight;i++)
        {
            for(int j = 0;j < block.mWidth;j++)
            {
                block.mPattern[i][j] = mPattern[i][j];
            }
        }
        return block;
    }

    public static Block randomBlock(GameBoard board)
    {
        int type = (int)(Math.random() * (TYPE_ALL - 1) % (TYPE_ALL - 1)) + 1;
        switch (type)
        {
            case TYPE_S:
                return SBlock(board);
            case TYPE_Z:
                return ZBlock(board);
            case TYPE_J:
                return JBlock(board);
            case TYPE_L:
                return LBlock(board);
            case TYPE_I:
                return IBlock(board);
            case TYPE_O:
                return OBlock(board);
            case TYPE_T:
                return TBlock(board);
        }
        return SBlock(board);
    }
}

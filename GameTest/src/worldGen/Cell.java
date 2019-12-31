package worldGen;

public class Cell
{
    public int value;
    public int px;
    public int py;
    public int pz;
    public int sx = 1;
    public int sy = 1;
    public int sz = 1;
    public int m = 0;
    public boolean rot;
    
    public Cell(int Value, int PX, int PY, int PZ, int M)
    {
        value = Value;
        px = PX;
        py = PY;
        pz = PZ;
        m = M;
        rot = false;
    }
    
    public Cell(int Value, int PX, int PY, int PZ, int M, boolean Rot)
    {
        value = Value;
        px = PX;
        py = PY;
        pz = PZ;
        m = M;
        rot = Rot;
    }
    
    public Cell(int Value, int PX, int PY, int PZ, int SX, int SY, int SZ, boolean Rot)
    {
        value = Value;
        px = PX;
        py = PY;
        pz = PZ;
        sx = SX;
        sy = SY;
        sz = SZ;
        rot = Rot;
        m = 4;
    }
}

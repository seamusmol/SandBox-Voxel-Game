package Villages;

import com.jme3.math.Vector3f;

public class Structure
{
    public Vector3f Position;
    public int Angle;
    public int WX;
    public int WY;
    public int WZ;

    public Structure(Vector3f P, int angle, int wx, int wy, int wz)
    {
        Position = P;
        Angle = angle;
        WX = wx;
        WY = wy;
        WZ = wz;
    }

}

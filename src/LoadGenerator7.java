/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public class LoadGenerator7 extends LoadGenerator{
    public LoadGenerator7(double bottom, double peak) {
        super(bottom, peak);
    }

    @Override
    public double generateLoad(double time) {
        return (getPeak()-getBottom())*Math.pow(Math.cos(time / 100),2)+getBottom();
    }
}

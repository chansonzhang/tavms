/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public class LoadGenerator4 extends LoadGenerator{
    public LoadGenerator4(double bottom, double peak) {
        super(bottom, peak);
    }

    @Override
    public double generateLoad(double time) {
        return getPeak()-0.5*(getPeak()-getBottom())*(Math.cos(time/100)+1.0);
    }
}

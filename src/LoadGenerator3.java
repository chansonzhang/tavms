/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public class LoadGenerator3 extends LoadGenerator{
    public LoadGenerator3(double bottom, double peak) {
        super(bottom, peak);
    }

    @Override
    public double generateLoad(double time) {
        return 0.5*(getPeak()-getBottom())*(Math.cos(time/100)+1.0)+getBottom();
    }
}

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public abstract class LoadGenerator {
    private double bottom;
    private double peak;

    public double getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public double getPeak() {
        return peak;
    }

    public void setPeak(int peak) {
        this.peak = peak;
    }

    public LoadGenerator(double bottom, double peak) {
        this.bottom = bottom;
        this.peak = peak;
    }

    abstract public double generateLoad(double time);
}

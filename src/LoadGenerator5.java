/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public class LoadGenerator5 extends LoadGenerator{
    public LoadGenerator5(double bottom, double peak) {
        super(bottom, peak);
    }

    @Override
    public double generateLoad(double time) {
        return (getPeak()-getBottom())*Math.pow(Math.sin(time / 100),2)+getBottom();
    }
}

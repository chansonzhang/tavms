/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/6.
 */
public class LoadGeneratorNormalDistribution extends LoadGenerator{
    public LoadGeneratorNormalDistribution(double bottom, double peak) {
        super(bottom, peak);
    }

    /**
     * 具有相同的变化趋势（现实中，清晨和深夜处于低负载，白天处于高负载）
     * @param time
     * @return
     */
    @Override
    public double generateLoad(double time) {
        //三角函数的周期为2PI=6.28，则/100后，周期为628s，
        //return 0.5*(getPeak()-getBottom())*(Math.sin(time / 100)+1.0)+getBottom();
        return getPeak()-0.5*(getPeak()-getBottom())*(Math.cos(time/100)+1.0);
    }
}

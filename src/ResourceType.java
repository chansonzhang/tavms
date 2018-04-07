import org.cloudbus.cloudsim.core.CloudSim;

import java.util.HashMap;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/27.
 */
public class ResourceType {
    private int id;
    private double MIPS;
    private double ram;
    private double bw;
    private LoadGenerator mipsGenerator;
    private LoadGenerator ramGenerator;
    private LoadGenerator bwGenerator;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @param MIPS
     * @param ram 单位MB
     * @param bw 单位Kbps
     */
    /*public ResourceType(int MIPS, int ram, int bw) {
        this.MIPS = MIPS;
        this.ram = ram;
        this.bw = bw;
    }*/

    public ResourceType(LoadGenerator mipsGenerator,LoadGenerator ramGenerator,LoadGenerator bwGenerator,int id){
        setId(id);
        setMipsGenerator(mipsGenerator);
        setRamGenerator(ramGenerator);
        setBwGenerator(bwGenerator);
    }

    public double getMIPS() {
    return getMipsGenerator().generateLoad(CloudSim.clock());
}

    public double getRam() {
        return getRamGenerator().generateLoad(CloudSim.clock());
    }

    public double getBw() {
        return getBwGenerator().generateLoad(CloudSim.clock());
    }

    public LoadGenerator getMipsGenerator() {
        return mipsGenerator;
    }

    public void setMipsGenerator(LoadGenerator mipsGenerator) {
        this.mipsGenerator = mipsGenerator;
    }

    public LoadGenerator getRamGenerator() {
        return ramGenerator;
    }

    public void setRamGenerator(LoadGenerator ramGenerator) {
        this.ramGenerator = ramGenerator;
    }

    public LoadGenerator getBwGenerator() {
        return bwGenerator;
    }

    public void setBwGenerator(LoadGenerator bwGenerator) {
        this.bwGenerator = bwGenerator;
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param obj the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object obj) {
        ResourceType resourceType=(ResourceType)obj;
        return (resourceType.getId() == this.getId());
    }
}

import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/26.
 */
public class PeProvisionerTAVMS extends PeProvisionerSimple {
    /**
     * Instantiates a new pe provisioner simple.
     *
     * @param availableMips The total mips capacity of the PE that the provisioner can allocate to VMs.
     * @pre $none
     * @post $none
     */
    public PeProvisionerTAVMS(double availableMips) {
        super(availableMips);
    }

    @Override
    public boolean allocateMipsForVm(String vmUid, double mips) {
        return super.allocateMipsForVm(vmUid, mips);
    }
}

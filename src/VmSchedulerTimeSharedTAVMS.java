import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/26.
 */
public class VmSchedulerTimeSharedTAVMS extends VmSchedulerTimeShared {

    //物理机上的资源分配系数，每个物理机上所有虚拟机的资源之和 <= factor*物理机总的资源
    private int cpuFactor=1;
    private int ramFactor=1;
    private int netFactor=1;
    /**
     * Instantiates a new vm time-shared scheduler.
     *
     * @param pelist the list of PEs of the host where the VmScheduler is associated to.
     */
    public VmSchedulerTimeSharedTAVMS(List<? extends Pe> pelist) {
        super(pelist);
    }


    /**
     * Allocate PEs for a vm.
     *
     * @param vmUid              the vm uid
     * @param mipsShareRequested the list of mips share requested by the vm
     * @return true, if successful
     */
    @Override
    protected boolean allocatePesForVm(String vmUid, List<Double> mipsShareRequested) {
        double totalRequestedMips = 0;
        double peMips = getPeCapacity();
        for (Double mips : mipsShareRequested) {
            // each virtual PE of a VM must require not more than the capacity of a physical PE
            if (mips > peMips) {
                return false;
            }
            totalRequestedMips += mips;
        }

        // This scheduler does not allow over-subscription
        int totalMips=0;
        int allocatedMips=0;
        List<Pe> peList=getPeList();
        for(Pe pe:peList){
            totalMips+=pe.getPeProvisioner().getMips();
            allocatedMips += pe.getPeProvisioner().getTotalAllocatedMips();
        }

        //当cpuFactor<=1时，不允许超分
        if((totalMips * cpuFactor) < (allocatedMips+totalRequestedMips)){
            return false;
        }


        getMipsMapRequested().put(vmUid, mipsShareRequested);
        setPesInUse(getPesInUse() + mipsShareRequested.size());

        if (getVmsMigratingIn().contains(vmUid)) {
            // the destination host only experience 10% of the migrating VM's MIPS
            totalRequestedMips *= 0.1;
        }

        List<Double> mipsShareAllocated = new ArrayList<Double>();
        for (Double mipsRequested : mipsShareRequested) {
            if (getVmsMigratingOut().contains(vmUid)) {
                // performance degradation due to migration = 10% MIPS
                mipsRequested *= 0.9;
            } else if (getVmsMigratingIn().contains(vmUid)) {
                // the destination host only experience 10% of the migrating VM's MIPS
                mipsRequested *= 0.1;
            }
            mipsShareAllocated.add(mipsRequested);
        }

        getMipsMap().put(vmUid, mipsShareAllocated);
        setAvailableMips(getAvailableMips() - totalRequestedMips);

        return true;
    }

    public int getCpuFactor() {
        return cpuFactor;
    }

    public void setCpuFactor(int cpuFactor) {
        this.cpuFactor = cpuFactor;
    }

    public int getRamFactor() {
        return ramFactor;
    }

    public void setRamFactor(int ramFactor) {
        this.ramFactor = ramFactor;
    }

    public int getNetFactor() {
        return netFactor;
    }

    public void setNetFactor(int netFactor) {
        this.netFactor = netFactor;
    }
}

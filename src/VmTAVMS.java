import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.PeProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/11.
 */
public class VmTAVMS extends Vm {
    /**
     * Creates a new Vm object.
     *
     * @param id                unique ID of the VM
     * @param userId            ID of the VM's owner
     * @param mips              the mips
     * @param numberOfPes       amount of CPUs
     * @param ram               amount of ram
     * @param bw                amount of bandwidth
     * @param size              The size the VM image size (the amount of storage it will use, at least initially).
     * @param vmm               virtual machine monitor
     * @param cloudletScheduler cloudletScheduler policy for cloudlets scheduling
     * @pre id >= 0
     * @pre userId >= 0
     * @pre size > 0
     * @pre ram > 0
     * @pre bw > 0
     * @pre cpus > 0
     * @pre priority >= 0
     * @pre cloudletScheduler != null
     * @post $none
     */
    public VmTAVMS(int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm, CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
    }

    /**
     * Sets the mips.
     *
     * @param mips the new mips
     */
    @Override
    protected void setMips(double mips) {
        super.setMips(mips);
    }

    /**
     * Sets the number of pes.
     *
     * @param numberOfPes the new number of pes
     */
    @Override
    protected void setNumberOfPes(int numberOfPes) {
        super.setNumberOfPes(numberOfPes);
    }

    /**
     * 获取请求的pe数目
     *
     * @return
     */
    public int getCurrentRequestedPe() {
        if (isBeingInstantiated()) {
            return getRam();
        }
        double totalRequestedMIPS = getCurrentTotalNeedMIPS();
        int n = (int) (totalRequestedMIPS / getMips());
        if (totalRequestedMIPS - n * getMips() > 0.1) {
            n++;
        }
        double time = CloudSim.clock();
        return n;
    }

    /**
     * Gets the current requested mips.
     *
     * @return the current requested mips
     */
    @Override
    public List<Double> getCurrentRequestedMips() {
        List<Double> currentRequestedMips = new ArrayList<>();
        if (isBeingInstantiated()) {
            for (int i = 0; i < getNumberOfPes(); i++) {
                currentRequestedMips.add(getMips());
            }
        } else {
            double totalRequestedMIPS = getCurrentTotalNeedMIPS();
            while (totalRequestedMIPS > getMips()) {
                totalRequestedMIPS -= getMips();
                currentRequestedMips.add(getMips());
            }
            if (totalRequestedMIPS > 0.1) {
                currentRequestedMips.add(totalRequestedMIPS);
            }
        }
        return currentRequestedMips;
    }

    /**
     * Gets the current requested ram.
     *
     * @return the current requested ram
     */
    @Override
    public int getCurrentRequestedRam() {
        if (isBeingInstantiated()) {
            return getRam();
        }
        double totalRequestedRam = 0.0;
        List<Cloudlet> cloudletsOfThisVm = null;
        try {
            cloudletsOfThisVm = TAVMSUtils.getCloudletsOfVM().get(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (cloudletsOfThisVm != null) {
            for (Cloudlet cloudlet : cloudletsOfThisVm) {
                CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                ResourceType resourceType = cloudletWithResType.getResourceType();
                totalRequestedRam += resourceType.getRam();
            }
        }

        return (int) (totalRequestedRam);
    }

    /**
     * Gets the current requested bw.
     *
     * @return the current requested bw
     */
    @Override
    public long getCurrentRequestedBw() {
        if (isBeingInstantiated()) {
            return getRam();
        }
        double totalRequestedBw = 0.0;
        List<Cloudlet> cloudletsOfThisVm = TAVMSUtils.getCloudletsOfVM().get(this);
        if (cloudletsOfThisVm != null) {
            for (Cloudlet cloudlet : cloudletsOfThisVm) {
                CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                ResourceType resourceType = cloudletWithResType.getResourceType();
                totalRequestedBw += resourceType.getBw();
            }
        }

        return (int) (totalRequestedBw);
    }

    public double getTotalAllocatedMIPS() {
        double total = getHost().getVmScheduler().getTotalAllocatedMipsForVm(this);
        return total;
    }

    public double getTotalCurrentRequestedMIPS() {
        double total = 0.0;
        List<Double> currentRequestedMIPS = getCurrentRequestedMips();
        for (double mips : currentRequestedMIPS) {
            total += mips;
        }
        return total;
    }


    /**
     * 计算当前需要多少MIPS
     *
     * @return
     */
    public double getCurrentTotalNeedMIPS() {
        double totalNeedMIPS = 0.0;
        List<Cloudlet> cloudletsOfThisVm = TAVMSUtils.getCloudletsOfVM().get(this);
        if (cloudletsOfThisVm != null) {
            for (Cloudlet cloudlet : cloudletsOfThisVm) {
                CloudletWithResType cloudletWithResType = (CloudletWithResType) cloudlet;
                ResourceType resourceType = cloudletWithResType.getResourceType();
                double time = CloudSim.clock();
                totalNeedMIPS += resourceType.getMIPS();
            }
        }
        return totalNeedMIPS;
    }

    /**
     * @param newPeNumber
     * @return
     */
    public boolean resizeMIPS(double newMIPS) {
        List<Double> newRequestMIPS = new ArrayList<>();
        boolean result1 = true;
        if (getHost().getAvailableMips() + this.getTotalAllocatedMIPS() < newMIPS) {
            newMIPS = getHost().getAvailableMips() + this.getTotalAllocatedMIPS()-0.01;
            result1 = false;
        }
        int newPeNumber = 0;
        while (newMIPS > getMips()) {
            newRequestMIPS.add(getMips());
            newMIPS -= getMips();
            newPeNumber++;
        }
        if (newMIPS > 0.1) {
            newRequestMIPS.add(newMIPS);
            newPeNumber++;
        }

        //TODO:是否要在这里设置vm的peNumber值？不需要，况且如果后面分配失败，不好回滚
        VmScheduler vmScheduler = this.getHost().getVmScheduler();
        vmScheduler.deallocatePesForVm(this);
        boolean result2 = vmScheduler.allocatePesForVm(this, newRequestMIPS);
        if(!result2){
            System.out.print("");
        }
        return result1 && result2;
    }

    /**
     * @param newRam
     * @return
     */
    public boolean resizeRam(double newRam) {
        boolean result1 = true;
        if (getHost().getRamProvisioner().getAvailableRam() + this.getCurrentAllocatedRam() < newRam) {
            newRam = getHost().getRamProvisioner().getAvailableRam() + this.getCurrentAllocatedRam();
            result1 = false; //资源不足
        }
        RamProvisioner ramProvisioner = getHost().getRamProvisioner();
        ramProvisioner.deallocateRamForVm(this);
        this.setRam((int) newRam+1); //解决allocateRamForVm限制ram请求不超过定义的ram的问题
        boolean result2 = ramProvisioner.allocateRamForVm(this, (int) newRam);
        return result1 && result2;
    }

    /**
     * @param newBw
     * @return
     */
    public boolean resizeBw(double newBw) {
        boolean result1=true;
        if (getHost().getBwProvisioner().getAvailableBw() + this.getCurrentAllocatedBw() < newBw) {
            newBw = getHost().getBwProvisioner().getAvailableBw() + this.getCurrentAllocatedBw();
            result1 = false; //资源不足
        }
        BwProvisioner bwProvisioner = getHost().getBwProvisioner();
        bwProvisioner.deallocateBwForVm(this);
        boolean result2=bwProvisioner.allocateBwForVm(this, (long) newBw);
        return result1 && result2;
    }

}

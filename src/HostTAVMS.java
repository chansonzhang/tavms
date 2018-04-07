import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/3/2.
 */
public class HostTAVMS extends Host {
    /**
     * Instantiates a new host.
     *
     * @param id             the host id
     * @param ramProvisioner the ram provisioner
     * @param bwProvisioner  the bw provisioner
     * @param storage        the storage capacity
     * @param peList         the host's PEs list
     * @param vmScheduler    the vm scheduler
     */
    public HostTAVMS(int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage, List<? extends Pe> peList, VmScheduler vmScheduler) {
        super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
    }

    /**
     * Adds a VM migrating into the current host.
     * 和父类中的方法相同，只是去掉了检查，因为plan中的迁移还未执行，检查会报错
     * @param vm the vm
     */
    @Override
    public void addMigratingInVm(Vm vm) {
        vm.setInMigration(true);
        if (!getVmsMigratingIn().contains(vm)) {
            getVmScheduler().getVmsMigratingIn().add(vm.getUid());
            setStorage(getStorage() - vm.getSize());
            getVmsMigratingIn().add(vm);
            getVmList().add(vm);
            updateVmsProcessing(CloudSim.clock());
            vm.getHost().updateVmsProcessing(CloudSim.clock());
        }
    }
}

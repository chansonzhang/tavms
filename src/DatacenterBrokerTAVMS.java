import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;

import java.util.List;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/26.
 */
public class DatacenterBrokerTAVMS extends DatacenterBroker {
    /**
     * Created a new DatacenterBroker object.
     *
     * @param name name to be associated with this entity (as required by {@link SimEntity} class)
     * @throws Exception the exception
     * @pre name != null
     * @post $none
     */
    public DatacenterBrokerTAVMS(String name) throws Exception {
        super(name);
    }

    public int getCloudletSubmitedCount(){
        return getCloudletSubmittedList().size();
    }
}

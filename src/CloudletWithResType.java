import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;

/**
 * Created by Zhang Chen(zhangchen.shaanxi@gmail.com) on 2017/2/27.
 */
public class CloudletWithResType extends Cloudlet{

    private ResourceType resourceType;
    /**
     * Allocates a new Cloudlet object. The Cloudlet length, input and output
     * file sizes should be greater than or equal to 1. By default this
     * constructor sets the history of this object.
     *
     * @param cloudletId          the unique ID of this Cloudlet
     * @param cloudletLength      the length or size (in MI) of this cloudlet to be
     *                            executed in a PowerDatacenter
     * @param pesNumber           the pes number
     * @param cloudletFileSize    the file size (in byte) of this cloudlet
     *                            <tt>BEFORE</tt> submitting to a Datacenter
     * @param cloudletOutputSize  the file size (in byte) of this cloudlet
     *                            <tt>AFTER</tt> finish executing by a Datacenter
     * @param utilizationModelCpu the utilization model of cpu
     * @param utilizationModelRam the utilization model of ram
     * @param utilizationModelBw  the utilization model of bw
     * @pre cloudletID >= 0
     * @pre cloudletLength >= 0.0
     * @pre cloudletFileSize >= 1
     * @pre cloudletOutputSize >= 1
     * @post $none
     */
    public CloudletWithResType(
            int cloudletId,
            long cloudletLength,
            int pesNumber,
            long cloudletFileSize,
            long cloudletOutputSize,
            UtilizationModel utilizationModelCpu,
            UtilizationModel utilizationModelRam,
            UtilizationModel utilizationModelBw,
            ResourceType resourceType) {
        super(cloudletId, cloudletLength, pesNumber, cloudletFileSize, cloudletOutputSize, utilizationModelCpu, utilizationModelRam, utilizationModelBw);
        setResourceType(resourceType);
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}

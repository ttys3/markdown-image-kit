package info.dong4j.idea.plugin.sdk.qcloud.cos.endpoint;

import info.dong4j.idea.plugin.sdk.qcloud.cos.internal.BucketNameUtils;
import info.dong4j.idea.plugin.sdk.qcloud.cos.region.Region;

public class RegionEndpointBuilder implements EndpointBuilder {
    private Region region;

    public RegionEndpointBuilder(Region region) {
        super();
        this.region = region;
    }

    @Override
    public String buildGeneralApiEndpoint(String bucketName) {
        if (this.region == null) {
            throw new IllegalArgumentException("region is null");
        }
        BucketNameUtils.validateBucketName(bucketName);
        return String.format("%s.%s.myqcloud.com", bucketName, Region.formatRegion(this.region));
    }

    @Override
    public String buildGetServiceApiEndpoint() {
        return "service.cos.myqcloud.com";
    }
}

package com.sequenceiq.provisioning.service;

import com.sequenceiq.provisioning.controller.json.CloudInstanceResult;
import com.sequenceiq.provisioning.domain.CloudInstance;
import com.sequenceiq.provisioning.domain.CloudInstanceDescription;
import com.sequenceiq.provisioning.domain.CloudPlatform;
import com.sequenceiq.provisioning.domain.User;

public interface ProvisionService {

    CloudInstanceResult createCloudInstance(User user, CloudInstance cloudInstance);

    CloudInstanceDescription describeCloudInstance(User user, CloudInstance cloudInstance);

    CloudInstanceDescription describeCloudInstanceWithResources(User user, CloudInstance cloudInstance);

    CloudPlatform getCloudPlatform();

}
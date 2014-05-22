package com.sequenceiq.provisioning.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sequenceiq.provisioning.controller.json.CloudInstanceJson;
import com.sequenceiq.provisioning.domain.CloudInstance;
import com.sequenceiq.provisioning.domain.CloudInstanceDescription;
import com.sequenceiq.provisioning.repository.InfraRepository;

@Component
public class CloudInstanceConverter extends AbstractConverter<CloudInstanceJson, CloudInstance> {

    @Autowired
    private InfraRepository infraRepository;

    @Override
    public CloudInstanceJson convert(CloudInstance entity) {
        CloudInstanceJson cloudInstanceJson = new CloudInstanceJson();
        cloudInstanceJson.setInfraId(entity.getInfra().getId());
        cloudInstanceJson.setClusterSize(entity.getClusterSize());
        cloudInstanceJson.setCloudName(entity.getName());
        cloudInstanceJson.setId(entity.getId());
        cloudInstanceJson.setCloudPlatform(entity.getInfra().cloudPlatform());
        return cloudInstanceJson;
    }

    public CloudInstanceJson convert(CloudInstance entity, CloudInstanceDescription description) {
        CloudInstanceJson cloudInstanceJson = new CloudInstanceJson();
        cloudInstanceJson.setInfraId(entity.getInfra().getId());
        cloudInstanceJson.setClusterSize(entity.getClusterSize());
        cloudInstanceJson.setId(entity.getId());
        cloudInstanceJson.setCloudPlatform(entity.getInfra().cloudPlatform());
        cloudInstanceJson.setDescription(description);
        return cloudInstanceJson;
    }

    @Override
    public CloudInstance convert(CloudInstanceJson json) {
        CloudInstance cloudInstance = new CloudInstance();
        cloudInstance.setClusterSize(json.getClusterSize());
        cloudInstance.setName(json.getCloudName());
        cloudInstance.setInfra(infraRepository.findOne(Long.valueOf(json.getInfraId())));
        return cloudInstance;
    }
}
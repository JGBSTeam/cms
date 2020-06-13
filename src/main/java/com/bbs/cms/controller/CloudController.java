package com.bbs.cms.controller;

import java.util.Optional;

import com.bbs.cms.entity.Cloud;
import com.bbs.cms.repository.CloudRepository;
import com.bbs.cms.result.Result;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Component
@RestController
@RequestMapping(value="/api/cloud")
public class CloudController {
    @Autowired
    public CloudRepository cloudRepo;

    @GetMapping(value="")
    public Iterable<Cloud> getCloud(){
        return cloudRepo.findAll();
    }

    @GetMapping(value="/{username}")
    public Object getCloud(@PathVariable String username){
        Iterable<Cloud> cloud = cloudRepo.findByUsername(username);
        
        return cloud;
    }

    @PostMapping(value="")
    public Object createCloud(@RequestBody Cloud cloud){
        if(cloudRepo.existsByCloudname(cloud.getCloudname()) == false){
            return Result.CloudName_EXISTS.toResponse(HttpStatus.BAD_REQUEST);
        }
        if(cloudRepo.existsByPort(cloud.getOuterport()) == false){
            return Result.CloudPort_EXISTS.toResponse(HttpStatus.BAD_REQUEST);
        }
        boolean response = createDocker(cloud);

        if(response == false){
            return Result.FAIL.toResponse(HttpStatus.BAD_REQUEST);
        }
        cloudRepo.save(cloud);
        return Result.SUCCESS.toResponse(HttpStatus.OK);
    }

    @PutMapping(value="/{idx}")
    public Object updateCloud(@PathVariable int idx, @RequestBody Cloud userCloud){
        Optional<Cloud> cloud = cloudRepo.findById(idx);

        boolean response = changeDocker(cloud.get(), userCloud);
        if(response == false) return Result.FAIL.toResponse(HttpStatus.BAD_REQUEST);
        cloudRepo.save(userCloud);

        return Result.SUCCESS.toResponse(HttpStatus.OK);
    }

    @DeleteMapping(value="/{idx}")
    public Object removeCloud(@PathVariable int idx){
        if(cloudRepo.existsById(idx) == false){
            return Result.Cloud_NOT_FOUND.toResponse(HttpStatus.BAD_REQUEST);
        }

        cloudRepo.deleteById(idx);

        return Result.SUCCESS.toResponse(HttpStatus.OK);
    }

    public int shellCommand(String shell){
    
        int exitCode = 1;
    
        DefaultExecutor executor = new DefaultExecutor();
    
        try {
    
        CommandLine cmdLine = CommandLine.parse(shell);
        exitCode = executor.execute(cmdLine);
    
        } catch (Exception e) {
        e.printStackTrace();
        }

        return exitCode;
    }

    public boolean createDocker(Cloud cloud){
        String shellString = "docker run -itd --previleged --name " + cloud.getCloudname() + " -p " + cloud.getOuterport() + ":" + cloud.getInnerport() + "centos:0.0.1 /sbin/init";
        int exitCode = shellCommand(shellString);
        if(exitCode == 1) return false;
        return true;
    }

    public boolean changeDocker(Cloud cloud, Cloud userCloud){
        String shellString = "docker commit "+ cloud.getCloudname() + " " + cloud.getCloudname();
        int exitCode = shellCommand(shellString);
        if(exitCode == 1) return false;
        String shellString2 = "docker stop " + cloud.getCloudname() + " && docker rm " + cloud.getCloudname();
        exitCode = shellCommand(shellString2);
        if(exitCode == 1) return false;
        String shellString3 = "docker run -itd --previleged --name " + userCloud.getCloudname() + " -p " + userCloud.getOuterport() + ":" + userCloud.getInnerport() + "centos:0.0.1 /sbin/init";
        exitCode = shellCommand(shellString3);
        if(exitCode == 1) return false;

        return true;
    }
}
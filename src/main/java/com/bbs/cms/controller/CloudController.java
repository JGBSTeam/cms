package com.bbs.cms.controller;

import java.util.List;
import java.util.Optional;

import com.bbs.cms.entity.Cloud;
import com.bbs.cms.entity.Kind;
import com.bbs.cms.repository.CloudRepository;
import com.bbs.cms.repository.KindRepository;
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

    @Autowired
    public KindRepository kindRepo;

    @GetMapping(value="")
    public Iterable<Cloud> getCloud(){
        return cloudRepo.findAll();
    }

    @GetMapping(value="/{username}")
    public Object getCloud(@PathVariable String username){
        List<Cloud> cloud = cloudRepo.findByUsername(username);
        
        return cloud;
    }

    @PostMapping(value="")
    public Object createCloud(@RequestBody Cloud cloud){
        if(cloudRepo.existsByCloudname(cloud.getCloudname())){
            return Result.CloudName_EXISTS.toResponse(HttpStatus.BAD_REQUEST);
        }
        if(cloudRepo.existsByOuterPort(cloud.getOuterPort())){
            return Result.CloudPort_EXISTS.toResponse(HttpStatus.BAD_REQUEST);
        }
        boolean response = createDocker(cloud);

        if(!response){
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

        userCloud.setIdx(idx);
        cloudRepo.save(userCloud);

        return Result.SUCCESS.toResponse(HttpStatus.OK);
    }

    @DeleteMapping(value="")
    public Object removeCloud(@RequestBody Cloud cloud){
        if(!cloudRepo.existsById(cloud.getIdx())){
            return Result.Cloud_NOT_FOUND.toResponse(HttpStatus.BAD_REQUEST);
        }
        boolean response = deleteDocker(cloud);
        if(response == false){
            return Result.FAIL.toResponse(HttpStatus.BAD_REQUEST);
        }

        cloudRepo.deleteById(cloud.getIdx());

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

    public boolean checkUsing(String using){
        if(using.equals("실행중")){
            return true;
        }
        return false;
    }
    
    public boolean createDocker(Cloud cloud){
        Optional<Kind> kind = kindRepo.getByKindName(cloud.getKind());
        
        String shellString = "";
        
        if(cloud.getKind().equals("Mysql"))
            shellString = "docker run --name " + cloud.getCloudname() + " -e MYSQL_ROOT_PASSWORD=test1357 -p " + cloud.getOuterPort() + ":" + kind.get().getInnerPort() + " -d " + kind.get().getImage();
        else
            shellString = "docker run -itd --privileged --name " + cloud.getCloudname() + " -p " + cloud.getOuterPort() + ":" + kind.get().getInnerPort() + " " + kind.get().getImage() + ":" + kind.get().getTag() +" /sbin/init";

        int exitCode = shellCommand(shellString);
        if(exitCode == 1) return false;
        return true;
    }

    public boolean changeDocker(Cloud cloud, Cloud userCloud){
        Optional<Kind> kind = kindRepo.getByKindName(cloud.getKind());
        String cloudName = cloud.getCloudname().toLowerCase();
        //docker image commit
        String commitShellString = "docker commit "+ cloud.getCloudname() + " " + cloudName;
        int exitCode = shellCommand(commitShellString);
        if(exitCode == 1) return false;

        //docker stop and docker remove
        if(checkUsing(cloud.getUsingStatus())){
            String stopShellString = "docker stop " + cloud.getCloudname();
            shellCommand(stopShellString);
        }

        String rmShellString = "docker rm " + cloud.getCloudname();
        exitCode = shellCommand(rmShellString);
        if(exitCode == 1) return false;

        //docker run
        String runShellString = "";
        if(kind.get().getKindName().equals("Mysql"))
            runShellString = "docker run -d --name " + userCloud.getCloudname() + " -p " + userCloud.getOuterPort() + ":" + kind.get().getInnerPort() + " " + cloudName;
        else
            runShellString = "docker run -itd --privileged --name " + userCloud.getCloudname() + " -p " + userCloud.getOuterPort() + ":" + kind.get().getInnerPort() + " " + cloudName + " /sbin/init";
        exitCode = shellCommand(runShellString);
        if(exitCode == 1) return false;

        return true;
    }

    public boolean deleteDocker(Cloud cloud){
        int exitCode;
        if(checkUsing(cloud.getUsingStatus())){
            String stopShellString = "docker stop " + cloud.getCloudname();
            exitCode = shellCommand(stopShellString);
            if(exitCode == 1){
                return false;
            }

        }
        String rmShellString = "docker rm " + cloud.getCloudname();
        exitCode = shellCommand(rmShellString);
        if(exitCode == 1){
            return false;
        }
        return true;
    }
}
# CMS (Cloud Management System)
라즈베리파이에 docker를 설치하여 Linux와 Mysql을 제공하는 시스템

# 사용환경
## 라즈베리파이에 Docker 셋팅
+ 다음 명령어를 실행하여 라즈비안에 32bit docker 설치
```
$ curl -fsSL get.docker.com -o get-docker.sh
$ sudo sh get-docker.sh
```

#### Linux 설치 환경
+ dockerhub에서 32bit linux(arm32v7) 이미지를 다운
+ docker Container 실행 및 접속
```
docker run -itd --name "CONTAINER_NAME" --privileged -p "OUTERPORT":22 "LINUXIMAGE_NAME" /sbin/init
docker exec "CONTAINER_NAME" /bin/bash
```
+ docker container에 접속된 것으로 ssh를 설치
각 linux에 맞는 명령어를 사용해 ssh를 search해 줍니다.
```
yum search ssh
apt search ssh
```
search에 나온 ssh server를 설치 후 다음 명령어로 ssh를 실행시켜줍니다.
```
systemctl start sshd
```

#### Mysql 설치 환경
+ dockerhub에서 32bit Mysql(hypriot/rpi-mysql) 설치
+ docker Container 실행
```
docker run --name "CONTAINER_NAME" -e MYSQL_ROOT_PASSWORD="YOUR_PASSWORD" -p "OUTERPORT":3306 hypriot/rpi-mysql
```
+ 실행이 되었다면 mysql에 접속

## Api 사용
+ maven build를 사용하여 jar파일 추출
+ jar파일을 라즈베리파이에서 실행
+ 각 설정에 맞게 data 전송

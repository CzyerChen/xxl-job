## 一、版本内容如下：
1. xxl-job-core-2.0.2版本
2. springboot-2.6.1
3. spring-boot-starter-data-redis-2.6.1
4. redisson-spring-boot-starter-3.16.7

## 二、配置启动
发现底层netty版本均为4.1.70.Final,但存在冲突，具体原因不明确

```bash
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00000001143564d4, pid=21642, tid=0x0000000000000e03
#
# JRE version: Java(TM) SE Runtime Environment (8.0_181-b13) (build 1.8.0_181-b13)
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.181-b13 mixed mode bsd-amd64 compressed oops)
# Problematic frame:
# C  [libnetty_resolver_dns_native_macos_x86_648047274197466035758.dylib+0x64d4]  netty_jni_util_JNI_OnUnload+0x84
#
# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/chenzy/files/gitFile/xxl-job/hs_err_pid21642.log
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#

```
  
## 三、解决方式

定位是netty可能存在的冲突问题（主要是redisson底层的netty与xxljob使用的netty通信存在冲突）

- 在xxl-job中提出netty依赖（xxl-job添加了netty-all的依赖，实际上并没有用到全部）
```xml
 <!-- xxl-job-core -->
        <dependency>
            <groupId>com.xuxueli</groupId>
            <artifactId>xxl-job-core</artifactId>
            <version>${xxljob.version}</version>
            <exclusions>
                <exclusion>
                    <groupId>io.netty</groupId>
                    <artifactId>netty-all</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
```
- 启动后，针对NoClassFound的依赖，进行补充
```xml
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-codec-http</artifactId>
            <version>4.1.70.Final</version>
        </dependency>
```
- 再次启动，相关依赖无缺失，能够正常调度任务、传递参数、调用组件



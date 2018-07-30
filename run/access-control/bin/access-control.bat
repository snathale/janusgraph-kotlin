@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  access-control startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and ACCESS_CONTROL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\access-control.jar;%APP_HOME%\lib\kotlin-janusgraph-ogm-0.13.1.jar;%APP_HOME%\lib\kotlin-gremlin-ogm-0.13.1.jar;%APP_HOME%\lib\kotlin-reflect-1.2.51.jar;%APP_HOME%\lib\clikt-1.3.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.2.51.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.2.51.jar;%APP_HOME%\lib\kotlin-stdlib-1.2.51.jar;%APP_HOME%\lib\gremlin-driver-3.3.3.jar;%APP_HOME%\lib\spark-gremlin-3.3.3.jar;%APP_HOME%\lib\hadoop-gremlin-3.3.3.jar;%APP_HOME%\lib\janusgraph-cassandra-0.2.1.jar;%APP_HOME%\lib\janusgraph-es-0.2.1.jar;%APP_HOME%\lib\janusgraph-cql-0.2.1.jar;%APP_HOME%\lib\janusgraph-core-0.2.1.jar;%APP_HOME%\lib\gremlin-groovy-3.3.3.jar;%APP_HOME%\lib\tinkergraph-gremlin-3.2.9.jar;%APP_HOME%\lib\gremlin-core-3.3.3.jar;%APP_HOME%\lib\kotlin-runtime-1.2.51.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.2.51.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\hadoop-client-2.7.2.jar;%APP_HOME%\lib\hadoop-hdfs-2.7.2.jar;%APP_HOME%\lib\astyanax-thrift-3.8.0.jar;%APP_HOME%\lib\astyanax-recipes-3.8.0.jar;%APP_HOME%\lib\astyanax-cassandra-3.8.0.jar;%APP_HOME%\lib\astyanax-core-3.8.0.jar;%APP_HOME%\lib\cassandra-all-2.1.20.jar;%APP_HOME%\lib\netty-all-4.0.56.Final.jar;%APP_HOME%\lib\groovy-json-2.4.15-indy.jar;%APP_HOME%\lib\groovy-sql-2.4.15-indy.jar;%APP_HOME%\lib\groovy-groovysh-2.4.15-indy.jar;%APP_HOME%\lib\groovy-jsr223-2.4.15-indy.jar;%APP_HOME%\lib\groovy-console-2.4.15.jar;%APP_HOME%\lib\groovy-swing-2.4.15.jar;%APP_HOME%\lib\groovy-templates-2.4.15.jar;%APP_HOME%\lib\groovy-xml-2.4.15.jar;%APP_HOME%\lib\groovy-2.4.15-indy.jar;%APP_HOME%\lib\groovy-2.4.15.jar;%APP_HOME%\lib\cassandra-thrift-2.1.20.jar;%APP_HOME%\lib\commons-lang3-3.3.1.jar;%APP_HOME%\lib\elasticsearch-rest-client-6.0.1.jar;%APP_HOME%\lib\thrift-server-0.3.7.jar;%APP_HOME%\lib\libthrift-0.9.2.jar;%APP_HOME%\lib\hadoop-common-2.7.2.jar;%APP_HOME%\lib\hadoop-auth-2.7.2.jar;%APP_HOME%\lib\httpclient-4.5.2.jar;%APP_HOME%\lib\commons-codec-1.10.jar;%APP_HOME%\lib\commons-configuration-1.10.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\reflections-0.9.9-RC1.jar;%APP_HOME%\lib\cassandra-driver-core-3.1.4.jar;%APP_HOME%\lib\guava-18.0.jar;%APP_HOME%\lib\jackson-mapper-asl-1.9.13.jar;%APP_HOME%\lib\jackson-core-asl-1.9.13.jar;%APP_HOME%\lib\snappy-java-1.1.1.7.jar;%APP_HOME%\lib\spark-core_2.11-2.2.0.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\scala-library-2.11.8.jar;%APP_HOME%\lib\scala-xml_2.11-1.0.5.jar;%APP_HOME%\lib\jackson-databind-2.6.5.jar;%APP_HOME%\lib\hadoop-mapreduce-client-app-2.7.2.jar;%APP_HOME%\lib\hadoop-mapreduce-client-jobclient-2.7.2.jar;%APP_HOME%\lib\hadoop-mapreduce-client-shuffle-2.7.2.jar;%APP_HOME%\lib\hadoop-mapreduce-client-common-2.7.2.jar;%APP_HOME%\lib\hadoop-yarn-client-2.7.2.jar;%APP_HOME%\lib\hadoop-mapreduce-client-core-2.7.2.jar;%APP_HOME%\lib\hadoop-yarn-server-nodemanager-2.7.2.jar;%APP_HOME%\lib\hadoop-yarn-server-common-2.7.2.jar;%APP_HOME%\lib\hadoop-yarn-common-2.7.2.jar;%APP_HOME%\lib\hadoop-yarn-api-2.7.2.jar;%APP_HOME%\lib\commons-lang-2.6.jar;%APP_HOME%\lib\avro-mapred-1.7.7-hadoop2.jar;%APP_HOME%\lib\avro-ipc-1.7.7.jar;%APP_HOME%\lib\avro-ipc-1.7.7-tests.jar;%APP_HOME%\lib\avro-1.7.7.jar;%APP_HOME%\lib\paranamer-2.6.jar;%APP_HOME%\lib\curator-recipes-2.7.1.jar;%APP_HOME%\lib\curator-framework-2.7.1.jar;%APP_HOME%\lib\curator-client-2.7.1.jar;%APP_HOME%\lib\zookeeper-3.4.6.jar;%APP_HOME%\lib\netty-3.9.9.Final.jar;%APP_HOME%\lib\javax.json-1.0.jar;%APP_HOME%\lib\metrics-ganglia-3.0.1.jar;%APP_HOME%\lib\metrics-graphite-3.0.1.jar;%APP_HOME%\lib\metrics-core-3.0.1.jar;%APP_HOME%\lib\spatial4j-0.7.jar;%APP_HOME%\lib\jts-core-1.15.0.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\hppc-0.7.1.jar;%APP_HOME%\lib\high-scale-lib-1.1.4.jar;%APP_HOME%\lib\jsr305-3.0.0.jar;%APP_HOME%\lib\noggit-0.6.jar;%APP_HOME%\lib\commons-text-1.0.jar;%APP_HOME%\lib\logback-classic-1.1.2.jar;%APP_HOME%\lib\jcl-over-slf4j-1.7.21.jar;%APP_HOME%\lib\spark-network-shuffle_2.11-2.2.0.jar;%APP_HOME%\lib\metrics-jvm-3.1.2.jar;%APP_HOME%\lib\metrics-json-3.1.2.jar;%APP_HOME%\lib\metrics-graphite-3.1.2.jar;%APP_HOME%\lib\metrics-core-3.1.2.jar;%APP_HOME%\lib\reporter-config-2.1.0.jar;%APP_HOME%\lib\metrics-core-2.2.0.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\commons-pool-1.6.jar;%APP_HOME%\lib\antlr-3.5.2.jar;%APP_HOME%\lib\ST4-4.0.8.jar;%APP_HOME%\lib\antlr-runtime-3.5.2.jar;%APP_HOME%\lib\netty-handler-4.0.56.Final.jar;%APP_HOME%\lib\vavr-0.9.0.jar;%APP_HOME%\lib\gremlin-shaded-3.3.3.jar;%APP_HOME%\lib\snakeyaml-1.15.jar;%APP_HOME%\lib\javatuples-1.2.jar;%APP_HOME%\lib\jcabi-manifests-1.1.jar;%APP_HOME%\lib\javapoet-1.8.0.jar;%APP_HOME%\lib\exp4j-0.4.8.jar;%APP_HOME%\lib\ivy-2.3.0.jar;%APP_HOME%\lib\jbcrypt-0.4.jar;%APP_HOME%\lib\caffeine-2.3.1.jar;%APP_HOME%\lib\hadoop-annotations-2.7.2.jar;%APP_HOME%\lib\spark-unsafe_2.11-2.2.0.jar;%APP_HOME%\lib\chill_2.11-0.8.0.jar;%APP_HOME%\lib\chill-java-0.8.0.jar;%APP_HOME%\lib\xbean-asm5-shaded-4.4.jar;%APP_HOME%\lib\spark-launcher_2.11-2.2.0.jar;%APP_HOME%\lib\spark-network-common_2.11-2.2.0.jar;%APP_HOME%\lib\jets3t-0.9.3.jar;%APP_HOME%\lib\commons-math3-3.4.1.jar;%APP_HOME%\lib\jul-to-slf4j-1.7.16.jar;%APP_HOME%\lib\compress-lzf-1.0.3.jar;%APP_HOME%\lib\lz4-1.3.0.jar;%APP_HOME%\lib\RoaringBitmap-0.5.11.jar;%APP_HOME%\lib\commons-net-3.1.jar;%APP_HOME%\lib\json4s-jackson_2.11-3.2.11.jar;%APP_HOME%\lib\jersey-container-servlet-2.22.2.jar;%APP_HOME%\lib\jersey-container-servlet-core-2.22.2.jar;%APP_HOME%\lib\jersey-server-2.22.2.jar;%APP_HOME%\lib\jersey-client-2.22.2.jar;%APP_HOME%\lib\jersey-media-jaxb-2.22.2.jar;%APP_HOME%\lib\jersey-common-2.22.2.jar;%APP_HOME%\lib\stream-2.7.0.jar;%APP_HOME%\lib\jackson-module-scala_2.11-2.6.5.jar;%APP_HOME%\lib\oro-2.0.8.jar;%APP_HOME%\lib\pyrolite-4.13.jar;%APP_HOME%\lib\py4j-0.10.4.jar;%APP_HOME%\lib\spark-tags_2.11-2.2.0.jar;%APP_HOME%\lib\commons-crypto-1.0.0.jar;%APP_HOME%\lib\unused-1.0.0.jar;%APP_HOME%\lib\jackson-annotations-2.6.0.jar;%APP_HOME%\lib\jackson-core-2.6.5.jar;%APP_HOME%\lib\gmetric4j-1.0.3.jar;%APP_HOME%\lib\hk2-locator-2.4.0-b34.jar;%APP_HOME%\lib\javassist-3.18.1-GA.jar;%APP_HOME%\lib\dom4j-1.6.1.jar;%APP_HOME%\lib\logback-core-1.1.2.jar;%APP_HOME%\lib\commons-cli-1.2.jar;%APP_HOME%\lib\concurrentlinkedhashmap-lru-1.3.jar;%APP_HOME%\lib\json-simple-1.1.jar;%APP_HOME%\lib\high-scale-lib-1.0.6.jar;%APP_HOME%\lib\super-csv-2.1.0.jar;%APP_HOME%\lib\jna-4.0.0.jar;%APP_HOME%\lib\jamm-0.3.0.jar;%APP_HOME%\lib\joda-time-1.6.2.jar;%APP_HOME%\lib\uuid-3.2.jar;%APP_HOME%\lib\org.apache.servicemix.bundles.commons-csv-1.0-r706900_3.jar;%APP_HOME%\lib\jersey-json-1.9.jar;%APP_HOME%\lib\jettison-1.2.jar;%APP_HOME%\lib\httpcore-4.4.5.jar;%APP_HOME%\lib\httpasyncclient-4.1.2.jar;%APP_HOME%\lib\httpcore-nio-4.4.5.jar;%APP_HOME%\lib\jnr-posix-3.0.27.jar;%APP_HOME%\lib\jnr-ffi-2.0.7.jar;%APP_HOME%\lib\netty-codec-4.0.56.Final.jar;%APP_HOME%\lib\netty-transport-4.0.56.Final.jar;%APP_HOME%\lib\netty-buffer-4.0.56.Final.jar;%APP_HOME%\lib\vavr-match-0.9.0.jar;%APP_HOME%\lib\jcabi-log-0.14.jar;%APP_HOME%\lib\jline-2.12.jar;%APP_HOME%\lib\xmlenc-0.52.jar;%APP_HOME%\lib\commons-httpclient-3.1.jar;%APP_HOME%\lib\jsp-api-2.1.jar;%APP_HOME%\lib\protobuf-java-2.5.0.jar;%APP_HOME%\lib\gson-2.2.4.jar;%APP_HOME%\lib\htrace-core-3.1.0-incubating.jar;%APP_HOME%\lib\commons-compress-1.4.1.jar;%APP_HOME%\lib\jetty-util-6.1.26.jar;%APP_HOME%\lib\xercesImpl-2.9.1.jar;%APP_HOME%\lib\leveldbjni-all-1.8.jar;%APP_HOME%\lib\kryo-shaded-3.0.3.jar;%APP_HOME%\lib\mx4j-3.0.2.jar;%APP_HOME%\lib\mail-1.4.7.jar;%APP_HOME%\lib\bcprov-jdk15on-1.51.jar;%APP_HOME%\lib\java-xmlbuilder-1.0.jar;%APP_HOME%\lib\json4s-core_2.11-3.2.11.jar;%APP_HOME%\lib\javax.ws.rs-api-2.0.1.jar;%APP_HOME%\lib\hk2-api-2.4.0-b34.jar;%APP_HOME%\lib\javax.inject-2.4.0-b34.jar;%APP_HOME%\lib\javax.annotation-api-1.2.jar;%APP_HOME%\lib\jersey-guava-2.22.2.jar;%APP_HOME%\lib\osgi-resource-locator-1.0.1.jar;%APP_HOME%\lib\hibernate-validator-4.3.0.Final.jar;%APP_HOME%\lib\validation-api-1.1.0.Final.jar;%APP_HOME%\lib\fastutil-6.5.7.jar;%APP_HOME%\lib\jackson-module-paranamer-2.6.5.jar;%APP_HOME%\lib\xml-apis-1.3.04.jar;%APP_HOME%\lib\disruptor-3.0.1.jar;%APP_HOME%\lib\junit-4.8.1.jar;%APP_HOME%\lib\stax-api-1.0.1.jar;%APP_HOME%\lib\jffi-1.2.10.jar;%APP_HOME%\lib\jffi-1.2.10-native.jar;%APP_HOME%\lib\asm-commons-5.0.3.jar;%APP_HOME%\lib\asm-analysis-5.0.3.jar;%APP_HOME%\lib\asm-util-5.0.3.jar;%APP_HOME%\lib\asm-tree-5.0.3.jar;%APP_HOME%\lib\asm-5.0.3.jar;%APP_HOME%\lib\jnr-x86asm-1.0.2.jar;%APP_HOME%\lib\jnr-constants-0.9.0.jar;%APP_HOME%\lib\netty-common-4.0.56.Final.jar;%APP_HOME%\lib\apacheds-kerberos-codec-2.0.0-M15.jar;%APP_HOME%\lib\xz-1.0.jar;%APP_HOME%\lib\jaxb-impl-2.2.3-1.jar;%APP_HOME%\lib\jaxb-api-2.2.2.jar;%APP_HOME%\lib\jersey-client-1.9.jar;%APP_HOME%\lib\jersey-guice-1.9.jar;%APP_HOME%\lib\jersey-server-1.9.jar;%APP_HOME%\lib\jersey-core-1.9.jar;%APP_HOME%\lib\jackson-jaxrs-1.9.13.jar;%APP_HOME%\lib\jackson-xc-1.9.13.jar;%APP_HOME%\lib\guice-3.0.jar;%APP_HOME%\lib\minlog-1.3.0.jar;%APP_HOME%\lib\objenesis-2.1.jar;%APP_HOME%\lib\base64-2.3.8.jar;%APP_HOME%\lib\json4s-ast_2.11-3.2.11.jar;%APP_HOME%\lib\scalap-2.11.0.jar;%APP_HOME%\lib\hk2-utils-2.4.0-b34.jar;%APP_HOME%\lib\aopalliance-repackaged-2.4.0-b34.jar;%APP_HOME%\lib\jboss-logging-3.1.0.CR2.jar;%APP_HOME%\lib\apacheds-i18n-2.0.0-M15.jar;%APP_HOME%\lib\api-asn1-api-1.0.0-M20.jar;%APP_HOME%\lib\api-util-1.0.0-M20.jar;%APP_HOME%\lib\stax-api-1.0-2.jar;%APP_HOME%\lib\activation-1.1.jar;%APP_HOME%\lib\javax.inject-1.jar;%APP_HOME%\lib\aopalliance-1.0.jar;%APP_HOME%\lib\cglib-2.2.1-v20090111.jar;%APP_HOME%\lib\asm-3.1.jar

@rem Execute access-control
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %ACCESS_CONTROL_OPTS%  -classpath "%CLASSPATH%" cli.CliKt %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable ACCESS_CONTROL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%ACCESS_CONTROL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
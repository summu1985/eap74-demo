# observability-demo
Code to demonstrate the Observability features in EAP 7.4

1. Start jaeger using 
docker run -d --name jaeger \
  -p 6831:6831/udp \
  -p 5778:5778 \
  -p 14268:14268 \
  -p 16686:16686 \
  jaegertracing/all-in-one:1.16

2. EAP configuration
Use the EAP CLI tool to define an outbound socket binding towards the Jaeger tracer.
[standalone@localhost:9990 /] /socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=jaeger:add(host=localhost, port=6831)
{"outcome" => "success"}
 

Now we can define our MPOT tracer configuration:
[standalone@localhost:9990 /] /subsystem=microprofile-opentracing-smallrye/jaeger-tracer=jaeger-demo:add(sampler-type=const, sampler-param=1, reporter-log-spans=true, sender-binding=jaeger)
{"outcome" => "success"}

Setting the default tracer
Let’s define this new tracer as the default tracer to be used by WildFly:
[standalone@localhost:9990 /] /subsystem=microprofile-opentracing-smallrye:write-attribute(name=default-tracer, value=jaeger-demo)
{
    "outcome" => "success",
    "response-headers" => {
        "operation-requires-reload" => true,
        "process-state" => "reload-required"
    }
}
[standalone@localhost:9990 /] reload


3. Deploy the application
git clone https://github.com/summu1985/eap74-demo.git

cd eap74-demo/observality-demo

mvn clean install

cp target/eap-observability-demo.war.war $EAP_HOME/standalone/deployments

$EAP_HOME/bin/standalone.sh -c standalone-microprofile.xml

Application is deployed in http://localhost:8080/eap-observability-demo

4. Open Jaeger UI at http://localhost:16686/
Select the service from the dropdown and click “Find traces”

5. Prometheus
Download prometheus from https://prometheus.io/download/

Modify the prometheus.yml file to include jboss eap metrics endpoint 

Sample prometheus config file is here : https://github.com/summu1985/eap74-demo/blob/main/observability-demo/prometheus.yml

Run prometheus and then access the prometheus UI
./prometheus

http://localhost:9090/graph

Search for the metrics and then execute.

The metrics can be retrieved from the microprofile metrics endpoint - http://localhost:9990/metrics/application

6. Grafana

Download grafana from : https://grafana.com/grafana/download?platform=mac
Grafana UI is accessible : http://localhost:3000
Default username / password = admin/admin

Add Prometheus as datasource

Create new dashboard and add new panel and then add the metric that you want to be visualized that is exported from prometheus.

Demo dashboard
A sample grafana dashboard for the observability demo is here : https://github.com/summu1985/eap74-demo/blob/main/observability-demo/EAP%207.4%20Observability%20demo-1639595534945.json.

Import it and customize it to your needs.

7. Apache JMeter

Download jmeter from : https://jmeter.apache.org/download_jmeter.cgi

Import the JMeter config file for running 100 threads for infinite time : https://github.com/summu1985/eap74-observability-demo/blob/main/Observality_demo_threadgroup.jmx

Open the config file and modify the thread count / delay / Web service endpoints as per your setup and convenience and then start Jmeter run. The threads run in a continuous loop and to stop the threads, you can stop the Jmeter run from the JMeter UI.


# Live reload demo

Add the wildfly bootable jar maven plugin.

Modify the pom to add the bootable jar plugin

Watch the build using the maven goal :  mvn wildfly-jar:dev-watch

This builds, deploys and runs an embedded EAP server and exposes the service on 8080 and ‘/’ path.

Make any change in code, the plugin will automatically build, and redeploy the code without needing to reload the server or deployment.



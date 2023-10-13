// camel-k: language=java
// camel-k: 

import org.apache.camel.builder.RouteBuilder;

public class Multicast extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Write your routes here, for example:
        from("timer:java?period={{time:1000}}")
            .setBody()
                .constant("message")
            .multicast()
                .parallelProcessing(true)
                .to("seda:a")
                .to("seda:b");

            from("seda:a")
                .process((exchange)-> Thread.sleep(6000))
                .log("a");

                from("seda:b")
                .process((exchange)-> Thread.sleep(1))
                .log("b");
    }
}

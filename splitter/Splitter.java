// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class Splitter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Write your routes here, for example:
        from("file://in?noop=true")
            .split()
            .tokenize("\n",1)
            .log("${body}");
    }
}

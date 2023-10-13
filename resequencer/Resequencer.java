// camel-k: language=java


import org.apache.camel.builder.RouteBuilder;

public class Resequencer extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Write your routes here, for example:
       // from("timer:java?period={{time:1000}}")
        from("timer:java?period={{time:10000}}")
            .setBody(constant("4,2,5,3,1"))
            .split()
            .tokenize(",")
            .resequence(body())
            .log("${body}");
            
    }
}

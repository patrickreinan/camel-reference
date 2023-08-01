// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;

public class MessageRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        // Write your routes here, for example:
        from("file://in?noop=true")
            .split()
            .tokenize("\n",1)
            .setProperty("brand", simple("${body.substring(4,6).trim()}"))
            .setProperty("name", simple("${body.substring(6,22).trim()}"))
            

            .choice()

                .when(simple("${exchangeProperty[brand]} == 'MV'"))
                    .to("direct:marvel")  
                .when(simple("${exchangeProperty[brand]} == 'DC'"))
                    .to("direct:dc")
                .otherwise()
                    .to("direct:unknown")
            .end()
            .setBody(simple("${exchangeProperty[name]} | ${exchangeProperty[brandFullName]} "))
            .to("file://out?fileExist=Append&appendChars=\\n");
            


        from("direct:marvel")
            .setProperty("brandFullName",constant("Marvel"));

        from("direct:dc")
            .setProperty("brandFullName",constant("DC"));

        from("direct:unknown")
             .setProperty("brandFullName",constant("Unknown"));

    }
}

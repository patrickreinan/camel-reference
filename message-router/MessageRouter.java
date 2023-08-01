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
            .end();
            
            


        from("direct:marvel")
            .to("file://out?fileName=marvel.txt&fileExist=TryRename&appendChars=\\n");
            

        from("direct:dc")
            .to("file://out/?fileName=dc.txt&fileExist=Append&appendChars=\\n");

        from("direct:unknown")
             .to("file://out?fileName=unknown.txt&fileExist=Append&appendChars=\\n");

    }
}

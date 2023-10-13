// camel-k: language=java

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;


public class Aggregator extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("file://in?noop=true&fileName=car.txt")
            .routeId("car")
            .to("direct:aggregate");
        
        from("file://in?noop=true&fileName=category.txt")
            .routeId("category")
            .to("direct:aggregate");
        

        from("direct:aggregate")
      
            .aggregate(new CustomStringAggregationStrategy())
        
            .constant(true)
            .completionSize(2)
            .log("${body}");
    }
}


class CustomStringAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        

        boolean first = oldExchange==null;
        Exchange exchange = oldExchange == null ? newExchange : oldExchange;

        String oldBody = first ? null : oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String body = null;

        switch (newExchange.getFromRouteId()) {
            case "car":
                body =  "car:" + newBody;
                break;
        
            case "category" :
                body= "category:" + newBody;
                break;
        }

        
        if(first )
            exchange.getIn().setBody(body);
        else
            exchange.getIn().setBody(oldBody + "\n"+ body); 
        
;
        return exchange;
    }
}
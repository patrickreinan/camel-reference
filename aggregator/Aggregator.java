// camel-k: language=java


import java.util.ArrayList;
import java.util.List;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePropertyKey;
import org.apache.camel.builder.RouteBuilder;


public class Aggregator extends RouteBuilder {

     static final String  CARS_ROUTE_ID="cars";
     static final String CATEGORY_ROUTE_ID="category";



    @Override
    public void configure() throws Exception {

        from("file://in?noop=true&fileName=car.txt")
            .routeId(CARS_ROUTE_ID)
            .split()
            .tokenize("\n")       
            .to("direct:aggregate");
        
        from("file://in?noop=true&fileName=category.txt")
            .routeId(CATEGORY_ROUTE_ID)
            .to("direct:aggregate");
        

        from("direct:aggregate")
      
            .aggregate(new CustomStringAggregationStrategy())
        
            .constant(true)
            // cars may be finished and category may be a name.
            .completionTimeout("1000")
            .completionPredicate(p -> p.getIn().getExchange().getFromRouteId().equals(CARS_ROUTE_ID) && p.getIn().getExchange().getProperty(ExchangePropertyKey.SPLIT_COMPLETE, Boolean.class) && !p.getIn().getBody(Category.class).getName().isEmpty())
            
            .marshal()
            .json()
            .log("${body}");
    
    }



class CustomStringAggregationStrategy implements AggregationStrategy {

    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        
        //old will be null or a category
        //new always will be a string

        boolean first = oldExchange==null;
        
        Category oldBody = first ? new Category() : oldExchange.getIn().getBody(Category.class);
        String newBody = newExchange.getIn().getBody(String.class);

         switch (newExchange.getFromRouteId()) {
            case CARS_ROUTE_ID:
                oldBody.addCar(newBody);
                break;
        
            case CATEGORY_ROUTE_ID :
                oldBody.setName(newBody);
                break;
        }

        //replace new from string to category
        newExchange.getIn().setBody(oldBody);

        return newExchange;
    }
}

class Category {

    private String name;
    private final List<String> cars;

    public Category() {
        cars = new ArrayList<String>();
    }
    
    public void setName(String name) {
        this.name = name;
    }
   public String getName(){
        return name;
    }

    
    public void addCar(String car){
        this.cars.add(car);
    }

    public List<String> getCars(){
        return cars;
    }



 
}
    

}
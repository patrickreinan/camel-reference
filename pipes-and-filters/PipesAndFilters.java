// camel-k: language=java

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.DataFormatDefinition;
import org.apache.camel.Processor;
import org.apache.camel.Exchange;


public class PipesAndFilters extends RouteBuilder {

    @Override
    public void configure() throws Exception {

       

        // Write your routes here, for example:
        from("file://in")
            .routeId("fileIn")
            .to("direct:parse")
            .to("direct:transform")
            .to("direct:output");
            
        

        from("direct:transform")
            .routeId("transform")
            .process((exc)->{
                Order o = exc.getMessage().getBody(Order.class);
                exc.getMessage().setBody(o);
            });


        from("direct:parse")
            .routeId("parse")
            .unmarshal()
            .json(Order.class);


        from("direct:log")
            .routeId("log")
            .log("${body}");

        from("direct:output")
            .id("output")
            .marshal()
            .json()
            .to("file://out?fileExist=Override")
            .to("direct:log");
    }
   

}

 class Order {
    private int customerId;
    private OrderItem[] items;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setItems(OrderItem[] items){
        this.items=items;
    }

    public OrderItem[] getItems(){
        return items;
    }

    public int getTotal() {
        
        int total = 0;

        for(OrderItem item : items){
            total += item.getQty() * item.getPrice();
        }

        return total;
    }
   

    
}

class OrderItem {
    private int id;
    private int qty;
    private int price;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

  

    public void setQty(int qty){
        this.qty=qty;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice(){
        return price;
    }

    public void setPrice(int price){
        this.price=price;
    }

}






```java
@RestController
@Slf4j
@AllArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping("/orders/find")
    public OrderResponse getProduct(@RequestParam("id") String id) {
        return service.findById(id);
    }

    @GetMapping("/orders/create")
    public CreatedOrderResponse createOrder(@RequestParam("product") String productEan,
                                            @RequestParam("price") Double price) {
        return service.createOrder(productEan, price);
    }

    @PutMapping("/orders/delete")
    public CreatedOrderResponse deleteOrder(@RequestParam("id") String id) {
        return service.delete(id);
    }

    @PutMapping("/orders/order-item/create")
    public CreatedOrderItemResponse createOrderItems(@RequestParam("id") String itemId,
                                                     @RequestParam("product") String productEan,
                                                     @RequestParam("price") Double price) {
        return service.createOrderItem(itemId, productEan, price);
    }
}
```



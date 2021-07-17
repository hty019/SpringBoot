package jpabook.jpashop.api.order.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Data;

@Data
public class OrderQueryItemDto {
    private Long orderId;
    private String itemName; //상품명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderQueryItemDto(Long orderId, String itemName, int orderPrice, int count) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.orderPrice = orderPrice;
        this.count = count;
    }
}

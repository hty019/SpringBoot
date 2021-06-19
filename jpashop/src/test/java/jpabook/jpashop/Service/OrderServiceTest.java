package jpabook.jpashop.Service;

import jpabook.jpashop.Repository.OrderRepository;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class OrderServiceTest {

    @Autowired private EntityManager em;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {

        //given
        Member member = getMember();

        Item item = getBook("사과", 1000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문 시 상태는 order", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문 상품 수가 일치해야 한다.", 1, getOrder.getOrderItems().size());
        assertEquals("주문 총액이 일치해야 한다.", 1000*orderCount,getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 감소해야한다.",10-orderCount, item.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception {

        //given
        Member member = getMember();
        Item item = getBook("사과", 1000, 10);

        //when
        int orderCount = 12;
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //then
        fail();
    }
    @Test
    public void 주문취소() throws Exception {
        Member member = getMember();
        Item book = getBook("사과책", 1000, 10);

        Long orderId = orderService.order(member.getId(), book.getId(), 2);

        orderService.cancelOrder(orderId);

        Order order = orderRepository.findOne(orderId);
        assertEquals("주문상태가 취소여야한다.", OrderStatus.CANCEL, order.getStatus());
        assertEquals("재고가 복구되어야한다.",10,book.getStockQuantity());
    }

    private Item getBook(String name, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(name);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return item;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("태연");
        member.setAddress(new Address("서울", "두텁바위로", "04337"));

        em.persist(member);
        return member;
    }
}

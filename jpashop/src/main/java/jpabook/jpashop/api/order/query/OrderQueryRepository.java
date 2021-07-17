package jpabook.jpashop.api.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrders(int offset, int limit) {
        List<OrderQueryDto> results = em.createQuery(
                "select new jpabook.jpashop.api.order.query.OrderQueryDto(o.id, o.member.name, o.orderDate, o.status, o.delivery.address)" +
                        " from Order o" +
                        " join o.member m" +
                        " join o.delivery d", OrderQueryDto.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

        List<Long> orderIds = results.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
        List<OrderQueryItemDto> orderItemDtos = em.createQuery(
                "select new jpabook.jpashop.api.order.query.OrderQueryItemDto(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        "from OrderItem oi" +
                        " join oi.item i" +
                        " where oi.order.id in :ids", OrderQueryItemDto.class)
                .setParameter("ids",orderIds)
                .getResultList();
        Map<Long, List<OrderQueryItemDto>> collect = orderItemDtos.stream().collect(Collectors.groupingBy(o -> o.getOrderId()));
        results.stream().forEach(o -> o.setOrderItems(collect.get(o.getOrderId())));
        return results;
    }
}

package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    private final QOrder order = QOrder.order;
    private final QMember member = QMember.member;

    public void save(Order order) {
        em.persist(order);
    }
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch) {

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return queryFactory
                .select(order)
                .from(order)
                .join(order.member, member) // order와 member를 조인
                .where(
                        statusEq(orderSearch.getOrderStatus()), // 주문 상태 조건
                        nameLike(orderSearch.getMemberName()) // 회원 이름 조건
                )
                .limit(100)
                .fetch(); // 결과 리스트 반환
    }
    private BooleanExpression statusEq(OrderStatus statusCond) {
        if (statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }
    private BooleanExpression nameLike(String nameCond) {
        if (!StringUtils.hasText(nameCond)){
            return null;
        }
        return member.name.like(nameCond);
    }
}

## 예시: 온라인 쇼핑몰 주문 시스템

이 예시에서는 온라인 쇼핑몰의 주문 처리 시스템을 구현합니다. 여러 Item의 개념을 적용하여 설계하고 구현하겠습니다.


### 동작 흐름 설명

1. 클라이언트가 `OrderService.placeOrder(order)`를 호출합니다.
2. `OrderService`는 먼저 `PaymentProcessor`를 통해 결제를 처리합니다.
3. 결제가 성공하면, `OrderProcessor`의 `processOrder` 메서드가 호출됩니다.
4. `AbstractOrderProcessor`의 `processOrder` 메서드가 실행되며, 이는 다음 단계를 순차적으로 수행합니다:
   - a. `validateOrder`: 주문의 유효성을 검사합니다.
   - b. `saveOrder`: 주문을 데이터베이스에 저장합니다.
   - c. `notifyCustomer`: 고객에게 주문 확인 알림을 보냅니다.
5. 각 단계는 `StandardOrderProcessor`에서 구체적으로 구현된 메서드를 실행합니다.
---

### 1. 주문 처리 인터페이스 (Item 20: 추상 클래스보다는 인터페이스를 우선하라)

```java
public interface OrderProcessor {
    void processOrder(Order order);
    void cancelOrder(String orderId);
    OrderStatus checkOrderStatus(String orderId);
}
```

이 인터페이스는 주문 처리의 핵심 기능을 정의합니다. 인터페이스를 사용함으로써 다양한 주문 처리 방식을 유연하게 구현할 수 있습니다.

### 2. 골격 구현 클래스 (Item 20: 추상 클래스보다는 인터페이스를 우선하라)

```java
public abstract class AbstractOrderProcessor implements OrderProcessor {
    @Override
    public void processOrder(Order order) {
        validateOrder(order);
        saveOrder(order);
        notifyCustomer(order);
    }

    protected abstract void validateOrder(Order order);
    protected abstract void saveOrder(Order order);
    protected abstract void notifyCustomer(Order order);

    // checkOrderStatus와 cancelOrder의 기본 구현 제공
}
```

이 추상 클래스는 OrderProcessor 인터페이스의 골격 구현을 제공합니다. 공통 로직을 구현하고, 특정 단계는 하위 클래스에서 구현하도록 합니다.

### 3. 구체적인 주문 처리기 (Item 19: 상속을 고려해 설계하고 문서화하라)

```java
public class StandardOrderProcessor extends AbstractOrderProcessor {
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public StandardOrderProcessor(OrderRepository orderRepository, NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    protected void validateOrder(Order order) {
        // 주문 유효성 검사 로직
    }

    @Override
    protected void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    protected void notifyCustomer(Order order) {
        notificationService.sendOrderConfirmation(order);
    }
}
```

StandardOrderProcessor는 AbstractOrderProcessor를 상속받아 구체적인 주문 처리 로직을 구현합니다. 이는 Item 19의 원칙을 따라 상속을 고려한 설계를 보여줍니다.

### 4. 불변 클래스로 주문 표현 (Item 17: 변경 가능성을 최소화하라)

```java
@Getter
public final class Order {
    private final String orderId;
    private final String customerId;
    private final List<OrderItem> items;
    private final LocalDateTime orderDate;

    private Order(Builder builder) {
        this.orderId = builder.orderId;
        this.customerId = builder.customerId;
        this.items = List.copyOf(builder.items);
        this.orderDate = builder.orderDate;
    }

    public static class Builder {
        private String orderId;
        private String customerId;
        private List<OrderItem> items = new ArrayList<>();
        private LocalDateTime orderDate;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Order build() {
            if (orderId == null || customerId == null || items.isEmpty() || orderDate == null) {
                throw new IllegalStateException("필수 필드가 모두 설정되어야 합니다.");
            }
            return new Order(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }
}
```

Order 클래스를 불변으로 설계하여 객체의 상태가 생성 후 변경되지 않도록 보장합니다. 이는 스레드 안전성을 제공하고 버그 발생 가능성을 줄입니다.

### 5. 주문 처리 서비스 (Item 18: 상속보다는 컴포지션을 사용하라)

```java
@Service
public class OrderService {
    private final OrderProcessor orderProcessor;
    private final PaymentProcessor paymentProcessor;

    public OrderService(OrderProcessor orderProcessor, PaymentProcessor paymentProcessor) {
        this.orderProcessor = orderProcessor;
        this.paymentProcessor = paymentProcessor;
    }

    public void placeOrder(Order order) {
        paymentProcessor.processPayment(order);
        orderProcessor.processOrder(order);
    }
}
```

OrderService는 OrderProcessor와 PaymentProcessor를 컴포지션으로 사용합니다. 이는 상속 대신 컴포지션을 사용하여 유연성을 높이고 결합도를 낮춥니다.

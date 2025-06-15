package ua.aleh1s.paymentservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ua.aleh1s.paymentservice.client.SubscriptionsClient;
import ua.aleh1s.paymentservice.dto.CreateSubscriptionInvoiceDto;
import ua.aleh1s.paymentservice.dto.InvoiceUrlDto;
import ua.aleh1s.paymentservice.dto.PaymentsAnalytics;
import ua.aleh1s.paymentservice.dto.PaymentsStatistics;
import ua.aleh1s.paymentservice.dto.SubscribeRequestDto;
import ua.aleh1s.paymentservice.dto.SubscriptionDto;
import ua.aleh1s.paymentservice.dto.SubscriptionPlanDto;
import ua.aleh1s.paymentservice.dto.SubscriptionType;
import ua.aleh1s.paymentservice.exceptions.PaymentServiceException;
import ua.aleh1s.paymentservice.jwt.ClaimsNames;
import ua.aleh1s.paymentservice.model.DateRange;
import ua.aleh1s.paymentservice.model.KeyValue;
import ua.aleh1s.paymentservice.model.Payment;
import ua.aleh1s.paymentservice.repository.PaymentRepository;
import ua.aleh1s.paymentservice.utils.CommonGenerator;
import ua.aleh1s.paymentservice.utils.DateUtils;
import ua.aleh1s.paymentservice.utils.MoneyUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.Month;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final String SUBSCRIBE_ON_ID_FIELD = "subscribeOnId";
    private static final String SUBSCRIBER_ID_FIELD = "subscriberId";

    private final SubscriptionsClient subscriptionsClient;
    private final CommonGenerator commonGenerator;
    private final PaymentRepository paymentRepository;
    private final MongoTemplate mongoTemplate;

    @Value("${frontend.url}")
    private String frontendUrl;

    public InvoiceUrlDto createSubscriptionInvoice(CreateSubscriptionInvoiceDto createSubscriptionInvoice) {
        String subscribeOnId = createSubscriptionInvoice.getSubscribeOnId();

        SubscriptionPlanDto subscriptionPlan = subscriptionsClient.getSubscriptionPlan(subscribeOnId);

        if (!subscriptionPlan.getType().equals(SubscriptionType.PREMIUM)) {
            throw new PaymentServiceException("This subscription does not require payment");
        }

        SessionCreateParams.LineItem.PriceData.Recurring recurring = SessionCreateParams.LineItem.PriceData.Recurring.builder()
                .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                .build();

        SessionCreateParams.LineItem.PriceData.ProductData productData = SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName("Monthly Support for creator")
                .build();

        Long unitPrice = MoneyUtils.dollarsToCents(subscriptionPlan.getPrice());

        SessionCreateParams.LineItem.PriceData priceData = SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount(unitPrice)
                .setRecurring(recurring)
                .setProductData(productData)
                .build();

        SessionCreateParams.LineItem item = SessionCreateParams.LineItem.builder()
                .setQuantity(1L)
                .setPriceData(priceData)
                .build();

        String successUrl = "%s/subscriptions-success?session_id={CHECKOUT_SESSION_ID}".formatted(frontendUrl);
        String cancelUrl = "%s/profile/%s".formatted(frontendUrl, subscribeOnId);

        Jwt jwt = (Jwt) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String subscriberId = jwt.getClaimAsString(ClaimsNames.SUBJECT);

        Map<String, String> metadata = new HashMap<>();
        metadata.put(SUBSCRIBER_ID_FIELD, subscriberId);
        metadata.put(SUBSCRIBE_ON_ID_FIELD, subscribeOnId);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .putAllMetadata(metadata)
                .addLineItem(item)
                .build();

        try {
            Session session = Session.create(params);

            return InvoiceUrlDto.builder()
                    .invoiceUrl(session.getUrl())
                    .build();
        } catch (StripeException e) {
            throw new PaymentServiceException("Error while creating invoice", e);
        }
    }

    public SubscriptionDto handleSuccessSubscriptionPayment(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            if (!session.getPaymentStatus().equals("paid")) {
                throw new PaymentServiceException("Payment failed. Please try again");
            }

            if (paymentRepository.existsBySessionId(sessionId)) {
                throw new PaymentServiceException("Payment already handled exists");
            }

            Map<String, String> metadata = session.getMetadata();
            String subscriberId = metadata.get(SUBSCRIBER_ID_FIELD);
            String subscribeOnId = metadata.get(SUBSCRIBE_ON_ID_FIELD);

            Payment payment = Payment.builder()
                    .id(commonGenerator.uuid())
                    .amount(session.getAmountTotal())
                    .sessionId(sessionId)
                    .subscriberId(subscriberId)
                    .subscribedOnId(subscribeOnId)
                    .createdAt(commonGenerator.now())
                    .build();

            paymentRepository.save(payment);

            SubscribeRequestDto subscribeRequest = SubscribeRequestDto.builder()
                    .subscribeOnId(subscribeOnId)
                    .build();

            return subscriptionsClient.subscribe(subscribeRequest);
        } catch (StripeException e) {
            throw new PaymentServiceException("Error while retrieving session", e);
        }
    }

    public PaymentsStatistics getPaymentsStatistics(String subscribedOnId) {
        DateRange thisMonth = DateUtils.getThisMonthDateRange();
        DateRange prevMonth = DateUtils.getLastMonthDateRange();

        Long thisMonthTotalAmount = getTotalAmountBySubscribedOnIdAndCreatedAtBetween(
                subscribedOnId, thisMonth.from(), thisMonth.to());
        Long prevMonthTotalAmount = getTotalAmountBySubscribedOnIdAndCreatedAtBetween(
                subscribedOnId, prevMonth.from(), prevMonth.to());
        List<KeyValue<Month, BigDecimal>> totalPaymentsAmountByDate = getTotalPaymentsAmountByMonth(
                subscribedOnId, thisMonth.from(), thisMonth.to());

        BigDecimal revenueGrowthPercentage;
        if (prevMonthTotalAmount == 0) {
            revenueGrowthPercentage = BigDecimal.ZERO;
        } else {
            revenueGrowthPercentage = BigDecimal.valueOf(thisMonthTotalAmount - prevMonthTotalAmount)
                    .divide(BigDecimal.valueOf(prevMonthTotalAmount), 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(1, RoundingMode.HALF_UP);
        }

        return PaymentsStatistics.builder()
                .monthRevenue(MoneyUtils.centsToDollars(thisMonthTotalAmount))
                .monthlyRevenueGrowthPercentage(revenueGrowthPercentage)
                .monthlyRevenueStatistics(totalPaymentsAmountByDate)
                .build();
    }

    private Long getTotalAmountBySubscribedOnIdAndCreatedAtBetween(String subscribedOnId, Instant from, Instant to) {
        Criteria criteria = Criteria
                .where(Payment.Fields.subscribedOnId)
                .is(subscribedOnId)
                .and(Payment.Fields.createdAt)
                .gte(from)
                .lt(to);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group().sum(Payment.Fields.amount).as("totalAmount")
        );

        AggregationResults<Document> result = mongoTemplate.aggregate(
                aggregation, Payment.COLLECTION_NAME, Document.class);

        Document sum = result.getUniqueMappedResult();
        return sum != null ? sum.getLong("totalAmount") : 0;
    }

    public PaymentsAnalytics getPaymentsAnalytics(String subscribedOnId, Instant from, Instant to) {
        List<KeyValue<Instant, BigDecimal>> totalPaymentsAmountByDate = getTotalPaymentsAmountByDate(subscribedOnId, from, to);

        return PaymentsAnalytics.builder()
                .amountsSumByDate(totalPaymentsAmountByDate)
                .build();
    }

    private List<KeyValue<Instant, BigDecimal>> getTotalPaymentsAmountByDate(String subscribedOnId, Instant from, Instant to) {
        Map<Instant, Long> paymentsAmountSumByDate = new HashMap<>();

        ZoneId zoneId = ZoneId.systemDefault();
        paymentRepository.findAllBySubscribedOnIdAndCreatedAtBetween(subscribedOnId, from, to).forEach(payment -> {
            Instant date = payment.getCreatedAt()
                    .atZone(zoneId)
                    .toLocalDate()
                    .atStartOfDay(zoneId)
                    .toInstant();

            paymentsAmountSumByDate.compute(date, (k, v) -> v == null
                    ? payment.getAmount()
                    : v + payment.getAmount());
        });

        return paymentsAmountSumByDate.entrySet().stream()
                .map(e -> new KeyValue<>(e.getKey(), MoneyUtils.centsToDollars(e.getValue())))
                .sorted(Comparator.comparing(KeyValue::key))
                .toList();
    }

    private List<KeyValue<Month, BigDecimal>> getTotalPaymentsAmountByMonth(String subscribedOnId, Instant from, Instant to) {
        Map<Month, Long> paymentsAmountSumByMonth = new HashMap<>();

        paymentRepository.findAllBySubscribedOnIdAndCreatedAtBetween(subscribedOnId, from, to).forEach(payment -> {
            Month month = payment.getCreatedAt()
                    .atZone(ZoneId.systemDefault())
                    .getMonth();

            paymentsAmountSumByMonth.compute(month, (k, v) -> v == null
                    ? payment.getAmount()
                    : v + payment.getAmount());
        });

        return paymentsAmountSumByMonth.entrySet().stream()
                .map(e -> new KeyValue<>(e.getKey(), MoneyUtils.centsToDollars(e.getValue())))
                .sorted(Comparator.comparing(KeyValue::key))
                .toList();
    }
}

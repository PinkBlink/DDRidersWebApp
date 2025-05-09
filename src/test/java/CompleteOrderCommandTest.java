import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.riders.sharing.command.Command;
import org.riders.sharing.command.CompleteOrderCommand;
import org.riders.sharing.connection.ConnectionPool;
import org.riders.sharing.dto.OrderDto;
import org.riders.sharing.repository.CustomerRepository;
import org.riders.sharing.repository.OrderRepository;
import org.riders.sharing.repository.ScooterRepository;
import org.riders.sharing.repository.impl.CustomerRepositoryImpl;
import org.riders.sharing.repository.impl.OrderRepositoryImpl;
import org.riders.sharing.repository.impl.ScooterRepositoryImpl;
import org.riders.sharing.service.CustomerService;
import org.riders.sharing.service.OrderService;
import org.riders.sharing.service.ScooterService;
import org.riders.sharing.service.impl.CustomerServiceImpl;
import org.riders.sharing.service.impl.OrderServiceImpl;
import org.riders.sharing.service.impl.ScooterServiceImpl;
import org.riders.sharing.utils.ModelMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_CONFLICT;
import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.riders.sharing.model.enums.OrderStatus.COMPLETED;
import static org.riders.sharing.model.enums.ScooterStatus.AVAILABLE;
import static org.riders.sharing.model.enums.ScooterStatus.RENTED;

public class CompleteOrderCommandTest extends BaseTest implements OrderTestData, CustomerTestData, ScooterTestData {
    private final CustomerRepository customerRepository = new CustomerRepositoryImpl(ConnectionPool.INSTANCE);
    private final ScooterRepository scooterRepository = new ScooterRepositoryImpl(ConnectionPool.INSTANCE);
    private final OrderRepository orderRepository = new OrderRepositoryImpl(ConnectionPool.INSTANCE);

    private final CustomerService customerService = new CustomerServiceImpl(customerRepository);
    private final ScooterService scooterService = new ScooterServiceImpl(scooterRepository);
    private final OrderService orderService = new OrderServiceImpl(customerService, scooterService, orderRepository);

    private final Command completeOrderCommand = new CompleteOrderCommand(orderService);

    @Test
    public void completeRespondsWith200AndOrder() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedScooter = scooterRepository.save(
            aScooter()
                .status(RENTED)
                .build()
        );

        final var savedOrder = orderRepository.save(
            anOrder()
                .customerId(savedCustomer.getId())
                .scooter(savedScooter)
                .build()
        );

        final var savedOrderDto = OrderDto.fromOrder(savedOrder);
        final var savedOrderDtoJson = ModelMapper.toJsonString(savedOrderDto);

        final var stringReader = new StringReader(savedOrderDtoJson);
        final var requestReader = new BufferedReader(stringReader);

        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedResponseStatus = SC_OK;
        final var expectedOrderStatus = COMPLETED;
        final var expectedScooterStatus = AVAILABLE;

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        completeOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);

        final var orderDtoFromResponse = ModelMapper.parse(stringWriter.toString(), OrderDto.class);
        final var scooterFromDb = scooterService.getById(orderDtoFromResponse.scooterId());

        Assertions.assertEquals(savedOrder.getId(), orderDtoFromResponse.orderId());
        Assertions.assertEquals(expectedOrderStatus, orderDtoFromResponse.status());
        Assertions.assertEquals(expectedScooterStatus, scooterFromDb.getStatus());
    }

    @Test
    public void completeRespondsWith400() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var stringReader = new StringReader("");
        final var requestReader = new BufferedReader(stringReader);

        final var expectedResponseStatus = SC_BAD_REQUEST;

        when(request.getReader()).thenReturn(requestReader);

        //when
        completeOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void completeRespondsWith401() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedScooter = scooterRepository.save(
            aScooter()
                .status(AVAILABLE)
                .build()
        );

        final var savedOrder = orderRepository.save(
            anOrder()
                .customerId(savedCustomer.getId())
                .scooter(savedScooter)
                .status(COMPLETED)
                .build()
        );

        final var savedOrderDto = OrderDto.fromOrder(savedOrder);
        final var savedOrderDtoJson = ModelMapper.toJsonString(savedOrderDto);

        final var stringReader = new StringReader(savedOrderDtoJson);
        final var requestReader = new BufferedReader(stringReader);

        final var stringWriter = new StringWriter();
        final var responseWriter = new PrintWriter(stringWriter);

        final var expectedResponseStatus = SC_CONFLICT;

        when(request.getReader()).thenReturn(requestReader);
        when(response.getWriter()).thenReturn(responseWriter);

        //when
        completeOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void completeRespondsWith404() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var savedCustomer = customerRepository.save(aCustomer().build());
        final var savedScooter = scooterRepository.save(
            aScooter()
                .status(AVAILABLE)
                .build()
        );

        final var unsavedOrder = anOrder()
            .customerId(savedCustomer.getId())
            .scooter(savedScooter)
            .build();

        final var unsavedOrderDto = OrderDto.fromOrder(unsavedOrder);
        final var unsavedOrderDtoJson = ModelMapper.toJsonString(unsavedOrderDto);

        final var stringReader = new StringReader(unsavedOrderDtoJson);
        final var requestReader = new BufferedReader(stringReader);

        final var expectedResponseStatus = SC_NOT_FOUND;

        when(request.getReader()).thenReturn(requestReader);

        //when
        completeOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }

    @Test
    public void completeRespondsWith500() throws IOException {
        //given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);

        final var expectedResponseStatus = SC_INTERNAL_SERVER_ERROR;

        when(request.getReader()).thenThrow(RuntimeException.class);

        //when
        completeOrderCommand.execute(request, response);

        //then
        verify(response).setStatus(expectedResponseStatus);
    }
}

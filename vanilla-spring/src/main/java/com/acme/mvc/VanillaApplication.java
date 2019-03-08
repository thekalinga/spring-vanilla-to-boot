package com.acme.mvc;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.hibernate.cfg.AvailableSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static java.util.Arrays.asList;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.GenerationType.IDENTITY;
import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

public class VanillaApplication {
  private static final int PORT = 8080;

  public static void main(String[] args) throws LifecycleException {
    String appBase = ".";
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(createTempDir());
    tomcat.setPort(PORT);
    tomcat.getHost().setAppBase(appBase);
    tomcat.addWebapp("", ".");
    tomcat.getConnector(); // Trigger the creation of the default connector
    tomcat.start();
    tomcat.getServer().await();
  }

  private static Context findContext(Tomcat tomcat) {
    for (Container child : tomcat.getHost().findChildren()) {
      if (child instanceof Context) {
        return (Context) child;
      }
    }
    throw new IllegalStateException("The host does not contain a Context");
  }

  // based on AbstractEmbeddedServletContainerFactory
  private static String createTempDir() {
    try {
      File tempDir = File.createTempFile("tomcat.", "." + PORT);
      tempDir.delete();
      tempDir.mkdir();
      tempDir.deleteOnExit();
      return tempDir.getAbsolutePath();
    } catch (IOException ex) {
      throw new RuntimeException(
          "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"),
          ex
      );
    }
  }
}

class ShopWebApplicationInitializer implements WebApplicationInitializer {
  @Override
  public void onStartup(ServletContext servletContext) {
    // Create the 'root' Spring application context
    AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
    rootContext.register(RootConfiguration.class);
    rootContext.setClassLoader(getClass().getClassLoader()); // Refer to this on why this is necessary. https://stackoverflow.com/a/54754744/211794

    // Manage the lifecycle of the root application context
    servletContext.addListener(new ContextLoaderListener(rootContext));

    // Create the dispatcher servlet's Spring application context
    AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
    dispatcherContext.register(CustomWebApplicationContext.class);
    dispatcherContext.setServletContext(servletContext);

    // Register and map the dispatcher servlet
    ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
  }
}

@Configuration
class InitializerConfiguration {
  @Bean
  InitializingBean dbInit(ProductRepository productRepository, OrderRepository orderRepository) {
    return () -> {
      Product productA = Product.builder().name("Product A").code("A").description("Very popular product").priceInInr(10).quantity(1).build();
      Product productB = Product.builder().name("Product B").code("B").description("Niche product").priceInInr(1000).quantity(9).build();
      Product productC = Product.builder().name("Product C").code("C").priceInInr(100).quantity(10).build();
      asList(productA, productB, productC).forEach(productRepository::save);
      LineItem lineItem1 = LineItem.builder().product(productA).quantity(9).build();
      LineItem lineItem2 = LineItem.builder().product(productB).quantity(1).build();
      int priceInInr = productA.getPriceInInr() * lineItem1.getQuantity() + productB.getPriceInInr() * lineItem2.getQuantity();
      Order order =
          Order.builder().lineItems(asList(lineItem1, lineItem2)).createdAt(LocalDateTime.now())
              .priceInInr(priceInInr).quantity(lineItem1.getQuantity() + lineItem2.getQuantity())
              .build();
      order.updateLineItemReferences();
      orderRepository.save(order);
    };
  }
}

@Configuration
@EnableWebMvc
@Import({ProductResource.class, OrderResource.class})
class CustomWebApplicationContext implements WebMvcConfigurer {
  @Bean
  ObjectMapper objectMapper() {
    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
    builder.serializationInclusion(NON_NULL);
    return builder.build();
  }
}


@RestController
@RequestMapping(value = "/products", produces = APPLICATION_JSON_UTF8_VALUE)
class ProductResource {
  private final ProductRepository repository;
  private final ProductMapper mapper;

  public ProductResource(ProductRepository repository, ProductMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @GetMapping
  ResponseEntity<Stream<ProductSummaryResponse>> summaries() {
    return ResponseEntity.ok().body(repository.findAll().stream().map(mapper::toSummary));
  }

  @GetMapping("/{code}")
  ResponseEntity<ProductDetailResponse> byCode(@PathVariable String code) {
    return repository.findByCode(code)
        .map(product -> ResponseEntity.ok().body(mapper.toDetail(product)))
        .orElse(ResponseEntity.notFound().build());
  }
}


@RestController
@RequestMapping(value = "/orders", produces = APPLICATION_JSON_UTF8_VALUE)
class OrderResource {
  private final OrderService service;

  OrderResource(OrderService service) {
    this.service = service;
  }

  @GetMapping
  ResponseEntity<Stream<OrderSummaryResponse>> summaries() {
    return ResponseEntity.ok().body(service.summaries());
  }

  @GetMapping("/{id}")
  ResponseEntity<OrderDetailResponse> byCode(@PathVariable int id) {
    return service.byCode(id)
        .map(summary -> ResponseEntity.ok().body(summary))
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE)
  ResponseEntity<?> save(@Valid @RequestBody OrderRequest request, UriComponentsBuilder componentsBuilder) {
    try {
      Order order = service.saveOrder(request);
      return ResponseEntity.created(componentsBuilder.path("/orders/{id}").buildAndExpand(order.getId()).toUri())
          .build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}

@Configuration
@Import({OrderServiceImpl.class, PersistenceConfiguration.class, OrderMapperImpl.class, ProductMapperImpl.class, InitializerConfiguration.class})
class RootConfiguration {
}

interface OrderService {
  Order saveOrder(OrderRequest request);
  Stream<OrderSummaryResponse> summaries();
  Optional<OrderDetailResponse> byCode(int id);
}

@Service
@Transactional(readOnly = true)
class OrderServiceImpl implements OrderService {

  private final OrderRepository repository;
  private final OrderMapper mapper;
  private final ProductRepository productRepository;

  public OrderServiceImpl(OrderRepository repository, OrderMapper mapper, ProductRepository productRepository) {
    this.repository = repository;
    this.mapper = mapper;
    this.productRepository = productRepository;
  }

  @Override
  @Transactional
  public Order saveOrder(OrderRequest request) {
    Order.OrderBuilder builder = Order.builder();
    List<LineItem> lineItems = new ArrayList<>();
    for (LineItemRequest itemRequest : request.getLineItems()) {
      Product product = productRepository.findByCode(itemRequest.getProductCode())
          .orElseThrow(() -> new IllegalArgumentException("Invalid product code " + itemRequest.getProductCode() + " specified"));
      if (product.getQuantity() < itemRequest.getQuantity()) {
        throw new IllegalArgumentException("Requested more quantity for product: " + product.getName() + ". Available: " +product
            .getQuantity() + "; Requested: " + itemRequest.getQuantity());
      }
      LineItem lineItem = LineItem.builder().product(product).quantity(itemRequest.getQuantity()).build();
      product.reduceQuantityBy(lineItem.getQuantity());
      lineItems.add(lineItem);
      productRepository.save(product);
    }
    Order order = builder.build();
    order.addLineItems(lineItems);
    return repository.save(order);
  }

  @Override
  public Stream<OrderSummaryResponse> summaries() {
    return repository.findAll().stream().map(mapper::toSummary);
  }

  @Override
  public Optional<OrderDetailResponse> byCode(int id) {
    return repository.eagerFindById(id)
        .map(mapper::toDetail);
  }

}


@Mapper(injectionStrategy = CONSTRUCTOR)
interface ProductMapper {
  ProductSummaryResponse toSummary(Product product);
  ProductDetailResponse toDetail(Product product);
}


@Mapper(injectionStrategy = CONSTRUCTOR)
interface OrderMapper {
  OrderSummaryResponse toSummary(Order order);
  OrderDetailResponse toDetail(Order order);
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "productCode", source = "product.code")
  LineItemResponse toResponse(LineItem lineItem);
}


@Getter
@Builder
@AllArgsConstructor
@ToString
class ProductSummaryResponse {
  private int id;
  private String name;
  private String code;
  private String description;
}


@Getter
@Builder
@AllArgsConstructor
@ToString
class ProductDetailResponse {
  private int id;
  private String name;
  private String code;
  private String description;
  private int quantity;
  private int priceInInr;
}

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
class OrderRequest {
  @Valid
  @Size(min = 1)
  private List<LineItemRequest> lineItems;
}


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
class LineItemRequest {
  @NotNull
  @Size(min = 1)
  private String productCode;
  @Min(1)
  private int quantity;
}

@Getter
@Builder
@AllArgsConstructor
class OrderSummaryResponse {
  private int id;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime createdAt;
  private int quantity;
  private int priceInInr;
}


@Getter
@Builder
@AllArgsConstructor
class OrderDetailResponse {
  private int id;
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
  private LocalDateTime createdAt;
  private List<LineItemResponse> lineItems;
}


@Getter
@Builder
@AllArgsConstructor
class LineItemResponse {
  private String productName;
  private String productCode;
  private int quantity;
}


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
class PersistenceConfiguration {
  @Bean
  DataSource dataSource() {
    return new EmbeddedDatabaseBuilder()
//        .addScripts("schema.sql", "data.sql")
        .setType(EmbeddedDatabaseType.H2)
        .build();
  }

  @Bean
  LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setPackagesToScan(Product.class.getPackage().getName());
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Map<String, Object> jpaHibernateProperties = new HashMap<>();
    jpaHibernateProperties.put(AvailableSettings.SHOW_SQL, true);
    jpaHibernateProperties.put(AvailableSettings.FORMAT_SQL, true);
    jpaHibernateProperties.put(AvailableSettings.HBM2DDL_AUTO, "create-drop");
    jpaHibernateProperties.put(AvailableSettings.PHYSICAL_NAMING_STRATEGY, TablePrefixedSpringPhysicalNamingStrategy.class);
    entityManagerFactoryBean.setJpaPropertyMap(jpaHibernateProperties);

    return entityManagerFactoryBean;
  }

  @Bean
  PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    return new JpaTransactionManager(entityManagerFactory);
  }
}


interface ProductRepository extends JpaRepository<Product, Integer> {
  Optional<Product> findByCode(String code);
}


interface OrderRepository extends JpaRepository<Order, Integer> {
  @Query("from Order o inner join fetch o.lineItems l inner join fetch l.product where o.id = :id")
  Optional<Order> eagerFindById(@Param("id") int id);
}


interface LineItemRepository extends JpaRepository<LineItem, Integer> {
}


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
@Table(indexes = {@Index(columnList = "code", unique = true)})
class Product {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Version
  private int version;
  @NotNull
  @Size(min = 1)
  private String name;
  private String description;
  @NotNull
  @Column(unique = true)
  private String code;
  @Min(0)
  private int quantity;
  @Min(1)
  private int priceInInr;

  void reduceQuantityBy(int quantity) {
    this.quantity -= quantity;
  }
}


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
class Order {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Min(1)
  private int priceInInr;
  @Min(1)
  private int quantity;
  @Builder.Default
  @NotNull
  private LocalDateTime createdAt = LocalDateTime.now();

  @Builder.Default
  @OneToMany(mappedBy = "order", cascade = ALL)
  private List<LineItem> lineItems = new ArrayList<>();

  void addLineItem(LineItem lineItem) {
    lineItems.add(lineItem);
    quantity += lineItem.getQuantity();
    priceInInr += lineItem.getProduct().getPriceInInr() * lineItem.getQuantity();
    lineItem.setOrder(this);
  }

  void updateLineItemReferences() {
    lineItems.forEach(lineItem -> lineItem.setOrder(this));
  }

  void addLineItems(List<LineItem> lineItems) {
    lineItems.forEach(this::addLineItem);
  }
}

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
class LineItem {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private int id;
  @Min(1)
  private int quantity;

  @ManyToOne(optional = false)
  @JoinColumn
  private Order order;

  @ManyToOne(optional = false)
  @JoinColumn
  private Product product;

  void setOrder(Order order) {
    this.order = order;
  }
}

insert into Product_tab
  (code, description, name, quantity, priceInInr, version)
values
  ('A', 'com.acme.boot.mvc.Product A', 10, 100, 1)
  ('B', 'com.acme.boot.mvc.Product B', 20, 200, 1)
  ('C', 'com.acme.boot.mvc.Product C', 30, 300, 1);

insert into Order_tab
  (createdAt, quantity, priceInInr)
values
  (, 'com.acme.boot.mvc.Product A', 10, 100, 1)
  ('B', 'com.acme.boot.mvc.Product B', 20, 200, 1)
  ('C', 'com.acme.boot.mvc.Product C', 30, 300, 1);

insert into LineItem_tab
  (quantity, order_id, product_id)
values
  ('A', 'com.acme.boot.mvc.Product A', 10, 100, 1)
  ('B', 'com.acme.boot.mvc.Product B', 20, 200, 1)
  ('C', 'com.acme.boot.mvc.Product C', 30, 300, 1);

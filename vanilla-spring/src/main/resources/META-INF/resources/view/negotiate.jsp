<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
       <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
       <title>Welcome to JSTL foreach tag Example in JSP</title>
    </head>

    <body>
        <h2>Products</h2>
        <c:forEach var="product" items="${productList}">
            <p><c:out value="${product.name}"/></p>
        </c:forEach>
    </body>
</html>

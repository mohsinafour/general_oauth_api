<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<script type="text/javascript">
	function foo()
	{
		var x = window.location.hash;
		window.location = "http://localhost:8888/general/general_oauth_redirected?"+x.slice(1);;
	}
</script>
</head>
<body onload="foo()">
</body>
</html>
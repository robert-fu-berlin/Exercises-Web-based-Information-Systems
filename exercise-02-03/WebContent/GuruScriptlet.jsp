<%@ page import="guruPackage.Guru" %>
<html>
	<head>
		<title>The Guru</title>
	</head>
	<body bgcolor=white>
		<h1><font color=red>Today`s advice from the Guru</font></h1>
		<% Guru theGuru = new Guru(); %>
		<p>
		<%= theGuru.getEnlightenment() %>
	</body>
</html>
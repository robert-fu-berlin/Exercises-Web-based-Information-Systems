<%@ page import="guruPackage.Guru" %>
<html>
	<head>
		<title>The Guru</title>
	</head>
	<body bgcolor=white>
		<h1><font color=red>Today`s advice from the Guru</font></h1>
		<p>
		<jsp:useBean id="theGuru" class="guruPackage.Guru" />
		<jsp:getProperty name="theGuru" property="enlightenment" />
	</body>
</html>
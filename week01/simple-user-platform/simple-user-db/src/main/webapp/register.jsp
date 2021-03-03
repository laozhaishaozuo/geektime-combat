<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<head>
<jsp:directive.include
	file="/WEB-INF/jsp/prelude/include-head-meta.jspf" />
<title>My Home Page</title>
<style>
.bd-placeholder-img {
	font-size: 1.125rem;
	text-anchor: middle;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
}

@media ( min-width : 768px) {
	.bd-placeholder-img-lg {
		font-size: 3.5rem;
	}
}
</style>
</head>
<body>
	<div class="container">
		<form action="${ctx }/user/success">
			<div class="form-group">
				<label for="inputName">用户名</label> <input
					type="text" class="form-control" id="inputName" name="name"
					aria-describedby="emailHelp">
			</div>
			<div class="form-group">
				<label for="inputEmail">电子邮箱</label> <input
					type="email" class="form-control" id="inputEmail" name="email"
					aria-describedby="emailHelp"> <small id="emailHelp"
					class="form-text text-muted"></small>
			</div>
			<div class="form-group">
				<label for="inputPassword">密码</label> <input name="password"
					type="password" class="form-control" id="inputPassword">
			</div>
			<div class="form-group">
				<label for="inputPhoneNumber">手机号</label> <input name="phoneNumber"
					type="text" class="form-control" id="inputPhoneNumber">
			</div>
			<button type="submit" class="btn btn-primary">Submit</button>
		</form>
	</div>
</body>
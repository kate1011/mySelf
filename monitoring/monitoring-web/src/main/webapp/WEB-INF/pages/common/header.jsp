<%@ page contentType="text/html;charset=UTF-8" language="java"%>

<jsp:include page="common.jsp"></jsp:include>
<div ng-controller="headContro" style="background-color: #222;">
	<div class="container-fluid">
		<div style="float: left; width: 100%;height:100px;">
			<table style="width: 100%; margin-top: 20px;">
				<tr>
					<td ><a class="navbar-brand" href="#">${userName},欢迎您!</a>
						<a class="navbar-brand" ng-click="exit()">退出</a></td>
					<td ><p
							style="color: #FFFFF0; font-size: 40px;float:right;">服务监控平台&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p></td>
				</tr>
			</table>

		</div>



	</div>
</div>
<script type="text/javascript">
	App.controller('headContro', function($scope, $http, factorys) {

		$scope.exit = function() {
			factorys.commethod("/userLogout");
			window.location.href = "/";
		}

	});
</script>

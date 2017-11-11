<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file = "common/tag.jsp" %><!-- 其实是jstl及fmt的库，但是把他们都抽象到一个jsp中 -->
<!DOCTYPE html>
<html>
   <head>
      <title>秒杀列表页</title>
      <!-- 静态包含，就相当于把代码放在这里面，最后作为整体jsp编译成servlet -->
      <!-- 动态包含则是先编译这个被包含的文件运行，在编译这个list.jsp最后才结合输出 -->
      <%@include file = "common/head.jsp" %>
   </head>
   <body>
		<!-- 页面显示部分 -->
		<div class="container">
			<div class="panel panel-default">
				<div class="panel-heading text-center">
					<h2>商品列表</h2>				
				</div>	
				<div class="panel-body">
					<table class="table table-hover">
						<thead>
							<tr>
								<th>名称</th>
								<th>库存量</th>
								<th>开始秒杀时间</th>
								<th>结束秒杀时间</th>
								<th>创建时间</th>
								<th>详情</th>
							</tr>
							<tbody>
								<c:forEach var="sk" items="${list}">
									<tr>
										<td>${sk.name}</td>
										<td>${sk.number }</td>
										<td>
											<fmt:formatDate value="${sk.startTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<fmt:formatDate value="${sk.endTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<fmt:formatDate value="${sk.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/>
										</td>
										<td>
											<a class="btn btn-info" href="/seckill/${sk.seckillId}/detail" target="_blank">详情</a>
										</td>
									</tr>								
								</c:forEach>
							</tbody>						
						</thead>
					</table>
				</div>		
			</div>
		</div>
   </body>
    <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>		 
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
</html>
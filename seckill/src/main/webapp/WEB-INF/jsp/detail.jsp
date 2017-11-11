<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
   <head>
      <title>秒杀详情页</title>
      <!-- 静态包含，就相当于把代码放在这里面，最后作为整体jsp编译成servlet -->
      <!-- 动态包含则是先编译这个被包含的文件运行，在编译这个list.jsp最后才结合输出 -->
      <%@include file = "common/head.jsp" %>
   </head>
   <body>
		<div class="container">
			<div class="panel panel-default text-center">
				<div class="panerl-heading">${seckill.name }</div>
				<div class="panel-body">
					<h2 class="text-danger">
						<!-- 显示time图标 -->
						<span class="glyphicon glyphicon-time"></span>
						<!-- 显示倒计时 -->
						<span class="glyphicon" id="seckill-box"></span>
					</h2>
				</div>
			</div>
		</div>
		<!-- bootstrap的一个组件 -->
		<div id="phoneModel" class="modal fade">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h3 class="modal-title text-center">
							<span class="glyphicon glyphicon-phone"></span>用户手机
						</h3>
					</div>				
					<div class="modal-body">
						<div class="row">
							<div class="col-xs-8 col-xs-offset-2">
								<input type="text" name="userPhone" id="userPhoneKey"
								 placeholder="请填写手机号" class="form-control">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<!-- 验证信息 -->
						<span id="userPhoneMessage" class="glyphicon"></span>
						<button type="button" id="userPhoneBtn" class="btn btn-success">
							<span class="glyphicon glyphicon-phone"></span>
							提交
						</button>	
					</div>
				</div>
			</div>
		</div>
   </body>
   <!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
	<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>		 
	<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
	<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
	<!-- jQuery cookie操作插件 -->
	<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
	<!-- jQuery提供的countDown倒计时插件 -->
	<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
	<!-- 开始编写交互逻辑 -->
	<script type="text/javascript">
		var seckill={
			//封装秒杀相关ajax的url	
			URL:{
				now : function(){
					return '/seckill/time/now';
				},
				exposer : function(seckillId){
					return '/seckill/' + seckillId + '/export';
				},
				execution : function(seckillId,md5){
					return '/seckill/' + seckillId + '/' + md5 + '/execution';
				}
			},
			//处理秒杀逻辑
			handleSeckill : function(seckillId,node){
				//获取库存地址，控制显示逻辑，用户可以点击执行秒杀按钮
				//隐藏的按钮！
				node.hide()
					.html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
				$.post(seckill.URL.exposer(seckillId),{},function(result){
					//在回调函数中执行交互流程
					if(result && result['success']){		
						var exposer = result['data'];
						if(exposer['exposed']){
							//开启了秒杀
							var md5 =exposer['md5'];
							var killUrl = seckill.URL.execution(seckillId,md5);
							console.log('killUrl:'+killUrl);
							//one和click的区别 :one 只绑定一次事件							
							$('#killBtn').one('click',function(){
								//执行秒杀
								//1.禁用按钮
								$(this).addClass('disabled');
								//2.发送秒杀请求
								$.post(killUrl,{},function(result){
									if(result && result['success']){
										var killResult = result['data'];
										var state = killResult['state'];
										var stateInfo = killResult['stateInfo'];
										//显示秒杀结果
										node.html('<span class="label label-success">'+stateInfo+'</span>');
									
									}
								});
							});
							node.show();		
						}else{
							//未开启秒杀,
							var now = exposer['now'];
							var start = exposer['start'];
							var end =exposer['end'];
							seckill.countdown(seckillId,now,start,end);
						}
					}else{
						consle.log('result:'+result);
					};
				});
			},
			//验证手机号
			validatePhone : function(phone){
				if(phone && phone.length == 11 && !isNaN(phone)){
					return true;
				}else{
					return false;
				}
			},
			countdown : function(seckillId,nowTime,startTime,endTime){
				//时间判断
				var seckillBox = $('#seckill-box');
				if(nowTime>endTime){
					seckillBox.html('秒杀结束');
				}else if(nowTime < startTime){
					//秒杀未开始,计时事件绑定
					var killTime = new Date(startTime + 1000);
					
					seckillBox.countdown(killTime,function(event){
						//控制时间的格式
						var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
						seckillBox.html(format);
					}).on('finish.countdown',function(){
						//到达时间了之后的回调事件
						//获取秒杀地址，控制显示逻辑，执行秒杀
						seckill.handleSeckill(seckillId,seckillBox);
					});
				}else{
					seckill.handleSeckill(seckillId,seckillBox);
				}
			},
			//详情页秒杀逻辑
			detail:{
				//详情页初始化
				init : function(params){
					//手机验证和登陆，计时交互
					//规划交互流程
					//在Cookie中查找手机号
					var userPhone = $.cookie('userPhone');
					if(!seckill.validatePhone(userPhone)){
						//请先登陆
						//控制输出
						var userPhoneModel = $('#phoneModel');//拿到bootstrap的组件
						//调用这个组件的方法，传递json参数
						userPhoneModel.modal({
							show : true, //显示弹出层
							backdrop : 'static',//禁止位置关闭
							keyboard : false //关闭键盘事件
						});
						$('#userPhoneBtn').click(function(){
							var inputPhone = $('#userPhoneKey').val();
							console.log('inputPhone='+inputPhone);//TODO
							if(seckill.validatePhone(inputPhone)){
								//将用户手机写入到cookie
								$.cookie('userPhone',inputPhone,{expires:7,path:'/seckill'});
								window.location.reload();
							}else{
								$('#userPhoneMessage').hide().html('<label class="label label-danger">手机填写有误</label>').show(300);
							}
						});
					}
					//已经登陆
					//计时交互
					var startTime = params['startTime'];
					var endTime = params['endTime'];
					var seckillId = params['seckillId'];
					$.get(seckill.URL.now(),{},function(result){
						if(result && result['success']){
							var nowTime = result['data'];
							console.log(nowTime);
							//时间交互
							seckill.countdown(seckillId,nowTime,startTime,endTime);
						}else{
							console.log('result:'+result);
						}
					});
				}
			}
		};
	
		//使用EL表达式传入参数
		$(function(){
			seckill.detail.init({
				seckillId : ${seckill.seckillId},
				startTime : ${seckill.startTime.time},//毫秒，方便js作解析
				endTime : ${seckill.endTime.time}
			});
		});
	</script>
</html>
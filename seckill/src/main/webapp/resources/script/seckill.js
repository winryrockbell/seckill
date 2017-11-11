//存放主要交互逻辑js代码
// javascript 模块化
//seckill是一个json对象
var seckill={
	//封装秒杀相关ajax的url	
	URL:{
		
	},
	//验证手机号
	validatePhone : function(phone){
		if(phone && phone.length == 11 && isNaN(phone)){
			return true;
		}else{
			return false;
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
			var startTime = params['startTime'];
			var endTime = params['endTime'];
			var seckillId = params['seckillId'];
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
					var inputPhone = $('userPhoneKey').val();
					if(seckill.validatePhone(inputPhone)){
						//将用户手机写入到cookie
						$.cookie('userPhone',inputPhone,{expires:7,path:'/seckill'});
						window.location.reload();
					}else{
						$('#userPhoneMessage').hide().html('<label class="label label-danger">手机填写有误</label>').show(300);
					}
				});
			}				
		}
	}
}

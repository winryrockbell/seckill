package org.lyh.seckill.web;

import java.util.Date;
import java.util.List;

import org.lyh.seckill.dto.Exposer;
import org.lyh.seckill.dto.SeckillExecution;
import org.lyh.seckill.dto.SeckillResult;
import org.lyh.seckill.entity.Seckill;
import org.lyh.seckill.enums.SeckillStateEnum;
import org.lyh.seckill.exception.RepeatKillException;
import org.lyh.seckill.exception.SeckillCloseException;
import org.lyh.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller//url:模块/资源/{参数}/细分
public class SeckillController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SeckillService seckillService;
	
	@RequestMapping(name = "/list",method = RequestMethod.GET)
	public String list(Model model){
		System.out.println("helloboy");
		//返回的字符串外加model就构成了ModelAndView
		List<Seckill> list = seckillService.getSeckillList();
		model.addAttribute("list",list);
		return "list";// /WEB-INF/jsp/list
	}
	
	@RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)  //符合Restful的规范并且传递了URL所带的参数
	public String detail(@PathVariable("seckillId")Long seckillId,Model model){
		if(seckillId == null){
			//没有参数时回到列表页
			return "redirect:/seckill/list";
		}
		Seckill seckill = seckillService.getById(seckillId);
		if(seckill == null){
			return "forward:/seckill/list";
		}
		model.addAttribute("seckill",seckill);
		return "detail";		
	}
	//ajax接口，返回的是json，不需要Model
	@RequestMapping(value = "/{seckillId}/export",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody//当springmvc遇到这个注解时会将这个方法的返回结果封装成json格式
	public SeckillResult<Exposer>  export(@PathVariable("seckillId")Long seckillId){
		SeckillResult<Exposer> result = null;
		System.out.println(seckillId);
		try {
			Exposer exposer = seckillService.exportSeckillUrl(seckillId);		
			result = new SeckillResult<Exposer>(true, exposer);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = new SeckillResult<Exposer>(false, e.getMessage());
		}
		return result;
	}
	@RequestMapping(value = "/{seckillId}/{md5}/execution",method = RequestMethod.POST,produces = {"application/json;charset=UTF-8"})
	@ResponseBody
	public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId")Long seckillId,
												   @CookieValue(value = "userPhone",required = false)Long userPhone, //这个userphone是用户标识，应该在登陆后存储在Cookie中，所以从Cookie中拿到这个标识
												   @PathVariable("md5")String md5){
		//这是手工验证，还可以采取springmvc使用的验证方式（针对于很多参数的时候)
		if(userPhone == null){
			return new SeckillResult<SeckillExecution>(false,"获取用户信息失败，请检查");
		}
		SeckillResult<SeckillExecution> result = null;
		try {
			SeckillExecution execution = seckillService.executeSeckill(seckillId, userPhone, md5);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch(RepeatKillException e){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.REPEAT_KILL);
			return new SeckillResult<SeckillExecution>(true,execution);//把异常显示给用户看，友好的交互体验
		}catch(SeckillCloseException e){
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.END);
			return new SeckillResult<SeckillExecution>(true,execution);
		}catch (Exception e) {
			SeckillExecution execution = new SeckillExecution(seckillId,SeckillStateEnum.INNER_ERROR);
			return new SeckillResult<SeckillExecution>(true,execution);
		}
	}
	@RequestMapping(value = "/time/now",method = RequestMethod.GET)
	@ResponseBody
	public SeckillResult<Long> time(){
		Date now = new Date();
		return new SeckillResult<Long>(true,now.getTime());
	}
}

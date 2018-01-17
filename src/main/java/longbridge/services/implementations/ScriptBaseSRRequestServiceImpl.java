package longbridge.services.implementations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.factory.annotation.Autowired;


import longbridge.dtos.ServiceRequestDTO;
import longbridge.dtos.SettingDTO;
import longbridge.services.ConfigurationService;
import longbridge.services.ServiceRequestFeeService;

public class ScriptBaseSRRequestServiceImpl implements ServiceRequestFeeService {

	private final String SCRIPT_PATH = "SCRIPT_PATH";
	private final String SCRIPT_SUFFIX = ".js";
	private final String HOOK_NAME = "calculator";
	private final String REQUEST_KEY = "request_key";
	@Autowired
	ConfigurationService config;
	
	@Override
	public Map<String, Number> getServiceRequestCharge(ServiceRequestDTO dto) {
		String requestName = dto.getRequestName();
		
		Map<String,Number> map = null;
		if(checkScript(requestName)) {
			Reader scriptReader = null;
			try {
				scriptReader = loadFile(requestName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
			Invocable invocable = (Invocable) engine;
			Compilable compiler = (Compilable) engine;
			try {
				CompiledScript comScript= compiler.compile(scriptReader);
				engine.put(REQUEST_KEY, dto.getBody());
				Object object = invocable.invokeFunction(HOOK_NAME);
				
				if(object instanceof Number)
				{
					map  = new HashMap();
					map.put("default", (Number)object);
				}else{
					map = (Map<String,Number>) object;
				}
			} catch (Exception e) {
			
			}
		}
		return map;
	}
	
	
	private boolean checkScript(String name){
		SettingDTO setting = config.getSettingByName(SCRIPT_PATH);
		if(setting != null && setting.isEnabled())
		{
			String path = setting.getValue();
			File file = new File(path, name);
			return file.exists();
		}
		return false;
	}
	
	private Reader loadFile(String name) throws IOException{
		SettingDTO setting = config.getSettingByName(SCRIPT_PATH);
		if(setting != null && setting.isEnabled())
		{
			String path = setting.getValue();
			File file = new File(path, name + SCRIPT_SUFFIX);
			if(file.exists())
				return new FileReader(file);
		}
		throw new FileNotFoundException("Script does not exist");
	}


}

package longbridge.trace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TraceStore {

	private List<String> trace ;

	private String id;

	public void add(String... trace){
		if(this.trace != null){
            this.trace.addAll(Arrays.asList(trace));
		}

	}

	public String getTrace() {
		return
				trace != null ? trace.stream().collect(Collectors.joining(",")) : "";
	}

	public void clear() {
		if(trace != null){
			trace.clear();
			this.trace = null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void init() {
		trace = new ArrayList();
	}
}
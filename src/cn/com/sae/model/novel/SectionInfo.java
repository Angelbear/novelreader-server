package cn.com.sae.model.novel;

import cn.com.sae.annotation.MustFilterField;
import cn.com.sae.model.CachableObject;

public class SectionInfo extends CachableObject{
	private static final long serialVersionUID = 926160435482016042L;
	
	@MustFilterField
	public String name;
	public String url;
}

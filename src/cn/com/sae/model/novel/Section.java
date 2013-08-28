package cn.com.sae.model.novel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import cn.com.sae.model.CachableObject;

@Entity
@Table(name = "sections")
public class Section extends CachableObject {
	private static final long serialVersionUID = -1911020462461950224L;

	@Id
	@Column(name = "id")
	@GenericGenerator(name = "generator", strategy = "increment")
	@GeneratedValue(generator = "generator")
	private int id;

	@Column(name = "book_id")
	private int book_id;
	
	@Column(name = "name")
	public String title;
	
	@Column(name = "text")
	public String text;
	
	@Column(name = "url")
	public String url;
	
	@Column(name = "last_update_time")
	private long last_update_time;

	public long getLast_update_time() {
		return new Date().getTime();
	}
	
}
